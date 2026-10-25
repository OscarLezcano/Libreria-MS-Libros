package com.bigobooks.services;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

import com.bigobooks.dto.GenreDto;
import com.bigobooks.dto.GenreRequestDto;
import com.bigobooks.entities.book.Genre;
import com.bigobooks.mappers.GenreMapper;
import com.bigobooks.repositories.GenreRepository;
import com.bigobooks.specifications.GenreSpecifications;

@Service
@Slf4j
public class GenreService extends AbstractCrudService<Genre, GenreDto, GenreRepository> {

    private final GenreMapper genreMapper;
    private final GenreSpecifications genreSpecifications;
    private final Cache cache;

    public GenreService(GenreRepository genreRepository, GenreMapper genreMapper,
            GenreSpecifications genreSpecifications, CacheManager cacheManager) {
        super(genreRepository);
        this.genreMapper = genreMapper;
        this.genreSpecifications = genreSpecifications;
        this.cache = cacheManager.getCache("genre");
    }

    @Override
    protected Function<Genre, GenreDto> toDtoMapper() {
        return genreMapper::toDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GenreDto> getAll(Pageable pageable) {
        Page<Long> idPage = repository.findAllIds(pageable);
        ArrayList<GenreDto> genres = new ArrayList<>(idPage.getNumberOfElements());

        for (Long id : idPage.getContent()) {
            String cacheKey = "genre_" + id;
            GenreDto cached = cache.get(cacheKey, GenreDto.class);
            log.debug("Cache GET cache=genre key={} result={}", cacheKey, cached == null ? "MISS" : "HIT");

            if (cached != null) {
                genres.add(cached);
                continue;
            }

            getById(id).ifPresent(genre -> {
                cache.put(cacheKey, genre);
                log.debug("Cache PUT cache=genre key={}", cacheKey);
                genres.add(genre);
            });
        }

        return new PageImpl<>(genres, pageable, idPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "genre", key = "'genre_' + #id")
    public Optional<GenreDto> getById(Long id) {
        return super.getById(id);
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
        return repository.searchByName(name, pageable).map(genreMapper::toDto);
    }

    @Transactional
    @CachePut(value = "genre", key = "'genre_' + #result.id", condition = "#result.id != null")
    public GenreDto create(GenreRequestDto dto) {
        genreSpecifications.validateCreate(dto);
        Genre genre = new Genre();
        genre.setName(dto.getName());
        return genreMapper.toDto(repository.save(genre));
    }

    @Transactional
    @CachePut(value = "genre", key = "'genre_' + #id", condition = "#result != null && #result.isPresent()")
    public Optional<GenreDto> update(Long id, GenreRequestDto dto) {
        genreSpecifications.validateUpdate(dto);
        return repository.findById(id).map(genre -> {
            genreMapper.update(dto, genre);
            return genreMapper.toDto(repository.save(genre));
        });
    }

    @Override
    @Transactional
    @CacheEvict(value = "genre", key = "'genre_' + #id")
    public void delete(Long id) {
        super.delete(id);
    }
}
