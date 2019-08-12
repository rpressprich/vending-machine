package com.techelevator;

public class Gum extends VendingMachineItem
{
	private int typeID;

	public Gum(String name, double price) {
		super(name, price);
		this.typeID = 3;
	}

	public int getTypeID() {
		return typeID;
	}
}
