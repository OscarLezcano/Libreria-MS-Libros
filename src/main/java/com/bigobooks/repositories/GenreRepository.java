package com.bigobooks.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.bigobooks.entities.book.Genre;
import com.bigobooks.repository.BaseRepository;

public interface GenreRepository extends BaseRepository<Genre> {

    Optional<Genre> findByName(String name);

    @Query("select b from Genre b where lower(b.name) like lower(concat('%', :name, '%'))")
    Page<Genre> searchByName(String name, Pageable pageable);

    @Query(value = "SELECT * FROM genre WHERE is_deleted = true", nativeQuery = true)
    List<Genre> findDeleted();

    @Query(value = "SELECT * FROM genre", nativeQuery = true)
    List<Genre> findAllIncludingDeleted();
}
