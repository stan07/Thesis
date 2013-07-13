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
		statement.execute("INSERT INTO CLIENT VALUES (default, "+client.srcLatitude+", "+client.srcLongitude+", "
														+client.destLatitude+", "+client.destLongitude+")");
	}
	
	
	public int loginTaxiData(String[] loginData) throws SQLException {
		int result = 0;
		
		resultSet = statement.executeQuery("SELECT * FROM DRIVER WHERE Driver_License LIKE '"+loginData[1]+"'");
		
		if(resultSet.first())
			result = statement.executeUpdate("UPDATE TAXI SET Taxi_IP='"+loginData[2]+"', Driver_License='"+loginData[1]+"' WHERE Plate_No LIKE '"+loginData[0]+"'");
		
		return result;
	}
	
	
	public void updateTaxiCoordinates(String plateNo, double[] taxiCoordinates) throws SQLException {
		statement.executeUpdate("UPDATE TAXI SET Taxi_Latitude="+taxiCoordinates[0]+", Taxi_Longitude="+taxiCoordinates[1]+" WHERE Plate_No LIKE '"+plateNo+"'");
	}
	
	
	public void updateTaxiStatus(String plateNo, char status) throws SQLException {
		statement.executeUpdate("UPDATE TAXI SET Vacancy_Status='"+status+"' WHERE Plate_No LIKE '"+plateNo+"'");
	}
	
	public void clearTaxiData() throws SQLException {
		statement.executeUpdate("UPDATE TAXI SET Taxi_IP = '', Taxi_Latitude = 0, Taxi_Longitude = 0, Vacancy_Status = '', Driver_License = ''");
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
		
		TaxiStruct taxi = new TaxiStruct();
		while(resultSet.next())
		{
			taxi.plateNo = resultSet.getString("Plate_No");
			taxi.bodyNo = resultSet.getString("Body_No");
			taxi.taxiComp = resultSet.getString("Taxi_Company");
			taxi.taxiDesc = resultSet.getString("Taxi_Description");
			taxi.taxiIP = resultSet.getString("Taxi_IP");
			taxi.latitude = resultSet.getDouble("Taxi_Latitude");
			taxi.longitude = resultSet.getDouble("Taxi_Longitude");
			taxi.status = resultSet.getString("Vacancy_Status").charAt(0);
			taxi.driverLicense = resultSet.getString("Driver_License");
			
			System.out.println("Plate No: " + taxi.plateNo);
			System.out.println("Body No: " + taxi.bodyNo);
			System.out.println("Taxi Company: " + taxi.taxiComp);
			System.out.println("Taxi Description: " + taxi.taxiDesc);
			System.out.println("Taxi IP: " + taxi.taxiIP);
			System.out.println("Taxi Latitude: " + taxi.latitude);
			System.out.println("Taxi Longitude: " + taxi.longitude);
			System.out.println("Taxi Vacancy: " + taxi.status);
			System.out.println("Driver License: " + taxi.driverLicense);
			System.out.println("\n");
		}
	}
	
	
	public TaxiStruct calculateShortestTaxiDistance(ClientStruct client) throws SQLException {
		TaxiStruct taxi = new TaxiStruct();
		double shortestDistance = 0, curDistance = 0;
		Statement driverStatement = connection.createStatement();
		
		resultSet = statement.executeQuery("SELECT * FROM TAXI WHERE Vacancy_Status='V'");
		
		while(resultSet.next())
		{
			if(shortestDistance == 0)
				shortestDistance = distance(client.srcLatitude, client.srcLongitude, resultSet.getDouble("Taxi_Latitude"), resultSet.getDouble("Taxi_Longitude"));
			else
				curDistance = distance(client.srcLatitude, client.srcLongitude, resultSet.getDouble("Taxi_Latitude"), resultSet.getDouble("Taxi_Longitude"));
			
			if(curDistance == 0 || curDistance < shortestDistance)
			{
				taxi.plateNo = resultSet.getString("Plate_No");
				taxi.bodyNo = resultSet.getString("Body_No");
				taxi.taxiComp = resultSet.getString("Taxi_Company");
				taxi.taxiDesc = resultSet.getString("Taxi_Description");
				taxi.taxiIP = resultSet.getString("Taxi_IP");
				
				ResultSet driverResultSet = driverStatement.executeQuery("SELECT * FROM DRIVER WHERE Driver_License LIKE '"+resultSet.getString("Driver_License")+"'");
				driverResultSet.next();
				taxi.driverName = driverResultSet.getString("Driver_Name");
				taxi.driverNo = driverResultSet.getString("Driver_No");
				
				if(curDistance != 0)
					shortestDistance = curDistance;
			}
		}
		return taxi;
	}
	
	
	private double distance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		dist = dist * 1.609344;
		return (dist);
	}

	//This function converts decimal degrees to radians
	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	//This function converts radians to decimal degrees
	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}
}