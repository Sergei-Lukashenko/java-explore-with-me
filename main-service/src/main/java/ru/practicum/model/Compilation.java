package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "compilations")
@Data
@EqualsAndHashCode(of = { "id", "eventDate" })
@NoArgsConstructor
@AllArgsConstructor

public class Compilation {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private Boolean pinned;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "event_compilations",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> events;
}
