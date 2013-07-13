package com.example.client;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TaxiInfo extends Activity{
	
	private TextView plateno, bodyno, taxicomp, taxidesc, drivername, driverno;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.taxi_info);
		
		String[] taxiData = getIntent().getStringArrayExtra("server_msg");
		
		plateno = (TextView) findViewById(R.id.plateno);
		plateno.append(taxiData[0]);
		
		bodyno = (TextView) findViewById(R.id.bodyno);
		bodyno.append(taxiData[1]);
		
		taxicomp = (TextView) findViewById(R.id.taxicomp);
		taxicomp.append(taxiData[2]);
		
		taxidesc = (TextView) findViewById(R.id.taxidesc);
		taxidesc.append(taxiData[3]);
		
		drivername = (TextView) findViewById(R.id.drivername);
		drivername.append(taxiData[4]);
		
		driverno = (TextView) findViewById(R.id.driverno);
		driverno.append(taxiData[5]);
	}
}
