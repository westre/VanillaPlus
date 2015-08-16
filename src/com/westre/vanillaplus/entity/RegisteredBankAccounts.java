package com.westre.vanillaplus.entity;

import java.util.HashMap;
import java.util.Map.Entry;

public class RegisteredBankAccounts {
	
	private static HashMap<Integer, Integer> bankAccountMap = new HashMap<Integer, Integer>();
	
	public static void addBankAccount(int account, int money) {
		bankAccountMap.put(account, money);
		System.out.println("Account: " + account);
	}
	
	public static HashMap<Integer, Integer> getAccounts() {
		return bankAccountMap;
	}
	
	public static void setMoney(int account, int money) {
		bankAccountMap.put(account, money);
	}
	
	public static int getMoney(int pinCode) {	
		for(Entry<Integer, Integer> entry : bankAccountMap.entrySet()) {
		    int key = entry.getKey();

		    if(key == pinCode) {
		    	return entry.getValue();
		    }
		}
		return 0;
	}
	
}
