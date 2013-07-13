package com.example.taxi;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;

public class SendServerAsyncTask extends AsyncTask<Integer, Void, Void>{
	
	private Socket socket;
	private DataOutputStream output;
	
	@Override
	protected Void doInBackground(Integer... id) {
		sendTaxiData(id[0]);
		return null;
	}
		
	public void sendTaxiData(int id) {
		try
		{
			socket = new Socket(TaxiConstants.SERVERIP, TaxiConstants.SERVERPORT);
			output = new DataOutputStream(socket.getOutputStream());
			
			if(id == TaxiConstants.TAXI_COORDINATES_ID)
			{
				//SEND TAXI COORDINATES TO SERVER
				String coordinates = TaxiConstants.curLatLng.latitude + "," + TaxiConstants.curLatLng.longitude;
				output.writeUTF(String.valueOf(TaxiConstants.TAXI_COORDINATES_ID));
				output.writeUTF(TaxiConstants.plateNo);
				output.writeUTF(coordinates);
			}
			
			else
			{
				//SEND TAXI STATUS TO SERVER
				output.writeUTF(String.valueOf(TaxiConstants.TAXI_STATUS_ID));
				output.writeUTF(TaxiConstants.plateNo);
				
				if(TaxiConstants.hasClient)
					output.writeUTF("O");
				
				else
					output.writeUTF("V");
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
			try
			{
				if(socket != null)
					socket.close();

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