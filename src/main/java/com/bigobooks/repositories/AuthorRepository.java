package com.bigobooks.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.bigobooks.entities.book.Author;
import com.bigobooks.repository.BaseRepository;

public interface AuthorRepository extends BaseRepository<Author> {

    Optional<Author> findByName(String name);

    List<Author> findByNameContainingIgnoreCase(String name);

    @Query(value = "SELECT * FROM author WHERE is_deleted = true", nativeQuery = true)
    List<Author> findDeleted();

    @Query(value = "SELECT * FROM author", nativeQuery = true)
    List<Author> findAllIncludingDeleted();
}
