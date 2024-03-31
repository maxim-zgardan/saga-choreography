package com.stock.ms.entity;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface StockRepository extends CrudRepository<WareHouse, Long> {

	WareHouse findByItem(String item);
}
