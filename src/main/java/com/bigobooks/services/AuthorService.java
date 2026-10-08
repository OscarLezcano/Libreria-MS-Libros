package com.bigobooks.services;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bigobooks.dto.AuthorDto;
import com.bigobooks.entities.book.Author;
import com.bigobooks.mappers.AuthorMapper;
import com.bigobooks.repositories.AuthorRepository;

@Service
public class AuthorService extends AbstractCrudService<Author, AuthorDto, AuthorRepository> {

    private final AuthorMapper authorMapper;

    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        super(authorRepository);
        this.authorMapper = authorMapper;
    }

    @Override
    protected Function<Author, AuthorDto> toDtoMapper() {
        return authorMapper::toDto;
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
    public AuthorDto create(AuthorDto dto) {
        Author author = new Author();
        author.setName(dto.getName());
        return authorMapper.toDto(repository.save(author));
    }

    @Transactional
    public Optional<AuthorDto> update(Long id, AuthorDto dto) {
        return repository.findById(id).map(author -> {
            authorMapper.update(dto, author);
            return authorMapper.toDto(repository.save(author));
        });
    }
}
