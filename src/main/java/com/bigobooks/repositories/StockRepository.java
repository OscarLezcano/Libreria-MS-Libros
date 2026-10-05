package com.bigobooks.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bigobooks.entities.stock.Stock;
import com.bigobooks.repository.BaseRepository;

import jakarta.persistence.LockModeType;

public interface StockRepository extends BaseRepository<Stock> {

    Optional<Stock> findByBookIdAndWarehouse_Id(Long bookId, Long warehouseId);

    List<Stock> findByBookId(Long bookId);

    List<Stock> findByWarehouse_Id(Long warehouseId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.bookId = :bookId and s.warehouse.id = :warehouseId")
    Optional<Stock> findForUpdateByBookIdAndWarehouseId(@Param("bookId") Long bookId,
            @Param("warehouseId") Long warehouseId);

    @Query("select coalesce(sum(s.quantity), 0) from Stock s where s.bookId = :bookId")
    int sumQuantityByBookId(@Param("bookId") Long bookId);

    @Query("select coalesce(sum(s.quantity), 0) from Stock s where s.bookId = :bookId and s.warehouse.id = :warehouseId")
    int sumQuantityByBookIdAndWarehouseId(@Param("bookId") Long bookId, @Param("warehouseId") Long warehouseId);

    @Query(value = "SELECT * FROM stock WHERE is_deleted = true", nativeQuery = true)
    List<Stock> findDeleted();

    @Query(value = "SELECT * FROM stock", nativeQuery = true)
    List<Stock> findAllIncludingDeleted();
}
