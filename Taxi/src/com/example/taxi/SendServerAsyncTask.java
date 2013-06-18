package com.example.taxi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;

public class SendServerAsyncTask extends AsyncTask<Void, Void, Void>{
			
	//DATA COMMUNICATION VARIABLES
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
		
	@Override
	protected Void doInBackground(Void... params) {
		sendTaxiCoordinates();
		return null;
	}
		
	public void sendTaxiCoordinates() {
		String coordinates = TaxiConstants.curLatLng.latitude + "," + TaxiConstants.curLatLng.longitude;
		
		if(TaxiConstants.hasClient)
		{
			if(TaxiConstants.isVacant)
				coordinates += "," + TaxiConstants.clientLatLng.latitude + "," + TaxiConstants.clientLatLng.longitude + ",CLIENT";
				
			else
				coordinates += "," + TaxiConstants.destLatLng.latitude + "," + TaxiConstants.destLatLng.longitude + ",DESTINATION";
		}
			
		try
		{
			socket = new Socket(TaxiConstants.SERVERIP, TaxiConstants.PORT);
			output = new DataOutputStream(socket.getOutputStream());
				
			//SEND TAXI ID TO SERVER
			output.writeUTF(String.valueOf(TaxiConstants.TAXIID));
			output.writeUTF("SEND");
			output.writeUTF(coordinates);
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