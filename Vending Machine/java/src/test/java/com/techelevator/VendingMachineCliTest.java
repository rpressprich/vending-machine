package com.techelevator;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.techelevator.view.Menu;

public class VendingMachineCliTest {
	
	VendingMachineCLI CLI = new VendingMachineCLI(new Menu(System.in, System.out));
	DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
	
	@Test
	public void testGetBalanceMessage() {
		assertEquals("\nCurrent Money Provided: $2.50", CLI.getBalanceMessage(2.50));
		assertEquals("\nCurrent Money Provided: $-2.50", CLI.getBalanceMessage(-2.50));
		assertEquals("\nCurrent Money Provided: $2.50", CLI.getBalanceMessage(2.5000000));
		assertEquals("\nCurrent Money Provided: $0.00", CLI.getBalanceMessage(0));
		assertEquals("\nCurrent Money Provided: $1000000.00", CLI.getBalanceMessage(1000000));
	}
	
	@Test
	public void testRestock() {
		assertTrue(CLI.restock(new File("VendingMachine.txt")));
	}
	
	@Test
	public void testDisplayItems() {
		Map<String, VendingMachineItem> inventory = new HashMap<String, VendingMachineItem>();
		List<String> keyList = new ArrayList<String>();
		keyList.add("A1");
		keyList.add("B3");
		
		inventory.put("A1", new Chip("Doritos", 3));
		inventory.put("B3", new Candy("Maltesers", 2.65));

		inventory.get("B3").dispense();
		inventory.get("B3").dispense();
		inventory.get("B3").dispense();
		inventory.get("B3").dispense();
		assertEquals("There are 1 left", CLI.displayItems(inventory, keyList));
		inventory.get("B3").dispense();
		assertEquals("SOLD OUT", CLI.displayItems(inventory, keyList));
	}
	
	@Test
	public void testDispenseChange() throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(new File("log.txt"));
		assertArrayEquals(new int[] {10, 1, 1},CLI.dispenseChange(2.65, DATE_FORMAT, writer));
		assertArrayEquals(new int[] {0, 0, 0},CLI.dispenseChange(0.00, DATE_FORMAT, writer));
		assertArrayEquals(new int[] {10, 1, 1},CLI.dispenseChange(2.69, DATE_FORMAT, writer));
	}
	
	@Test
	public void testDisplayFoodNoises() {
		assertEquals("Chew Chew, Yum!", CLI.displayFoodNoises(new int[] {0, 0, 0, 1}));
		assertEquals("Glug Glug, Yum!", CLI.displayFoodNoises(new int[] {0, 0, 1, 0}));
		assertEquals("Munch Munch, Yum!", CLI.displayFoodNoises(new int[] {0, 1, 0, 0}));
		assertEquals("Crunch Crunch, Yum!", CLI.displayFoodNoises(new int[] {1, 0, 0, 0}));
	}
	
	
}
