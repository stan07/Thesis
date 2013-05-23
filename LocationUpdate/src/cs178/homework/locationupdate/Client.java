package cs178.homework.locationupdate;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Client extends Activity{
	
	private EditText serverIp;
	private Button connect;
	private String serverIpAddress = "";
	private boolean connected = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client);
		
		serverIp = (EditText) findViewById(R.id.server_ip);
		
		connect = (Button) findViewById(R.id.connect);
		
		connect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!connected)
				{
					serverIpAddress = serverIp.getText().toString();
					
					if(!serverIpAddress.equals(""))
					{
						Thread thread = new Thread(new ClientThread());
						thread.start();
					}
				}
				
			}
		});
	}
	
	
	public class ClientThread implements Runnable {

		@Override
		public void run() {
			try{
				InetAddress serverAddress = InetAddress.getByName(serverIpAddress);
				Log.d("Client Activity", "Connecting...");
				Socket socket = new Socket(serverAddress, Server.PORT);
				connected = true;
				
				while(connected)
				{
					try{
						Log.d("Client Activity", "Sending command...");
						PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
						
						//WHERE YOU ISSUE THE COMMANDS
						out.println("HEY SERVER!");
						
						Log.d("Client Activity", "Sent!");
					
					} catch(Exception e) {
						Log.e("Client Activity", "Error!", e);
					}
				}
				socket.close();
				Log.d("Client Activity", "Closed!");
			
			} catch(Exception e) {
				Log.e("Client Activity", "Error!", e);
				connected = false;
			}
		}
	}
}
