package com.bigobooks.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bigobooks.entities.stock.Stock;
import com.bigobooks.repository.BaseRepository;

import jakarta.persistence.LockModeType;

public interface StockRepository extends BaseRepository<Stock> {

    @Query("select s from Stock s where s.book.id = :bookId and s.warehouse.id = :warehouseId")
    Optional<Stock> findByBookIdAndWarehouseId(@Param("bookId") Long bookId, @Param("warehouseId") Long warehouseId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.book.id = :bookId and s.warehouse.id = :warehouseId")
    Optional<Stock> findByBookIdAndWarehouseIdForUpdate(@Param("bookId") Long bookId,
            @Param("warehouseId") Long warehouseId);

    @Query("select s from Stock s where s.book.id = :bookId")
    Page<Stock> findByBookId(@Param("bookId") Long bookId, Pageable pageable);

    @Query("select s from Stock s where s.warehouse.id = :warehouseId")
    Page<Stock> findByWarehouseId(@Param("warehouseId") Long warehouseId, Pageable pageable);

    @Query("select coalesce(sum(s.quantity), 0) from Stock s where s.book.id = :bookId and s.warehouse.id = :warehouseId")
    int sumQuantityByBookIdAndWarehouseId(@Param("bookId") Long bookId, @Param("warehouseId") Long warehouseId);

    @Query(value = "SELECT * FROM stock WHERE is_deleted = true", nativeQuery = true)
    List<Stock> findDeleted();

    @Query(value = "SELECT * FROM stock", nativeQuery = true)
    List<Stock> findAllIncludingDeleted();
}
