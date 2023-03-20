package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Sort SORT_BY_DESC = Sort.by(Sort.Direction.DESC, "id");
    private static final Sort SORT_BY_ASC = Sort.by(Sort.Direction.ASC, "id");
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto save(User user) {
        try {
            return UserMapper.toUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("User with email: " + user.getEmail() + " is already exist.");
        }
    }

    @Override
    public UserDto update(Long id, User user) {
        try {
            return UserMapper.toUserDto(checkUpdate(id, user));
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("User with email: " + user.getEmail() + " is already exist.");
        }
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        User user = UserMapper.toUser(findById(id));
        userRepository.deleteById(user.getId());
        return true;
    }

    @Override
    public UserDto findById(Long id) {
        return UserMapper.toUserDto(userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("User with ID=" + id + " not found")));
    }

    @Override
    public List<UserDto> findAll(List<String> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, SORT_BY_ASC);
        if (ids == null || ids.isEmpty()) {
            return UserMapper.toListUserDto(userRepository.getAll(pageable).getContent());
        } else if (ids.size() == 1) {
            long idsFrom = parseAndCheckIds(ids.get(0));
            return UserMapper.toListUserDto(userRepository.getAllByIdIsAfterOrderById(idsFrom, pageable));
        } else {
            long idsStart = parseAndCheckIds(ids.get(0));
            long idsEnd = parseAndCheckIds(ids.get(1));
            if (idsStart > idsEnd) {
                throw new BadRequestException("ids Start should be greater than idsEnd. idsStart=" + idsStart +
                        "  idsEnd=" + idsEnd);
            }
            return UserMapper.toListUserDto(
                    userRepository.getAllByIdIsAfterAndIdIsBeforeOrderByIdAsc(idsStart, idsEnd, pageable));
        }
    }

    private User checkUpdate(Long id, User user) {
        User findUser = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("User with ID=" + id + " not found"));
        if (user.getName() != null && !user.getName().isBlank()) {
            findUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            findUser.setEmail(user.getEmail());
        }
        return findUser;
    }

    private Long parseAndCheckIds(String ids) {
        long parsedIds = Optional.of(Long.parseLong(ids)).orElseThrow(
                () -> new BadRequestException("ids must be Number"));
        if (parsedIds < 0) {
            throw new BadRequestException("Ids should be more 0");
        }
        return parsedIds;
    }
}
