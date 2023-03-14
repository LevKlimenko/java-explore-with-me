package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "hit")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column
    String app;
    @Column
    String uri;
    @Column
    String ip;
    @Column
    LocalDateTime timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hit hit = (Hit) o;
        return id == hit.id &&
                app.equals(hit.app) &&
                uri.equals(hit.uri) &&
                ip.equals(hit.ip) &&
                timestamp.equals(hit.timestamp);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, app, uri, ip, timestamp);
    }
}