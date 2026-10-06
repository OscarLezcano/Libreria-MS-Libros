package com.bigobooks.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bigobooks.entities.book.Book;
import com.bigobooks.repository.BaseRepository;

public interface BookRepository extends BaseRepository<Book> {

    Optional<Book> findByTitle(String title);

    Page<Book> findByGenres_Id(Long genreId, Pageable pageable);

    Page<Book> findByAuthors_Id(Long authorId, Pageable pageable);

    @Query("select b from Book b where lower(b.title) like lower(concat('%', :title, '%'))")
    Page<Book> searchByTitle(@Param("title") String title, Pageable pageable);

    @Query(value = "SELECT * FROM book WHERE is_deleted = true", nativeQuery = true)
    List<Book> findDeleted();

    @Query(value = "SELECT * FROM book", nativeQuery = true)
    List<Book> findAllIncludingDeleted();
}
