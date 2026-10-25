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

import com.bigobooks.dto.AuthorDto;
import com.bigobooks.dto.AuthorRequestDto;
import com.bigobooks.entities.book.Author;
import com.bigobooks.mappers.AuthorMapper;
import com.bigobooks.repositories.AuthorRepository;
import com.bigobooks.specifications.AuthorSpecifications;

@Service
@Slf4j
public class AuthorService extends AbstractCrudService<Author, AuthorDto, AuthorRepository> {

    private final AuthorMapper authorMapper;
    private final AuthorSpecifications authorSpecifications;
    private final Cache cache;

    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper,
            AuthorSpecifications authorSpecifications, CacheManager cacheManager) {
        super(authorRepository);
        this.authorMapper = authorMapper;
        this.authorSpecifications = authorSpecifications;
        this.cache = cacheManager.getCache("author");
    }

    @Override
    protected Function<Author, AuthorDto> toDtoMapper() {
        return authorMapper::toDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuthorDto> getAll(Pageable pageable) {
        Page<Long> idPage = repository.findAllIds(pageable);
        ArrayList<AuthorDto> authors = new ArrayList<>(idPage.getNumberOfElements());

        for (Long id : idPage.getContent()) {
            String cacheKey = "author_" + id;
            AuthorDto cached = cache.get(cacheKey, AuthorDto.class);
            log.debug("Cache GET cache=author key={} result={}", cacheKey, cached == null ? "MISS" : "HIT");

            if (cached != null) {
                authors.add(cached);
                continue;
            }

            getById(id).ifPresent(author -> {
                cache.put(cacheKey, author);
                log.debug("Cache PUT cache=author key={}", cacheKey);
                authors.add(author);
            });
        }

        return new PageImpl<>(authors, pageable, idPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "author", key = "'author_' + #id")
    public Optional<AuthorDto> getById(Long id) {
        return super.getById(id);
    }

    @Transactional(readOnly = true)
    public Optional<AuthorDto> findByName(String name) {
        return repository.findByName(name).map(authorMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<AuthorDto> search(String name, Pageable pageable) {
        if (!StringUtils.hasText(name)) {
            return getAll(pageable);
        }
        return repository.searchByName(name, pageable).map(authorMapper::toDto);
    }

    @Transactional
    @CachePut(value = "author", key = "'author_' + #result.id", condition = "#result.id != null")
    public AuthorDto create(AuthorRequestDto dto) {
        authorSpecifications.validateCreate(dto);
        Author author = new Author();
        author.setName(dto.getName());
        return authorMapper.toDto(repository.save(author));
    }

    @Transactional
    @CachePut(value = "author", key = "'author_' + #id", condition = "#result != null && #result.isPresent()")
    public Optional<AuthorDto> update(Long id, AuthorRequestDto dto) {
        authorSpecifications.validateUpdate(dto);
        return repository.findById(id).map(author -> {
            authorMapper.update(dto, author);
            return authorMapper.toDto(repository.save(author));
        });
    }

    @Override
    @Transactional
    @CacheEvict(value = "author", key = "'author_' + #id")
    public void delete(Long id) {
        super.delete(id);
    }
}
