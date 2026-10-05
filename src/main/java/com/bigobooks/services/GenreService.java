package com.bigobooks.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bigobooks.dto.GenreDto;
import com.bigobooks.entities.book.Genre;
import com.bigobooks.mappers.GenreMapper;
import com.bigobooks.repositories.GenreRepository;
import com.bigobooks.service.BaseService;

@Service
public class GenreService extends BaseService<Genre, GenreRepository> {

    private final GenreMapper genreMapper;

    public GenreService(GenreRepository genreRepository, GenreMapper genreMapper) {
        super(genreRepository);
        this.genreMapper = genreMapper;
    }

    @Transactional(readOnly = true)
    public List<GenreDto> getAll() {
        return genreMapper.toDtoList(findAll());
    }

    @Transactional(readOnly = true)
    public Optional<GenreDto> getById(Long id) {
        return findById(id).map(genreMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<GenreDto> findByName(String name) {
        return getRepository().findByName(name).map(genreMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<GenreDto> search(String name) {
        if (!StringUtils.hasText(name)) {
            return getAll();
        }
        return genreMapper.toDtoList(getRepository().findByNameContainingIgnoreCase(name));
    }

    @Transactional
    public GenreDto create(GenreDto dto) {
        Genre genre = new Genre();
        genre.setName(dto.getName());
        return genreMapper.toDto(save(genre));
    }

    @Transactional
    public Optional<GenreDto> update(Long id, GenreDto dto) {
        return findById(id).map(genre -> {
            genreMapper.update(dto, genre);
            return genreMapper.toDto(save(genre));
        });
    }

    @Transactional
    public void delete(Long id) {
        findById(id).ifPresent(genre -> deleteById(genre.getId()));
    }
}