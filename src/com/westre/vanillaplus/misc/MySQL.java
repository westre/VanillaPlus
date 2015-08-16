package com.westre.vanillaplus.misc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {
	
	private Connection connection;
	
	public MySQL() {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost/vanillaplus", "vanillaplus", "projectlife1337");
		}
		catch (SQLException ex) {
			System.out.println("NO DATABASE FOUND! " + ex.getMessage());
		}
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void close() {
		try {
			connection.close();
		}
		catch (SQLException ex) {
			System.out.println("COULD NOT CLOSE DATABASE! " + ex.getMessage());
		}
	}
}
