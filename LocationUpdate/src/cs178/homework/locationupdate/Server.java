package cs178.homework.locationupdate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Server extends Activity{
	
	//DEFAULT IP
	public static String IP = "10.0.2.15";
	
	//DESIGNATE A PORT
	public static final int PORT = 8000;
	
	private TextView status;
	private Button connect, disconnect, back;
	private Handler handler = new Handler();
	private ServerSocket socket;
	private String line;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server);
		status = (TextView) findViewById(R.id.status);
		connect = (Button) findViewById(R.id.connect);
		disconnect = (Button) findViewById(R.id.disconnect);
		back = (Button) findViewById(R.id.serverback);
		
		connect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.w("IP Address 1", IP);
				IP = getIpAddress();
				Log.w("IP Address 2", IP);
				
				Thread thread = new Thread(new ServerThread());
				thread.start();		
			}
		});
		
		
		disconnect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//CLOSE THE SOCKET UPON DISCONNECTING
				try{
					socket.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Server.this.finish();				
			}
		});
		
	}
		
	private String getIpAddress() {
		try{
			for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); )
			{
				NetworkInterface ni = en.nextElement();
				
				for(Enumeration<InetAddress> enIp = ni.getInetAddresses(); enIp.hasMoreElements(); )
				{
					InetAddress inet = enIp.nextElement();
					
					if(!inet.isLoopbackAddress())
						return inet.getHostAddress().toString();
				}
			}
		
		} catch(SocketException se) {
			Log.e("SocketException", se.toString());
		}
		
		return null;
	}
	
	
	public class ServerThread implements Runnable {

		@Override
		public void run() {
			try{
				if(IP != null)
				{
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							status.setText("Listening on IP: " + IP);	
						}
					});
					
					socket = new ServerSocket(PORT);
					
					while(true)
					{
						Socket client = socket.accept();
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								status.setText("Connected...");
							}
						});
						
						try{
							BufferedReader buffer = new BufferedReader(new InputStreamReader(client.getInputStream()));
							
							line = null;
							while((line = buffer.readLine()) != null)
							{
								Log.d("Server Activity", line);
								handler.post(new Runnable() {
									
									@Override
									public void run() {
										Toast.makeText(Server.this, line, Toast.LENGTH_LONG).show();								
									}
								});
							}
							break;
							
						} catch(Exception e) {
							handler.post(new Runnable() {
								
								@Override
								public void run() {
									status.setText("Disconnected! Please reconnect your phones...");									
								}
							});
							e.printStackTrace();
						}
					}
				}
				
				else
				{
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							status.setText("Internet connection not available...");							
						}
					});
				}
			} catch(Exception e) {
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						status.setText("Error");
					}
				});
				e.printStackTrace();
			}	
		}
	}
}
