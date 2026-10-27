package com.bigobooks.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.bigobooks.dto.WarehouseDto;
import com.bigobooks.dto.WarehouseListResponse;
import com.bigobooks.dto.WarehouseRequestDto;
import com.bigobooks.dto.WarehouseResponse;
import com.bigobooks.services.WarehouseService;
import com.bigobooks.util.Envelopes;
import com.bigobooks.util.Pageables;
import com.bigobooks.util.exception.NotFoundException;
import com.bigobooks.util.exception.ResourceType;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WarehouseController implements WarehousesApi {

    private final WarehouseService warehouseService;
    private final Pageables pageables;

    @Override
    @PreAuthorize("@roles.allows('warehouses-view')")
    public ResponseEntity<WarehouseListResponse> getWarehouses(String city, Integer page, Integer size) {
        Page<WarehouseDto> result = warehouseService.findByCity(city, pageables.of(page, size));
        return ResponseEntity.ok(new WarehouseListResponse()
                .data(result.getContent())
                .pagination(Envelopes.pagination(result)));
    }

    @Override
    @PreAuthorize("@roles.allows('warehouses-view')")
    public ResponseEntity<WarehouseResponse> getWarehouseById(Long id) {
        return warehouseService.getById(id)
                .map(warehouse -> ResponseEntity.ok(new WarehouseResponse().data(warehouse)))
                .orElseThrow(() -> NotFoundException.byId(ResourceType.WAREHOUSE, id));
    }

    @Override
    @PreAuthorize("@roles.allows('warehouses-manage')")
    public ResponseEntity<WarehouseResponse> createWarehouse(WarehouseRequestDto warehouseRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new WarehouseResponse().data(warehouseService.create(warehouseRequestDto)));
    }

    @Override
    @PreAuthorize("@roles.allows('warehouses-manage')")
    public ResponseEntity<WarehouseResponse> updateWarehouse(Long id, WarehouseRequestDto warehouseRequestDto) {
        return warehouseService.update(id, warehouseRequestDto)
                .map(warehouse -> ResponseEntity.ok(new WarehouseResponse().data(warehouse)))
                .orElseThrow(() -> NotFoundException.byId(ResourceType.WAREHOUSE, id));
    }

    @Override
    @PreAuthorize("@roles.allows('warehouses-delete')")
    public ResponseEntity<Void> deleteWarehouse(Long id) {
        warehouseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
