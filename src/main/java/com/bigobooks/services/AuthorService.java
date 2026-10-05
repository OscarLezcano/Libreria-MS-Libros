package com.bigobooks.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bigobooks.dto.AuthorDto;
import com.bigobooks.entities.book.Author;
import com.bigobooks.mappers.AuthorMapper;
import com.bigobooks.repositories.AuthorRepository;
import com.bigobooks.service.BaseService;

@Service
public class AuthorService extends BaseService<Author, AuthorRepository> {

    private final AuthorMapper authorMapper;

    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        super(authorRepository);
        this.authorMapper = authorMapper;
    }

    @Transactional(readOnly = true)
    public List<AuthorDto> getAll() {
        return authorMapper.toDtoList(findAll());
    }

    @Transactional(readOnly = true)
    public Optional<AuthorDto> getById(Long id) {
        return findById(id).map(authorMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<AuthorDto> findByName(String name) {
        return getRepository().findByName(name).map(authorMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<AuthorDto> search(String name) {
        if (!StringUtils.hasText(name)) {
            return getAll();
        }
        return authorMapper.toDtoList(getRepository().findByNameContainingIgnoreCase(name));
    }

    @Transactional
    public AuthorDto create(AuthorDto dto) {
        Author author = new Author();
        author.setName(dto.getName());
        return authorMapper.toDto(save(author));
    }

    @Transactional
    public Optional<AuthorDto> update(Long id, AuthorDto dto) {
        return findById(id).map(author -> {
            authorMapper.update(dto, author);
            return authorMapper.toDto(save(author));
        });
    }

    @Transactional
    public void delete(Long id) {
        findById(id).ifPresent(author -> deleteById(author.getId()));
    }
}