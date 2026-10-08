package com.bigobooks.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.bigobooks.entities.book.Author;
import com.bigobooks.repository.BaseRepository;

public interface AuthorRepository extends BaseRepository<Author> {

    Optional<Author> findByName(String name);

    @Query("select b from Author b where lower(b.name) like lower(concat('%', :name, '%'))")
    Page<Author> searchByName(String name, Pageable pageable);

    @Query(value = "SELECT * FROM author WHERE is_deleted = true", nativeQuery = true)
    List<Author> findDeleted();

    @Query(value = "SELECT * FROM author", nativeQuery = true)
    List<Author> findAllIncludingDeleted();
}
