package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.enums.RequestStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
@Builder
@EqualsAndHashCode(of = { "id", "userId" })
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Event.class)
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
