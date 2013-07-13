package com.example.taxi;

import com.google.android.gms.maps.model.LatLng;

public class TaxiConstants {
	
	//SERVER IP ADDRESS
	public static final String SERVERIP = "192.168.1.115";
				
	//SERVERPORT NUMBER
	public static final int SERVERPORT = 8888;
	public static final int TAXIPORT = 8000;
			
	//TAXI ID
	public static final int TAXI_LOGIN_ID = 1;
	public static final int TAXI_STATUS_ID = 2;
	public static final int TAXI_COORDINATES_ID = 3;
	
	public static String plateNo;
	public static boolean isVacant = true;
	public static boolean hasClient = false;
	public static LatLng curLatLng, clientLatLng, destLatLng;
	
}