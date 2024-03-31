package com.inventory.ms.entity;

import org.springframework.data.repository.CrudRepository;

public interface InventoryRepository extends CrudRepository<Inventory, Long> {

	Inventory findByItem(String item);
}
