package ru.practicum.ewm.controller.comment;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.service.comment.CommentService;

import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/comments")
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@Valid @RequestBody NewCommentDto newCommentDto,
                                    @PathVariable Long userId,
                                    @PathVariable Long eventId) {
        log.info("Получен запрос на создание комментарий {} пользователем c id {} к событию с id {} ", newCommentDto, userId, eventId);
        return commentService.createComment(newCommentDto, userId, eventId);
    }

    @DeleteMapping("/{eventId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long eventId,
                              @PathVariable Long commentId,
                              @PathVariable Long userId) {
        log.info("Получен запрос на удаление комментария с id {} у события с id {}", commentId, eventId);
        commentService.deleteComment(commentId, userId, eventId);
    }

    @PatchMapping("/{eventId}/{commentId}")
    public CommentDto updateComment(@Valid @RequestBody UpdateCommentDto updateCommentDto,
                                    @PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @PathVariable Long commentId) {
        log.info("Получен запрос на обновление комментария с id {} пользователем с id {} события с id {}", commentId, userId, eventId);
        return commentService.updateComment(updateCommentDto, userId, eventId, commentId);
    }

    @GetMapping("/comment/{commentId}")
    public CommentDto getCommentById(@PathVariable Long userId,
                                     @PathVariable Long commentId) {
        log.info("Получен запрос на поиск комментария с id {}", commentId);
        return commentService.getCommentById(commentId);
    }

    @GetMapping("/{eventId}")
    public List<CommentDto> getCommentByEvent(@PathVariable Long eventId,
                                              @RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение всех комментариев к событию с {}", eventId);
        return commentService.getAllCommentByEvent(eventId, from, size);
    }

    @GetMapping
    public List<CommentDto> getCommentByAuthor(@PathVariable Long userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение всех комментариев пользователя с if {}", userId);
        return commentService.getAllCommentByAuthor(userId, from, size);
    }
}