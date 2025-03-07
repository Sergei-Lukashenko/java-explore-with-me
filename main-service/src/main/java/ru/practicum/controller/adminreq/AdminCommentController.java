package ru.practicum.controller.adminreq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.service.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;

    @PatchMapping("/{eventId}/comments/{comId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long eventId,
                                                    @PathVariable Long comId,
                                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Поступил Admin-запрос на обновление комментария ID {} для события ID {}", comId, eventId);
        return ResponseEntity.ok().body(commentService.adminUpdate(eventId, comId, newCommentDto));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{eventId}/comments/{comId}")
    public ResponseEntity deleteComment(@PathVariable Long eventId,
                                        @PathVariable Long comId) {
        log.info("Поступил Admin-запрос на удаление комментария ID {} для события {}", comId, eventId);
        commentService.adminDelete(eventId, comId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long eventId,
                                                        @RequestParam(name = "from", defaultValue = "0") int from,
                                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Получен Admin-запрос списка комментариев по событию ID {}", eventId);
        return ResponseEntity.ok().body(commentService.getAll(eventId, from, size));
    }
}
