package ru.practicum.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.id>=?1 and u.id<=?2")
    List<User> getAllByIdIsAfterAndIdIsBeforeOrderByIdAsc(Long idsStart, Long idsEnd, Pageable pageable);

    @Query("select u from User u where u.id>=?1")
    List<User> getAllByIdIsAfterOrderById(Long idsStart, Pageable pageable);

    @Query("select u from User u group by u.id")
    Page<User> getAll(Pageable pageable);
}
