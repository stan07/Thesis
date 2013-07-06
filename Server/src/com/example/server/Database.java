package com.example.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	
	private String dbDriver = "com.mysql.jdbc.Driver";
	private String dbUrl = "jdbc:mysql://localhost/transphone?user=root&password=dolphin";
	
	public Database() {
		try {
			// This will load the MySQL driver
			Class.forName(dbDriver);
			
			// Setup the connection with the DB
			connection = DriverManager.getConnection(dbUrl);
			
			// Statements allow to issue SQL queries to the database
			statement = connection.createStatement();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void insertClientValues(ClientStruct client) throws SQLException {
		statement.execute("INSERT INTO CLIENT VALUES (default, '"+client.clientIP+"', "+client.srcLatitude+", "+
													  client.srcLongitude+", "+client.destLatitude+", "+client.destLongitude+")");
	}
	
	public void displayTaxi(String plateNo, String bodyNo) throws SQLException {
		if(!plateNo.equals("") && !bodyNo.equals(""))
			resultSet = statement.executeQuery("SELECT * FROM TAXI WHERE Plate_No LIKE '"+plateNo+"' AND Body_No LIKE '"+bodyNo+"'");
		
		else if(!plateNo.equals(""))
			resultSet = statement.executeQuery("SELECT * FROM TAXI WHERE Plate_No LIKE '"+plateNo+"'");
		
		else 
			resultSet = statement.executeQuery("SELECT * FROM TAXI WHERE Body_No LIKE '"+bodyNo+"'");
		
		displayResultSet(resultSet);
	}
	
	public void displayAllTaxi() throws SQLException {
		resultSet = statement.executeQuery("SELECT * FROM TAXI");
		displayResultSet(resultSet);
	}
	
	private void displayResultSet(ResultSet resultSet) throws SQLException {
		
		while(resultSet.next())
		{
			TaxiStruct taxi = new TaxiStruct();
			taxi.plateNo = resultSet.getString("Plate_No");
			taxi.bodyNo = resultSet.getString("Body_No");
			taxi.taxiDesc = resultSet.getString("Taxi_Description");
			taxi.taxiIP = resultSet.getString("Taxi_IP");
			taxi.latitude = resultSet.getDouble("Taxi_Latitude");
			taxi.longitude = resultSet.getDouble("Taxi_Longitude");
			taxi.status = resultSet.getBoolean("Vacancy_Status");
			taxi.driverLicense = resultSet.getString("Driver_License");
			
			System.out.println("Plate No: " + taxi.plateNo);
			System.out.println("Body No: " + taxi.bodyNo);
			System.out.println("Taxi Description: " + taxi.taxiDesc);
			System.out.println("Taxi IP: " + taxi.taxiIP);
			System.out.println("Taxi Latitude: " + taxi.latitude);
			System.out.println("Taxi Longitude: " + taxi.longitude);
			System.out.println("Taxi Vacancy: " + taxi.status);
			System.out.println("Driver License: " + taxi.driverLicense);
			System.out.println("\n");
		}
	}
}
