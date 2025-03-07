package ru.practicum.dto.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.util.List;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "created", source = "createdOn")
    @Mapping(target = "updated", source = "updatedOn")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    Comment toComment(NewCommentDto commentDto);
}
