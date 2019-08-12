package com.techelevator;

public abstract class VendingMachineItem {
	private String name;
	private double price;
	private int quantity;
	private int quantitySold;

	public VendingMachineItem(String name, double price) {
		this.name = name;
		this.price = price;
		this.quantity = 5;
		this.quantitySold = 0;
	}
	
	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void dispense() {
		quantitySold++;
		quantity--;
	}

	public abstract int getTypeID();

	public int getQuantitySold() {
		return quantitySold;
	}
}
