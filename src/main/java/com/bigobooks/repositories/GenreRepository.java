package com.bigobooks.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.bigobooks.entities.book.Genre;
import com.bigobooks.repository.BaseRepository;

public interface GenreRepository extends BaseRepository<Genre> {

    Optional<Genre> findByName(String name);

    List<Genre> findByNameContainingIgnoreCase(String name);

    @Query(value = "SELECT * FROM genre WHERE is_deleted = true", nativeQuery = true)
    List<Genre> findDeleted();

    @Query(value = "SELECT * FROM genre", nativeQuery = true)
    List<Genre> findAllIncludingDeleted();
}
