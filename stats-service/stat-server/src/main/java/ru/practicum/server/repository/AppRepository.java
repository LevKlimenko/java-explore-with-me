package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.model.App;

public interface AppRepository extends JpaRepository<App, Long> {
    App findByName(String name);
}
