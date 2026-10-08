package com.bigobooks.services;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.bigobooks.entities.BaseEntity;
import com.bigobooks.repository.BaseRepository;

public abstract class AbstractCrudService<E extends BaseEntity, D, R extends BaseRepository<E>> {

    protected final R repository;

    protected AbstractCrudService(R repository) {
        this.repository = repository;
    }

    protected abstract Function<E, D> toDtoMapper();

    @Transactional(readOnly = true)
    public Page<D> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(toDtoMapper());
    }

    @Transactional(readOnly = true)
    public Optional<D> getById(Long id) {
        return repository.findById(id).map(toDtoMapper());
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
