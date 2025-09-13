package ru.practicum.ewm.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.UpdateCommentDto;
import ru.practicum.ewm.service.comment.CommentService;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class CommentAdminController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public CommentDto getCommentForModeration(@PathVariable Long commentId) {
        log.info("Получение комментария с id {} для модерации", commentId);
        return commentService.getCommentById(commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByModeration(@PathVariable Long commentId) {
        log.info("Получен запрос на удаление комментария с id {} администрацией", commentId);
        commentService.deleteCommentModeration(commentId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateCommentByModeration(@RequestBody @Validated UpdateCommentDto updateCommentDto,
                                                @PathVariable Long commentId) {
        log.info("Получен запрос на модерацию комментария с id {} администрацией", commentId);
        return commentService.updateCommentModeration(updateCommentDto, commentId);
    }
}