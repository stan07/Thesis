package com.example.taxi;

import com.google.android.gms.maps.model.LatLng;

public class TaxiConstants {
	
	//SERVER IP ADDRESS
	public static final String SERVERIP = "192.168.1.117";
				
	//PORT NUMBER
	public static final int PORT = 8888;
			
	//CLIENT ID
	public static final int TAXIID = 2;
	
	public static boolean isVacant = true;
	public static boolean hasClient = false;
	public static LatLng curLatLng, clientLatLng, destLatLng;
	
}
