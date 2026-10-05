package com.bigobooks.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bigobooks.entities.book.Book;
import com.bigobooks.repository.BaseRepository;

public interface BookRepository extends BaseRepository<Book> {

    Optional<Book> findByTitle(String title);

    List<Book> findByGenres_Id(Long genreId);

    List<Book> findByAuthors_Id(Long authorId);

    @Query("select b from Book b where lower(b.title) like lower(concat('%', :title, '%'))")
    List<Book> searchByTitle(@Param("title") String title);

    @Query(value = "SELECT * FROM book WHERE is_deleted = true", nativeQuery = true)
    List<Book> findDeleted();

    @Query(value = "SELECT * FROM book", nativeQuery = true)
    List<Book> findAllIncludingDeleted();
}
