package com.techelevator;

public class Chip extends VendingMachineItem
{
	private int typeID;

	public Chip(String name, double price) {
		super(name, price);
		this.typeID = 1;
	}

	public int getTypeID() {
		return typeID;
	}
}
