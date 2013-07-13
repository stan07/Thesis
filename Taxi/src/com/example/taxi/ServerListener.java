package com.example.taxi;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.model.LatLng;

public class ServerListener implements Runnable{
	
	public static ServerSocket taxiSocket;

	@Override
	public void run() {
		Socket socket = null;
		DataInputStream input = null;
		
		try 
		{
			taxiSocket = new ServerSocket(TaxiConstants.TAXIPORT);
			socket = taxiSocket.accept();
			input = new DataInputStream(socket.getInputStream());

			String[] coordinates = new String[4];
			coordinates[0] = input.readUTF();
			coordinates[1] = input.readUTF();
			coordinates[2] = input.readUTF();
			coordinates[3] = input.readUTF();
			
			try 
			{
				TaxiConstants.clientLatLng = new LatLng(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]));
				TaxiConstants.destLatLng = new LatLng(Double.parseDouble(coordinates[2]), Double.parseDouble(coordinates[3]));
				TaxiConstants.hasClient = true;
				new SendServerAsyncTask().execute(TaxiConstants.TAXI_STATUS_ID);
				
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
				if(taxiSocket != null)
					taxiSocket.close();

				if(socket != null)
					socket.close();

				if(input != null)
					input.close();
				
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
