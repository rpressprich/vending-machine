package com.techelevator;

public class Drink extends VendingMachineItem
{
	private int typeID;

	public Drink(String name, double price) {
		super(name, price);
		this.typeID = 2;
	}

	public int getTypeID() {
		return typeID;
	}
}
