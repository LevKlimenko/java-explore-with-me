package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserRequestDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.service.UserService;
import ru.practicum.validation.Create;
import ru.practicum.validation.Update;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/admin/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Validated(Create.class) UserRequestDto user) {
        UserDto addedUser = userService.save(UserMapper.toUser(user));
        log.info("The user have been add, UserID={}", addedUser.getId());
        return new ResponseEntity<>(addedUser, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @Validated(Update.class)
    @RequestBody UserRequestDto userRequestDto) {
        UserDto updateUser = userService.update(id, UserMapper.toUser(userRequestDto));
        log.info("The user have been update, UserID={}", updateUser.getId());
        return new ResponseEntity<>(updateUser, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestParam(required = false) List<String> ids,
                                         @Valid @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @Valid @RequestParam(defaultValue = "10") @Positive int size) {
        List<UserDto> users = userService.findAll(ids, from, size);
        log.info("The list of all users has been received");
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        UserDto user = userService.findById(id);
        log.info("User with id={} have been received", id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        userService.deleteById(id);
        log.info("User with id={} have been deleted", id);
        return new ResponseEntity<>("User with id=" + id + " have been deleted", HttpStatus.NO_CONTENT);
    }
}
