package com.bigobooks.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.bigobooks.entities.stock.StockAdjustment;
import com.bigobooks.repository.BaseRepository;

public interface StockAdjustmentRepository extends BaseRepository<StockAdjustment> {

    Page<StockAdjustment> findByWarehouse_Id(Long warehouseId, Pageable pageable);

    Page<StockAdjustment> findByDetails_Book(Long bookId, Pageable pageable);

    @Query(value = "SELECT * FROM stock_adjustments WHERE is_deleted = true", nativeQuery = true)
    List<StockAdjustment> findDeleted();

    @Query(value = "SELECT * FROM stock_adjustments", nativeQuery = true)
    List<StockAdjustment> findAllIncludingDeleted();
}
