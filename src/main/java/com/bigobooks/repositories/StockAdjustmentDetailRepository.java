package com.bigobooks.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bigobooks.entities.stock.StockAdjustmentDetail;
import com.bigobooks.repository.BaseRepository;

public interface StockAdjustmentDetailRepository extends BaseRepository<StockAdjustmentDetail> {

    @Query("select d from StockAdjustmentDetail d where d.stockAdjustment.id = :stockAdjustmentId")
    List<StockAdjustmentDetail> findByStockAdjustmentId(@Param("stockAdjustmentId") Long stockAdjustmentId);

    @Query("select d from StockAdjustmentDetail d where d.book.id = :bookId")
    List<StockAdjustmentDetail> findByBookId(@Param("bookId") Long bookId);

    @Query(value = "SELECT * FROM stock_adjustment_details WHERE is_deleted = true", nativeQuery = true)
    List<StockAdjustmentDetail> findDeleted();

    @Query(value = "SELECT * FROM stock_adjustment_details", nativeQuery = true)
    List<StockAdjustmentDetail> findAllIncludingDeleted();
}
