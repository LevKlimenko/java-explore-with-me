package ru.practicum.comment.model;

import lombok.*;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "created", nullable = false)
    LocalDateTime createdOn;
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    User user;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    Event event;
    @Column(nullable = false)
    String text;
    @Column(nullable = false)
    CommentStatus status;
}