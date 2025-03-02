package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.enums.EventState;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@Builder
@EqualsAndHashCode(of = { "id", "eventDate" })
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Category.class)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @Column(name = "confirmed_requests")
    private Long confirmedRequests;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Location.class)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    private EventState state;

    @Column
    private String title;

    @Column
    private Long views;
}
