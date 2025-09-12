package ru.practicum.ewm.service.comment;

import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentDto;

import java.util.List;


public interface CommentService {

    CommentDto createComment(NewCommentDto newCommentDto, Long userId, Long eventId);

    void deleteComment(Long commentId, Long userId, Long eventId);

    CommentDto updateComment(UpdateCommentDto updateCommentDto, Long userId, Long eventId, Long commentId);

    CommentDto getCommentById(Long commentId);

    List<CommentDto> getAllCommentByEvent(Long eventId, Integer from, Integer size);

    List<CommentDto> getAllCommentByAuthor(Long userId, Integer from, Integer size);

    void deleteCommentModeration(Long commentId);

    CommentDto updateCommentModeration(UpdateCommentDto updateCommentDto, Long commendId);
}
