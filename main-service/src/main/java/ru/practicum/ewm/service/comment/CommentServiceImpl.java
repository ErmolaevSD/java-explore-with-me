package ru.practicum.ewm.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.entity.comment.Comment;
import ru.practicum.ewm.entity.event.Event;
import ru.practicum.ewm.entity.event.EventState;
import ru.practicum.ewm.entity.user.User;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.repository.comment.CommentRepository;
import ru.practicum.ewm.service.event.EventPublicService;
import ru.practicum.ewm.service.user.UserService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final UserService userService;
    private final EventPublicService eventPublicService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;


    @Override
    @Transactional
    public CommentDto createComment(NewCommentDto newCommentDto, Long userId, Long eventId) {
        User user = userService.getById(userId);
        Event event = eventPublicService.getById(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя комментировать неопубликованное событие");
        }

        Comment comment = Comment.builder()
                .id(null)
                .text(newCommentDto.getText())
                .author(user)
                .event(event)
                .build();
        Comment savedComment = commentRepository.save(comment);
        log.info("Комментарий {} успешно сохранен", savedComment);
        return commentMapper.toCommentDto(savedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId, Long eventId) {
        userService.getById(userId);
        eventPublicService.getById(eventId);
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new ConflictException("Указанного комментария с id {} не существует", commentId.toString());
        }

        if (!comment.get().getAuthor().getId().equals(userId)) {
            throw new ConflictException("Удалить комментарий может только пользователь которые его оставил или администрация");
        }

        commentRepository.deleteById(commentId);
        log.info("Комментарий {} успешно удален", comment.get().getText());
    }


    @Override
    @Transactional
    public CommentDto updateComment(UpdateCommentDto updateCommentDto, Long userId, Long eventId, Long commentId) {
        userService.getById(userId);
        eventPublicService.getById(eventId);
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new ConflictException("Указанного комментария с id {} не существует", commentId.toString());
        }

        if (!comment.get().getAuthor().getId().equals(userId)) {
            throw new ConflictException("Изменить комментарий может только автор или администрация");
        }

        commentMapper.updateCommentFromRequest(updateCommentDto, comment.get());
        Comment updateComment = commentRepository.save(comment.get());
        log.info("Комментарий {} успешно обновлен", comment.get());
        return commentMapper.toCommentDto(updateComment);
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new NotFoundException("Комментария с указанным id {} не существует", commentId);
        }
        return commentMapper.toCommentDto(comment.get());
    }

    @Override
    public List<CommentDto> getAllCommentByAuthor(Long userId, Integer from, Integer size) {

        Pageable pageable = PageRequest.of(from / size, size);
        userService.getById(userId);
        List<Comment> commentList = commentRepository.findByAuthorId(userId, pageable);

        return commentList.stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    @Override
    public void deleteCommentModeration(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new ConflictException("Указанного комментария с id {} не существует", commentId.toString());
        }
        commentRepository.deleteById(commentId);
        log.info("Комментарий {} успешно удален администрацией", comment.get());
    }

    @Override
    @Transactional
    public CommentDto updateCommentModeration(UpdateCommentDto updateCommentDto, Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new ConflictException("Указанного комментария с id {} не существует", commentId.toString());
        }
        commentMapper.updateCommentFromRequest(updateCommentDto, comment.get());
        Comment updateComment = commentRepository.save(comment.get());
        log.info("Комментарий {} успешно обновлен администрацией", comment.get());
        return commentMapper.toCommentDto(updateComment);
    }

    @Override
    public List<CommentDto> getAllCommentByEvent(Long eventId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        List<Comment> commentsByEvent = commentRepository.findAllByEventId(eventId, pageable);

        if (commentsByEvent.isEmpty()) {
            throw new ConflictException("Комментарии не найдены");
        }
        return commentsByEvent.stream()
                .map(commentMapper::toCommentDto).toList();
    }
}