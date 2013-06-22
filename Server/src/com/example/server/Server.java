package com.example.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{
	
	public ServerSocket serverSocket;
	
	private static String clientCoordinates = "", taxiCoordinates = "";
		
	@Override
	public void run() {

		//attempt to create a new server for data transfers
		try {
			serverSocket = new ServerSocket(Constants.PORT);
			System.out.println("LISTENING TO PORT: " + Constants.PORT);
		} catch (IOException e) {
			System.out.println("FAILED TO LISTEN TO PORT...");
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
		
		private Socket clientSocket;
		
		public ServerThread(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		
		@Override
		public void run() {
		
			int ID = 0;
			DataInputStream input = null;
			DataOutputStream output = null;
			
			try 
			{
				String clientIP = clientSocket.getInetAddress().toString();
				System.out.println("Connected to client: " + clientIP);
				
				input = new DataInputStream(clientSocket.getInputStream());
				output = new DataOutputStream(clientSocket.getOutputStream());
	
				try{
					ID = Integer.parseInt(input.readUTF());
					System.out.println("ID: " + ID);
				} catch(NumberFormatException e) {
					output.writeUTF("ID not found!");
					e.printStackTrace();
				}
			
				if(ID == Constants.SERVERID)
				{
					output.writeUTF(taxiCoordinates);
					String serverMsg = input.readUTF();
					System.out.println("Server Message: " + serverMsg);
				}
				
				else if(ID == Constants.TAXIID)
				{	
					String taxiAction = input.readUTF();
					
					if(taxiAction.equals("LISTEN"))
					{	
						if(!clientCoordinates.equals(""))
						{
							output.writeUTF(clientCoordinates);
							clientCoordinates = "";
						}	
						
						else
							output.writeUTF("NULL");
					}
					
					else
					{
						taxiCoordinates = input.readUTF();
						System.out.println("Taxi Coordinates: " + taxiCoordinates);
					}
				}
				
				else
				{
					clientCoordinates = input.readUTF();
					System.out.println("Client Coordinates: " + clientCoordinates);
					output.writeUTF("Coordinates received by server!");
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		
			finally
			{
				if(clientSocket!= null)
				{
					try {
						clientSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if(input!= null)
				{
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}	
				}
				
				if(output!= null)
				{
					try {
						output.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}