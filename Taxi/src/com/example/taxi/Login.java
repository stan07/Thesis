package com.example.taxi;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends Activity{
	
	private EditText plateNo, license;
	private TextView failed;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.taxi_login);
		
		plateNo = (EditText)findViewById(R.id.plateno);
		license = (EditText)findViewById(R.id.license);
		failed = (TextView)findViewById(R.id.failed);
		
		Button connect = (Button)findViewById(R.id.connect);
		connect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String IP = getIPAddress();
				
				if(IP != null)
				{
					String[] loginData = new String[3]; 
					loginData[0] = plateNo.getText().toString();
					loginData[1] = license.getText().toString();
					loginData[2] = IP; 
				
					if(!loginData[0].equals("") && !loginData[1].equals(""))
						new ConnectServerAsyncTask(Login.this).execute(loginData);
				}
				
				else
					failed.setText(IP);
			}
		});
	}
	
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		String extra = getIntent().getStringExtra("calling_class");
		
		if(extra.equals("ConnectServerAsyncTask"))
			failed.setText("Connection Failed!");
	}
	
	private String getIPAddress() {
        try 
        {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            
            for (NetworkInterface intf : interfaces) 
            {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
            
                for (InetAddress addr : addrs) 
                {
                    if (!addr.isLoopbackAddress()) 
                    {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
                        
                        if (isIPv4) 
                        	return sAddr;
                    }
                }
            }
        } catch (Exception e) { 
        	e.printStackTrace();
        } 
        return null;
    }
}
