package ru.practicum.compilation.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.QCompilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository repository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto save(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toNewCompilation(newCompilationDto);
        compilation.setEvents(new HashSet<>(eventRepository.getEventByIdIn(newCompilationDto.getEvents())));
        return CompilationMapper.toCompilationDto(repository.save(compilation));
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = checkAndUpdateCompilation(compId, updateCompilationRequest);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public Boolean delete(Long compId) {
        getCompilationOrElseThrowNotFound(compId);
        repository.deleteById(compId);
        return true;
    }

    @Override
    public CompilationDto getById(Long compId) {
        return CompilationMapper.toCompilationDto(getCompilationOrElseThrowNotFound(compId));
    }

    @Override
    public List<CompilationDto> getByParameters(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        QCompilation qCompilation = QCompilation.compilation;
        BooleanExpression expression = qCompilation.pinned.eq(pinned);
        List<Compilation> compilations = repository.findAll(expression, pageable).getContent();
        return CompilationMapper.toListCompilationDto(compilations);
    }

    private Compilation getCompilationOrElseThrowNotFound(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException("Compilation with id=" + id + " not found"));
    }

    private Compilation checkAndUpdateCompilation(Long id, UpdateCompilationRequest request) {
        Compilation oldComp = getCompilationOrElseThrowNotFound(id);
        if (request.getTitle() != null) {
            oldComp.setTitle(request.getTitle());
        }
        if (request.getPinned() != null) {
            oldComp.setPinned(request.getPinned());
        }
        if (request.getEvents() != null) {
            oldComp.setEvents(new HashSet<>(eventRepository.getEventByIdIn(request.getEvents())));
        }
        return oldComp;
    }
}