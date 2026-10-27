package com.bigobooks.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bigobooks.entities.stock.StockAdjustment;
import com.bigobooks.repository.BaseRepository;

public interface StockAdjustmentRepository extends BaseRepository<StockAdjustment> {

    @Query("select a from StockAdjustment a where a.warehouse.id = :warehouseId")
    Page<StockAdjustment> findByWarehouseId(@Param("warehouseId") Long warehouseId, Pageable pageable);

    @Query("select distinct a from StockAdjustment a join a.details d where d.book.id = :bookId")
    Page<StockAdjustment> findByBookId(@Param("bookId") Long bookId, Pageable pageable);

    @Query(value = "SELECT * FROM stock_adjustments WHERE is_deleted = true", nativeQuery = true)
    List<StockAdjustment> findDeleted();

    @Query(value = "SELECT * FROM stock_adjustments", nativeQuery = true)
    List<StockAdjustment> findAllIncludingDeleted();
}
