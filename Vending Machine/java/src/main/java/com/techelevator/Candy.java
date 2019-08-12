package com.techelevator;

public class Candy extends VendingMachineItem {

	private int typeID;

	public Candy(String name, double price) {
		super(name, price);
		this.typeID = 1;
	}

	public int getTypeID() {
		return typeID;
	}
}
