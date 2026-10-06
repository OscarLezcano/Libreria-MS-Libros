package com.bigobooks.controller;

import org.springframework.beans.factory.annotation.Value;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.WarehouseDto;
import com.bigobooks.services.WarehouseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WarehouseController implements WarehousesApi {

    private final WarehouseService warehouseService;

    @Value("${app.pagination.page-size:25}")
    private int defaultPageSize;

    @Value("${app.pagination.max-page-size:100}")
    private int maxPageSize;

    @Override
    public ResponseEntity<List<WarehouseDto>> getWarehouses(String city, Integer page, Integer size) {
        return ResponseEntity.ok(warehouseService.findByCity(city, pageable(page, size)).getContent());
    }

    @Override
    public ResponseEntity<WarehouseDto> getWarehouseById(Long id) {
        return warehouseService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<WarehouseDto> createWarehouse(WarehouseDto warehouseDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(warehouseService.create(warehouseDto));
    }

    @Override
    public ResponseEntity<WarehouseDto> updateWarehouse(Long id, WarehouseDto warehouseDto) {
        return warehouseService.update(id, warehouseDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> deleteWarehouse(Long id) {
        warehouseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Pageable pageable(Integer page, Integer size) {
        int pageNo = page == null ? 0 : page;
        int pageSize = Math.min(size == null ? defaultPageSize : size, maxPageSize);
        return PageRequest.of(pageNo, pageSize);
    }
}
