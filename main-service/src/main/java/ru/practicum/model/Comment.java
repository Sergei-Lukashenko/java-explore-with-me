package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@Builder
@EqualsAndHashCode(of = { "id" })
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Event.class)
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;

    @Column(name = "message")
    private String message;

    @Column(name = "created_on")
    private  LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

}
