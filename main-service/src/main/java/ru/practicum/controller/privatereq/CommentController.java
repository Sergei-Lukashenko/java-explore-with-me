package ru.practicum.controller.privatereq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.service.CommentService;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{userId}/events/{eventId}/comments")
    public ResponseEntity<CommentDto> addNewComment(@PathVariable Long userId,
                                                    @PathVariable Long eventId,
                                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Сохранение комментария для пользователя ID {} и события ID {}", userId, eventId);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(userId, eventId, newCommentDto));
    }

    @PatchMapping("/{userId}/events/{eventId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long userId,
                                                    @PathVariable Long eventId,
                                                    @PathVariable Long commentId,
                                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Обновление комментария ID {} для пользователя ID {} и события ID {}", commentId, userId, eventId);
        return ResponseEntity.ok().body(commentService.update(userId, eventId, commentId, newCommentDto));
    }

    @DeleteMapping("/{userId}/events/{eventId}/comments/{commentId}")
    public ResponseEntity deleteComment(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @PathVariable Long commentId) {
        log.info("Удаление комментария ID {} для пользователя ID {} и события ID {}", commentId, userId, eventId);
        commentService.delete(userId, eventId, commentId);
        return ResponseEntity.noContent().build();
    }
}
