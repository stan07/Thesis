package com.example.taxi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.android.gms.maps.model.LatLng;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

public class ListenServerAsyncTask extends AsyncTask<Void, Void, Void>{
		
	//DATA COMMUNICATION VARIABLES
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	
	@Override
	protected Void doInBackground(Void... params) {
		listenToServer();
		return null;
	}
				
	private void listenToServer() {
		String serverMsg;
		
		while(!TaxiConstants.hasClient)
		{
			try
			{
				socket = new Socket(TaxiConstants.SERVERIP, TaxiConstants.PORT);
				input = new DataInputStream(socket.getInputStream());
				output = new DataOutputStream(socket.getOutputStream());
				
				//SEND TAXI ID TO SERVER
				output.writeUTF(String.valueOf(TaxiConstants.TAXIID));
				output.writeUTF("LISTEN");
			
				//READ SERVER MESSAGE
				serverMsg = input.readUTF();
			
				if(!serverMsg.equals("NULL"))
				{
					String[] coordinates = serverMsg.split(",");
					try 
					{
						TaxiConstants.clientLatLng = new LatLng(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]));
						TaxiConstants.destLatLng = new LatLng(Double.parseDouble(coordinates[2]), Double.parseDouble(coordinates[3]));
						TaxiConstants.hasClient = true;
						
						Handler handler = new Handler(Looper.getMainLooper());
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								TaxiMap.setMarkers();
							}
						});
					}
					catch(NumberFormatException e)
					{
						e.printStackTrace();
					}
				}
			} 
			catch (UnknownHostException e) 
			{	
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			finally
			{
				if (socket != null)
				{
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}	
			
				if (output != null)
				{
					try {
						output.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (input != null)
				{
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}