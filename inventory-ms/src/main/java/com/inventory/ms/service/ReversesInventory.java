package com.inventory.ms.service;

import com.inventory.ms.dto.DeliveryEvent;
import com.inventory.ms.dto.PaymentEvent;
import com.inventory.ms.entity.InventoryRepository;
import com.inventory.ms.entity.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class ReversesInventory {

	@Autowired
	private InventoryRepository repository;

	@Autowired
	private KafkaTemplate<String, PaymentEvent> kafkaTemplate;

	@KafkaListener(topics = "reversed-inventory", groupId = "inventory-group")
	public void reverseStock(String event) {
		System.out.println("Inside reverse inventory for order "+event);
		
		try {
			DeliveryEvent deliveryEvent = new ObjectMapper().readValue(event, DeliveryEvent.class);

			Inventory inv = this.repository.findByItem(deliveryEvent.getOrder().getItem());

			inv.setQuantity(inv.getQuantity() + deliveryEvent.getOrder().getQuantity());
			repository.save(inv);

			PaymentEvent paymentEvent = new PaymentEvent();
			paymentEvent.setOrder(deliveryEvent.getOrder());
			paymentEvent.setType("PAYMENT_REVERSED");
			kafkaTemplate.send("reversed-payments", paymentEvent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
