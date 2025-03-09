package ru.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.storage.CommentRepository;
import ru.practicum.storage.EventRepository;
import ru.practicum.storage.UserRepository;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.CommentMapper;
import ru.practicum.enums.EventState;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CommentDto> findAllByEvent(Long eventId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.ASC, "id"));
        return commentRepository.findAllByEventId(pageable, eventId).stream()
                .map(CommentMapper.INSTANCE::toCommentDto)
                .toList();
    }

    @Override
    public CommentDto findByEventAndCommentId(Long eventId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий ID %d не найден".formatted(commentId)));
        if (!Objects.equals(eventId, comment.getEvent().getId())) {
            throw new ConflictException("Комментарий ID %d не принадлежит событию ID %d".formatted(commentId, eventId));
        }
        return CommentMapper.INSTANCE.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие ID %d не найдено".formatted(eventId)));

        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException("Событие ID %d не опубликовано".formatted(eventId));
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь ID %d не найден".formatted(userId)));

        Comment comment = CommentMapper.INSTANCE.toComment(newCommentDto);
        comment.setEvent(event);
        comment.setAuthor(user);
        comment.setCreatedOn(LocalDateTime.now());
        commentRepository.save(comment);
        return CommentMapper.INSTANCE.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto update(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto) {
        Comment comment = validateCommentForEventAndUser(eventId, commentId, userId);
        comment.setMessage(newCommentDto.getMessage());
        comment.setUpdatedOn(LocalDateTime.now());
        commentRepository.save(comment);
        return CommentMapper.INSTANCE.toCommentDto(comment);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long eventId, Long commentId) {
        Comment comment = validateCommentForEventAndUser(eventId, commentId, userId);
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public CommentDto adminUpdate(Long eventId, Long commentId, NewCommentDto newCommentDto) {
        Comment comment = validateCommentForEvent(eventId, commentId);
        comment.setMessage(newCommentDto.getMessage());
        comment.setUpdatedOn(LocalDateTime.now());
        commentRepository.save(comment);
        return CommentMapper.INSTANCE.toCommentDto(comment);
    }

    @Override
    @Transactional
    public void adminDelete(Long eventId, Long commentId) {
        Comment comment = validateCommentForEvent(eventId, commentId);
        commentRepository.delete(comment);
    }

    private Comment validateCommentForEventAndUser(Long eventId, Long commentId, Long userId) {
        Comment comment = validateCommentForEvent(eventId, commentId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь ID %d не найден".formatted(userId)));

        if (!user.equals(comment.getAuthor())) {
            throw new ConflictException("Комментарий ID %d не был создан пользователем с ID %d"
                    .formatted(comment.getAuthor().getId(), user.getId()));
        }
        return comment;
    }

    private Comment validateCommentForEvent(Long eventId, Long commentId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие ID %d не найдено".formatted(eventId)));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий ID %d не найден".formatted(commentId)));

        if (!Objects.equals(comment.getEvent().getId(), event.getId())) {
            throw new ConflictException("Комментарий ID %d не относится к событию ID %d"
                    .formatted(comment.getId(), event.getId()));
        }

        return comment;
    }
}
