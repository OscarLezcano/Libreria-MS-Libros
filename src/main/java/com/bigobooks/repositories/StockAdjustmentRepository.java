package com.bigobooks.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.bigobooks.entities.stock.StockAdjustment;
import com.bigobooks.repository.BaseRepository;

public interface StockAdjustmentRepository extends BaseRepository<StockAdjustment> {

    List<StockAdjustment> findByWarehouse_Id(Long warehouseId);

    List<StockAdjustment> findByDetails_Book(Long bookId);

    @Query(value = "SELECT * FROM stock_adjustments WHERE is_deleted = true", nativeQuery = true)
    List<StockAdjustment> findDeleted();

    @Query(value = "SELECT * FROM stock_adjustments", nativeQuery = true)
    List<StockAdjustment> findAllIncludingDeleted();
}
