package com.bigobooks.services;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bigobooks.dto.GenreDto;
import com.bigobooks.entities.book.Genre;
import com.bigobooks.mappers.GenreMapper;
import com.bigobooks.repositories.GenreRepository;

@Service
public class GenreService extends AbstractCrudService<Genre, GenreDto, GenreRepository> {

    private final GenreMapper genreMapper;

    public GenreService(GenreRepository genreRepository, GenreMapper genreMapper) {
        super(genreRepository);
        this.genreMapper = genreMapper;
    }

    @Override
    protected Function<Genre, GenreDto> toDtoMapper() {
        return genreMapper::toDto;
    }

    @Transactional(readOnly = true)
    public Optional<GenreDto> findByName(String name) {
        return repository.findByName(name).map(genreMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<GenreDto> search(String name, Pageable pageable) {
        if (!StringUtils.hasText(name)) {
            return getAll(pageable);
        }
        return repository.findByNameContainingIgnoreCase(name, pageable).map(genreMapper::toDto);
    }

    @Transactional
    public GenreDto create(GenreDto dto) {
        Genre genre = new Genre();
        genre.setName(dto.getName());
        return genreMapper.toDto(repository.save(genre));
    }

    @Transactional
    public Optional<GenreDto> update(Long id, GenreDto dto) {
        return repository.findById(id).map(genre -> {
            genreMapper.update(dto, genre);
            return genreMapper.toDto(repository.save(genre));
        });
    }
}
