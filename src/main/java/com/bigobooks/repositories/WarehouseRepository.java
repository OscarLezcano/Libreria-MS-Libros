package com.bigobooks.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.bigobooks.entities.stock.Warehouse;
import com.bigobooks.repository.BaseRepository;

public interface WarehouseRepository extends BaseRepository<Warehouse> {

    Optional<Warehouse> findByName(String name);

    Page<Warehouse> findByCity(String city, Pageable pageable);

    @Query(value = "SELECT * FROM warehouse WHERE is_deleted = true", nativeQuery = true)
    List<Warehouse> findDeleted();

    @Query(value = "SELECT * FROM warehouse", nativeQuery = true)
    List<Warehouse> findAllIncludingDeleted();
}
