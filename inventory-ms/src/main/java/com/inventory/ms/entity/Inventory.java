package com.inventory.ms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Inventory {

	@Id
	@GeneratedValue
	private long id;

	@Column
	private int quantity;

	@Column
	private String item;

}
