package com.bigobooks.services;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bigobooks.dto.WarehouseDto;
import com.bigobooks.dto.WarehouseRequestDto;
import com.bigobooks.entities.stock.Warehouse;
import com.bigobooks.mappers.WarehouseMapper;
import com.bigobooks.repositories.WarehouseRepository;
import com.bigobooks.specifications.WarehouseSpecifications;
import org.springframework.cache.Cache;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WarehouseService extends AbstractCrudService<Warehouse, WarehouseDto, WarehouseRepository> {

    private final WarehouseMapper warehouseMapper;
    private final WarehouseSpecifications warehouseSpecifications;
    @SuppressWarnings("unused")
    private final CacheManager cacheManager;
    private final Cache cache;

    public WarehouseService(WarehouseRepository warehouseRepository, WarehouseMapper warehouseMapper,
            WarehouseSpecifications warehouseSpecifications, CacheManager cacheManager) {
        super(warehouseRepository);
        this.warehouseMapper = warehouseMapper;
        this.warehouseSpecifications = warehouseSpecifications;
        this.cacheManager = cacheManager;
        this.cache = cacheManager.getCache("warehouse");
    }

    @Override
    protected Function<Warehouse, WarehouseDto> toDtoMapper() {
        return warehouseMapper::toDto;
    }

    @Transactional
    @CachePut(value = "warehouse", key = "'warehouse_' + #result.id", condition = "#result.id != null")
    public WarehouseDto create(WarehouseRequestDto dto) {
        warehouseSpecifications.validateCreate(dto);
        Warehouse warehouse = new Warehouse();
        warehouseMapper.update(dto, warehouse);
        Warehouse saved = repository.save(warehouse);
        return warehouseMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseDto> getAll(Pageable pageable) {
        Page<Long> idPage = repository.findAllIds(pageable);
        ArrayList<WarehouseDto> warehouses = new ArrayList<>(idPage.getNumberOfElements());

        for (Long id : idPage.getContent()) {
            String cacheKey = "warehouse_" + id;
            WarehouseDto cached = cache.get(cacheKey, WarehouseDto.class);
            log.debug("Cache GET cache=warehouse key={} result={}", cacheKey, cached == null ? "MISS" : "HIT");

            // Si esta en el cache retorno del cache directemente
            // si no lo busco en la db y lo agrego
            if (cached != null) {
                warehouses.add(cached);
                continue;
            }

            getById(id).ifPresent(warehouse -> {
                cache.put(cacheKey, warehouse);
                log.debug("Cache PUT cache=warehouse key={}", cacheKey);
                warehouses.add(warehouse);
            });

        }

        return new PageImpl<>(warehouses, pageable, idPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "warehouse", key = "'warehouse_' + #id")
    public Optional<WarehouseDto> getById(Long id) {
        return super.getById(id);
    }

    @Transactional
    @CachePut(value = "warehouse", key = "'warehouse_' + #id", condition = "#result != null && #result.isPresent()")
    public Optional<WarehouseDto> update(Long id, WarehouseRequestDto dto) {
        warehouseSpecifications.validateUpdate(dto);
        return repository.findById(id).map(warehouse -> {
            warehouseMapper.update(dto, warehouse);
            Warehouse saved = repository.save(warehouse);
            return warehouseMapper.toDto(saved);
        });
    }

    @Override
    @Transactional
    @CacheEvict(value = "warehouse", key = "'warehouse_' + #id")
    public void delete(Long id) {
        super.delete(id);
    }

    @Transactional(readOnly = true)
    // Este al hacer getAll nomas va utilizar el cache xd
    public Page<WarehouseDto> findByCity(String city, Pageable pageable) {
        if (!StringUtils.hasText(city)) {
            return getAll(pageable);
        }
        return repository.findByCity(city.trim(), pageable).map(warehouseMapper::toDto);
    }
}
