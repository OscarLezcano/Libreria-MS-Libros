package com.bigobooks.controller;

import org.springframework.beans.factory.annotation.Value;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.StockAdjustmentDto;
import com.bigobooks.services.StockAdjustmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StockAdjustmentController implements StockAdjustmentsApi {

    private final StockAdjustmentService stockAdjustmentService;

    @Value("${app.pagination.page-size:25}")
    private int defaultPageSize;

    @Value("${app.pagination.max-page-size:100}")
    private int maxPageSize;

    @Override
    public ResponseEntity<StockAdjustmentDto> getStockAdjustmentById(Long id) {
        return stockAdjustmentService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<StockAdjustmentDto>> getStockAdjustmentsByWarehouse(Long warehouseId, Integer page,
            Integer size) {
        return ResponseEntity.ok(stockAdjustmentService.getByWarehouse(warehouseId, pageable(page, size)).getContent());
    }

    @Override
    public ResponseEntity<StockAdjustmentDto> createStockAdjustment(Long warehouseId, StockAdjustmentDto stockAdjustmentDto) {
        return stockAdjustmentService.create(warehouseId, stockAdjustmentDto)
                .map(ResponseEntity.status(HttpStatus.CREATED)::body)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    private Pageable pageable(Integer page, Integer size) {
        int pageNo = page == null ? 0 : page;
        int pageSize = Math.min(size == null ? defaultPageSize : size, maxPageSize);
        return PageRequest.of(pageNo, pageSize);
    }
}
