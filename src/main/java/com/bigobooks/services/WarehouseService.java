package com.bigobooks.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bigobooks.dto.WarehouseDto;
import com.bigobooks.entities.stock.Warehouse;
import com.bigobooks.mappers.WarehouseMapper;
import com.bigobooks.repositories.WarehouseRepository;
import com.bigobooks.service.BaseService;

@Service
public class WarehouseService extends BaseService<Warehouse, WarehouseRepository> {

    private final WarehouseMapper warehouseMapper;

    public WarehouseService(WarehouseRepository warehouseRepository, WarehouseMapper warehouseMapper) {
        super(warehouseRepository);
        this.warehouseMapper = warehouseMapper;
    }

    @Transactional(readOnly = true)
    public Page<WarehouseDto> getAll(Pageable pageable) {
        return getRepository().findAll(pageable).map(warehouseMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<WarehouseDto> getById(Long id) {
        return findById(id).map(warehouseMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<WarehouseDto> findByName(String name) {
        return getRepository().findByName(name).map(warehouseMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<WarehouseDto> findByCity(String city, Pageable pageable) {
        if (!StringUtils.hasText(city)) {
            return getAll(pageable);
        }
        return getRepository().findByCity(city.trim(), pageable).map(warehouseMapper::toDto);
    }

    @Transactional
    public WarehouseDto create(WarehouseDto dto) {
        Warehouse warehouse = new Warehouse();
        warehouseMapper.update(dto, warehouse);
        return warehouseMapper.toDto(save(warehouse));
    }

    @Transactional
    public Optional<WarehouseDto> update(Long id, WarehouseDto dto) {
        return findById(id).map(warehouse -> {
            warehouseMapper.update(dto, warehouse);
            return warehouseMapper.toDto(save(warehouse));
        });
    }

    @Transactional
    public void delete(Long id) {
        findById(id).ifPresent(warehouse -> deleteById(warehouse.getId()));
    }
}
