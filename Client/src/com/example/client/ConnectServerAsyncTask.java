package com.example.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.android.gms.maps.model.LatLng;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class ConnectServerAsyncTask extends AsyncTask<Void, Void, String[]>{
	
	//SERVER IP ADDRESS
	private final String SERVERIP = "192.168.1.115";
		
	//PORT NUMBER
	private final int PORT = 8888;
	
	//CLIENT ID
	private final int CLIENT_ID = 4;
	
	//ASYNCTASK VARIABLES
	private Context context;
	private LatLng cur, dest;
	private ProgressDialog progressDialog;
	
	//DATA COMMUNICATION VARIABLES
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	
	public ConnectServerAsyncTask(Context clientMapContext, LatLng curLatLng, LatLng destLatLng) {
		context = clientMapContext;
		cur = curLatLng;
		dest = destLatLng;
	}
	
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage("Retrieving data of taxi, please wait...");
		progressDialog.setIndeterminate(true);
		progressDialog.show();
	}
	
	@Override
	protected String[] doInBackground(Void... params) {
		return openConnection();
	}
	
	protected void onPostExecute(String[] result) {
		super.onPostExecute(result);
		progressDialog.hide();
		Intent intent = new Intent(context, TaxiInfo.class);
		intent.putExtra("server_msg", result);
		context.startActivity(intent);
	}
	
	private String[] openConnection() {
		String[] taxiData = new String[6];
		
		try
		{
			socket = new Socket(SERVERIP, PORT);
			
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
			
			//SEND CLIENT ID TO SERVER
			output.writeUTF(String.valueOf(CLIENT_ID));
			
			//SEND COORDINATES TO SERVER
			String clientData = cur.latitude + "," + cur.longitude + "," 
								 + dest.latitude + "," + dest.longitude;
			output.writeUTF(clientData);
			
			//READ SERVER MESSAGE
			taxiData[0] = input.readUTF();
			taxiData[1] = input.readUTF();
			taxiData[2] = input.readUTF();
			taxiData[3] = input.readUTF();
			taxiData[4] = input.readUTF();
			taxiData[5] = input.readUTF();
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
		return taxiData;
	}
}
