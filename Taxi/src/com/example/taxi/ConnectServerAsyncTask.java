package com.example.taxi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class ConnectServerAsyncTask extends AsyncTask<String, Void, Integer>{
	
	private Context context;
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	
	public ConnectServerAsyncTask(Context loginContext) {
		context = loginContext;
	}
	
	@Override
	protected Integer doInBackground(String... loginData) {
		int result = 0;
		
		try {
			socket = new Socket(TaxiConstants.SERVERIP, TaxiConstants.SERVERPORT);
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
			
			TaxiConstants.plateNo = loginData[0];
			output.writeUTF(String.valueOf(TaxiConstants.TAXI_LOGIN_ID));
			output.writeUTF(loginData[0]);
			output.writeUTF(loginData[1]);
			output.writeUTF(loginData[2]);

			String serverMsg = input.readUTF();
			result = Integer.parseInt(serverMsg);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(socket != null)
					socket.close();

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
		return result;
	}
	
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		Intent intent;

		if(result > 0)
			intent = new Intent(context, TaxiMap.class);
		
		else
		{
			TaxiConstants.plateNo = "";
			intent = new Intent(context, Login.class);
			intent.putExtra("calling_class", "ConnectServerAsyncTask");
			intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		}
		
		context.startActivity(intent);
	}
}