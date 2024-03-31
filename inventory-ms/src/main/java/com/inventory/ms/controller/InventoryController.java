package com.inventory.ms.controller;

import com.inventory.ms.dto.CustomerOrder;
import com.inventory.ms.dto.DeliveryEvent;
import com.inventory.ms.dto.InventoryDto;
import com.inventory.ms.dto.PaymentEvent;
import com.inventory.ms.entity.InventoryRepository;
import com.inventory.ms.entity.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api")
public class InventoryController {

	@Autowired
	private InventoryRepository repository;

	@Autowired
	private KafkaTemplate<String, DeliveryEvent> kafkaTemplate;

	@Autowired
	private KafkaTemplate<String, PaymentEvent> kafkaPaymentTemplate;

	@KafkaListener(topics = "new-payments", groupId = "payments-group")
	public void updateStock(String paymentEvent) throws JsonMappingException, JsonProcessingException {
		System.out.println("Inside update inventory for order "+paymentEvent);
		
		DeliveryEvent event = new DeliveryEvent();

		PaymentEvent p = new ObjectMapper().readValue(paymentEvent, PaymentEvent.class);
		CustomerOrder order = p.getOrder();

		try {
			Inventory inventory = repository.findByItem(order.getItem());


			if (inventory == null || inventory.getQuantity() < order.getQuantity()) {
				System.out.println("Inventory not exist so reverting the order");
				throw new Exception("Inventory not available");
			}

			inventory.setQuantity(inventory.getQuantity() - order.getQuantity());
			repository.save(inventory);

			event.setType("INVENTORY_UPDATED");
			event.setOrder(p.getOrder());
			kafkaTemplate.send("new-inventory", event);
		} catch (Exception e) {
			PaymentEvent pe = new PaymentEvent();
			pe.setOrder(order);
			pe.setType("PAYMENT_REVERSED");
			kafkaPaymentTemplate.send("reversed-payments", pe);
		}
	}

	@PostMapping("/addItems")
	public void addItems(@RequestBody InventoryDto inventoryDto) {
		Inventory items = repository.findByItem(inventoryDto.getItem());

		if (items != null) {
			items.setQuantity(inventoryDto.getQuantity() + items.getQuantity());
			repository.save(items);
		} else {
			Inventory i = new Inventory();
			i.setItem(inventoryDto.getItem());
			i.setQuantity(inventoryDto.getQuantity());
			repository.save(i);
		}
	}
}
