package ru.practicum.dto.user;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = { "id", "email" })
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
