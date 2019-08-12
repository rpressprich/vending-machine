package com.techelevator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;

import com.techelevator.view.Menu;

/*
 * Vending Machine is a program that takes an input file to stock the vending machine, follows user input to sell product, and generates audit reports
 * and sales reports after transactions are completed.
 * The product types currently accepted are Candy, Chip, Drink, and Gum, each one with a corresponding ID number.
 * the program restocks based off an input file.
 * it displays menus to the user, to guide them through the purchase process.
 * options to display contents of the machine, to input money, to make purchases, and to finalize the transaction and receive change.
 * 
 * @author Robert Pressprich and Liam Sabor
 * @version 1.0
 * @date 6-21-2019
 */

public class VendingMachineCLI {
	private Map<String, VendingMachineItem> inventory;
	private double balance;
	private final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
	private PrintWriter writer;
	private Scanner systemInput;
	private int[] purchases; // keeps track of item types for outputFoodNoises
	private List<String> keyList;
	private final String INPUT_FILE = "vendingmachine.txt";
	private final String LOG_FILE = "log.txt";
	private final String REPORT_FILE = "SalesReport.txt";
	private static final int QUARTER_VALUE = 25;
	private static final int DIME_VALUE = 10;
	private static final int NICKEL_VALUE = 5;
	private static final int CENTS_IN_A_DOLLAR = 100;

	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "1) Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "2) Purchase";
	private static final String MAIN_MENU_OPTION_EXIT = "3) Turn Off Machine";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE,
			MAIN_MENU_OPTION_EXIT };
	private static final String PURCHASE_MENU_OPTIONS_FEED_MONEY = "1) Feed Money";
	private static final String PURCHASE_MENU_OPTIONS_SELECT_PRODUCT = "2) Select Product";
	private static final String PURCHASE_MENU_OPTIONS_FINISH_TRANSACTION = "3) Finish Transaction";
	private final String[] PURCHASE_MENU_OPTIONS = { PURCHASE_MENU_OPTIONS_FEED_MONEY,
			PURCHASE_MENU_OPTIONS_SELECT_PRODUCT, PURCHASE_MENU_OPTIONS_FINISH_TRANSACTION };
	private static final int NUMBER_OF_TYPES_OF_ITEMS = 4; // candy, gum, drink, chip

	private Menu menu;

	/*
	 * vendingMachineCLI creates a new vending machine. initializes it with a balance of 0
	 * @param menu a simple ui to prompt for user input 
	 */
	
	public VendingMachineCLI(Menu menu) {
		this.inventory = new HashMap<String, VendingMachineItem>();
		this.menu = menu;
		this.balance = 0;
	}
	/*
	 * run calls all other methods and handles the top menu. 
	 * creates a new log file and writes to it.
	 * it restocks the vending machine.
	 * runs the topmost menu.
	 * calls createSalesReport.
	 */
	private void run() throws IOException {
		boolean isRunDone = false;
		systemInput = new Scanner(System.in);

		File log = new File(LOG_FILE);
		log.delete(); // creates a new file to avoid overwrite or leftover text
		log.createNewFile();

		writer = new PrintWriter(log);
		restock(new File(INPUT_FILE));
		while (!isRunDone) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);

			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				displayItems(inventory, keyList);
			} else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				purchaseItems();
			} else if (choice.equals(MAIN_MENU_OPTION_EXIT)) {
				isRunDone = true;
			}
		}
		createSalesReport(new File(REPORT_FILE));
		writer.close();
		systemInput.close();
	}
	
	/*
	 * restock reads from an input file and puts vendingMachineItem into a hashmap inventory.
	 * @param file the name of the input file
	 * @return returns true if no exceptions are thrown
	 * @see run
	 */
	public boolean restock(File file) {
		try (Scanner input = new Scanner(file)) {
			keyList = new ArrayList<String>();
			while (input.hasNextLine()) {
				String[] itemInput = input.nextLine().trim().split("[|]");
				keyList.add(itemInput[0]);
				if (itemInput[3].equals("Drink")) // adds items into inventory
				{
					inventory.put(itemInput[0], new Drink(itemInput[1], Double.parseDouble(itemInput[2])));
				} else if (itemInput[3].equals("Chip")) {
					inventory.put(itemInput[0], new Chip(itemInput[1], Double.parseDouble(itemInput[2])));
				} else if (itemInput[3].equals("Candy")) {
					inventory.put(itemInput[0], new Candy(itemInput[1], Double.parseDouble(itemInput[2])));
				} else if (itemInput[3].equals("Gum")) {
					inventory.put(itemInput[0], new Gum(itemInput[1], Double.parseDouble(itemInput[2])));
				}
			}
			return true;
		}
		catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	/*
	 * display items displays each item in inventory
	 * @param inventory used for unit testing
	 * @param keyList used for unit testing
	 * @see run
	 * @return displays how many are left of the last item called
	 */
	public String displayItems(Map<String, VendingMachineItem> inventory, List<String> keyList) {
		String quantityMessage = "";
		for (String key : keyList) { // for each item, displays name, price, and quantity.
			quantityMessage = (inventory.get(key).getQuantity() == 0) ? "SOLD OUT"
					: "There are " + inventory.get(key).getQuantity() + " left";
			System.out.printf("%s %-20s $%.2f \t%s\n", key, inventory.get(key).getName(), inventory.get(key).getPrice(),
					quantityMessage);
		}
		return quantityMessage;
	}

	/*
	 * purchase items prompts the user to enter money, select a product to buy, or finalize transaction
	 * @see run
	 */
	private void purchaseItems() {
		purchases = new int[NUMBER_OF_TYPES_OF_ITEMS];
		boolean isPurchaseDone = false;
		while (!isPurchaseDone) {
			System.out.print(getBalanceMessage(balance));
			String choice = (String) menu.getChoiceFromOptions(PURCHASE_MENU_OPTIONS);

			if (choice.equals(PURCHASE_MENU_OPTIONS_FEED_MONEY)) {
				depositToBalance();
			} else if (choice.equals(PURCHASE_MENU_OPTIONS_SELECT_PRODUCT)) {
				buyProduct();
			} else if (choice.equals(PURCHASE_MENU_OPTIONS_FINISH_TRANSACTION)) {
				dispenseChange(balance, DATE_FORMAT, writer);
				displayFoodNoises(purchases);
				isPurchaseDone = true;
			}
		}
	}
	
	/*
	 * get balance message returns a string based on how much money is in the machine
	 * @param balance how much money is in the machine
	 * @return balance message
	 * @see purchaseItems
	 */
	public String getBalanceMessage(double balance) {
		return String.format("\nCurrent Money Provided: $%.2f", balance);
	}

	/*
	 * deposit to balance takes user input and adds it to machine balance. it also prints to the audit file
	 * @see purchaseItems
	 */
	private void depositToBalance() {
		double deposit = 0;
		System.out.print("Insert bills: ");
		try {
			deposit = Math.abs(Integer.parseInt(systemInput.nextLine()));
		} catch (Exception e) {
			System.out.println("\nNumbers only, please.");
		}

		balance += deposit; // writes to the audit file
		writer.printf("%s\t%-23s \t$%.2f\t$%.2f\n",
				DATE_FORMAT.format(Calendar.getInstance(TimeZone.getTimeZone("America/Detroit")).getTime()),
				"FEED MONEY: ", deposit, balance);
	}

	/*
	 * buy products takes user input and if the product exists, has quantity, and the user has enough money, sells the product
	 * @see purchaseItems
	 */
	private void buyProduct() {
		System.out.print("Enter product code, e.g. A1: ");
		String key = systemInput.nextLine();
		if (inventory.get(key) == null) {
			System.out.println("Product not found.");
		} else if (inventory.get(key).getQuantity() == 0) {
			System.out.println("That product is sold out.");
		} else if (inventory.get(key).getPrice() > balance) {
			System.out.println("Balance too low. Item too expensive.");
		} else {
			dispenseItem(key);
		}
	}

	/*
	 * dispense item dispenses item, prints to the audit file and takes money out of the machine balance
	 * @param key the unique string related to the product 
	 * @see buyProduct
	 */
	private void dispenseItem(String key) {
		inventory.get(key).dispense();
		writer.printf("%s\t%-23s \t$%.2f\t$%.2f\n", // writes to audit file
				DATE_FORMAT.format(Calendar.getInstance(TimeZone.getTimeZone("America/Detroit")).getTime()),
				inventory.get(key).getName() + " " + key, balance, balance - inventory.get(key).getPrice());
		purchases[inventory.get(key).getTypeID()]++; // records type for outputFoodNoises
		System.out.println("Dispensing " + inventory.get(key).getName() + ".");

		balance -= inventory.get(key).getPrice();
	}

	/*
	 * dispense change dispences quarters, dimes and nickels based on the leftover balabce
	 * @param balance is the machine balance
	 * @param dateFormat is for output files
	 * @param writer is for the output files
	 * @return an array containing the quarters, dimes and nickels dispensed
	 */
	public int[] dispenseChange(double balance, DateFormat dateFormat, PrintWriter writer) // dispenses quarters/dimes/nickels and resets balance to 0
	{
		int intBalance = (int) (Math.ceil(balance * CENTS_IN_A_DOLLAR));
		int quarters = intBalance / QUARTER_VALUE;
		intBalance -= quarters * QUARTER_VALUE;
		int dimes = intBalance / DIME_VALUE;
		intBalance -= dimes * DIME_VALUE;
		int nickel = intBalance / NICKEL_VALUE;

		System.out.println(
				"Dispensing " + quarters + " quarter(s), " + dimes + " dime(s), and " + nickel + " nickel(s).");
		writer.printf("%s\t%-23s \t$%.2f\t$%.2f\n", // writing to audit file
				DATE_FORMAT.format(Calendar.getInstance(TimeZone.getTimeZone("America/Detroit")).getTime()),
				"GIVE CHANGE: ", balance, 0.00);
		balance = 0;
		
		return new int[] {quarters, dimes, nickel};
	}

	/*
	 * display food noises is my favorite it displays sounds of enjoyment based on the type of food enjoyed
	 * @param purchases an array of the types of food being purchased
	 * @return the food enjoyment noise of the last item purchased
	 * @see purchaseItems
	 */
	public String displayFoodNoises(int[] purchases) {
		String outputString = "";
		for (int i = 0; i < purchases.length; i++) {
			while (purchases[i] > 0) {
				if (i == 0) {
					System.out.println("Crunch Crunch, Yum!"); // displays for chips
					purchases[i]--;
					outputString = "Crunch Crunch, Yum!";
				}
				if (i == 1) {
					System.out.println("Munch Munch, Yum!"); // displays for candy
					purchases[i]--;
					outputString = "Munch Munch, Yum!";
				}
				if (i == 2) {
					System.out.println("Glug Glug, Yum!"); // displays for drink
					purchases[i]--;
					outputString = "Glug Glug, Yum!";
				}
				if (i == 3) {
					System.out.println("Chew Chew, Yum!"); // displays for gum
					purchases[i]--;
					outputString = "Chew Chew, Yum!";
				}
			}
		}
		return outputString;
	}

	/*
	 * creates sales report creates a sales report based on what is left in the inventory and displays the dollar amount of the total sales from the 
	 * machine
	 * @param outputFile is the file to be written to
	 * @return whether or not output file is created
	 * @see run
	 */
	public boolean createSalesReport(File outputFile) throws IOException {
		outputFile.delete();
		boolean isCreated = outputFile.createNewFile(); // creates a new file to avoid overwriting or leftover text
		PrintWriter outputWriter = new PrintWriter(outputFile);
		double totalSales = 0;
		for (String key : keyList) {
			outputWriter.println(inventory.get(key).getName() + "|" + (inventory.get(key).getQuantitySold())); // adds name and quantity sold to sales report file
			totalSales += inventory.get(key).getPrice() * (inventory.get(key).getQuantitySold());
		}
		outputWriter.printf("\n**TOTAL SALES** $%.2f", totalSales); // ads total sales to sales report file
		outputWriter.close();
		return isCreated;
	}

	public static void main(String[] args) throws IOException {
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.run();
	}
}
