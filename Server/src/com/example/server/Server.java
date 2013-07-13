package com.example.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Server implements Runnable{
	
	public static ServerSocket serverSocket;
		
	@Override
	public void run() {

		//attempt to create a new server for data transfers
		try {
			serverSocket = new ServerSocket(Constants.SERVERPORT);
			System.out.println("LISTENING TO SERVERPORT: " + Constants.SERVERPORT);
		} catch (IOException e) {
			System.out.println("FAILED TO LISTEN TO SERVERPORT...");
			e.printStackTrace();
		}
	  
		//process continues if the server has been established then generate a server-client connection
		while(Constants.isThreadRunning)
		{
			try {
				Socket socket = serverSocket.accept();
				new Thread(new ServerThread(socket)).start();
			} catch (IOException e) {
				
			}
		}
		System.out.println("SERVER IS STOPPED!");
	}
	
	private class ServerThread implements Runnable {
		
		private Socket socket;
		
		public ServerThread(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {
		
			int ID = 0;
			Socket taxiSocket = null;
			DataInputStream input = null;
			DataOutputStream output = null;
			
			try 
			{
				input = new DataInputStream(socket.getInputStream());
				output = new DataOutputStream(socket.getOutputStream());
	
				try{
					ID = Integer.parseInt(input.readUTF());
					System.out.println("ID: " + ID);
				} catch(NumberFormatException e) {
					output.writeUTF("ID not found!");
					e.printStackTrace();
				}
				
				if(ID == Constants.TAXI_LOGIN_ID)
				{
					String[] loginData = new String[3]; 
					loginData[0] = input.readUTF();
					loginData[1] = input.readUTF();
					loginData[2] = input.readUTF();
					
					int result = Constants.database.loginTaxiData(loginData);
					output.writeUTF(String.valueOf(result));
				}
				
				else if(ID == Constants.TAXI_STATUS_ID)
				{
					String plateNo = input.readUTF();
					System.out.println(plateNo);
					char status = input.readUTF().charAt(0);
					System.out.println(status);
					Constants.database.updateTaxiStatus(plateNo, status);
				}
				
				else if(ID == Constants.TAXI_COORDINATES_ID)
				{	
					String plateNo = input.readUTF();
					String[] taxiMsg = input.readUTF().split(",");
					double[] taxiCoordinates = new double[2]; 
					taxiCoordinates[0] = Double.parseDouble(taxiMsg[0]);
					taxiCoordinates[1] = Double.parseDouble(taxiMsg[1]);
					Constants.database.updateTaxiCoordinates(plateNo, taxiCoordinates);
				}
				
				else
				{
					String clientMsg = input.readUTF();
					System.out.println("Client Message: " + clientMsg);
					String[] clientCoordinates = clientMsg.split(",");
					
					ClientStruct client = new ClientStruct();
					client.srcLatitude = Double.parseDouble(clientCoordinates[0]);
					client.srcLongitude = Double.parseDouble(clientCoordinates[1]);
					client.destLatitude = Double.parseDouble(clientCoordinates[2]);
					client.destLongitude = Double.parseDouble(clientCoordinates[3]);
					
					Constants.database.insertClientValues(client);
					TaxiStruct taxi = Constants.database.calculateShortestTaxiDistance(client);
					output.writeUTF(taxi.plateNo);
					output.writeUTF(taxi.bodyNo);
					output.writeUTF(taxi.taxiComp);
					output.writeUTF(taxi.taxiDesc);
					output.writeUTF(taxi.driverName);
					output.writeUTF(taxi.driverNo);
					
					taxiSocket = new Socket(taxi.taxiIP, Constants.TAXIPORT);
					DataOutputStream taxiOutput = new DataOutputStream(taxiSocket.getOutputStream());
					taxiOutput.writeUTF(clientCoordinates[0]);
					taxiOutput.writeUTF(clientCoordinates[1]);
					taxiOutput.writeUTF(clientCoordinates[2]);
					taxiOutput.writeUTF(clientCoordinates[3]);
					
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if(socket != null)
						socket.close();
					
					if(taxiSocket != null)
						taxiSocket.close();

					if(input != null)
						input.close();
					
					if(output != null)
						output.close();
					
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
}