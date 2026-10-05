package com.bigobooks.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.bigobooks.entities.stock.StockAdjustmentDetail;
import com.bigobooks.repository.BaseRepository;

public interface StockAdjustmentDetailRepository extends BaseRepository<StockAdjustmentDetail> {

    List<StockAdjustmentDetail> findByStockAdjustment_Id(Long stockAdjustmentId);

    List<StockAdjustmentDetail> findByBook(Long book);

    @Query(value = "SELECT * FROM stock_adjustment_details WHERE is_deleted = true", nativeQuery = true)
    List<StockAdjustmentDetail> findDeleted();

    @Query(value = "SELECT * FROM stock_adjustment_details", nativeQuery = true)
    List<StockAdjustmentDetail> findAllIncludingDeleted();
}
