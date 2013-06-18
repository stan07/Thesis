package com.example.taxi;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class TaxiMap extends FragmentActivity implements LocationListener{
	
	public static GoogleMap map;
	public static Polyline line;
	
	private String provider;
	private Button vacant, occupied;
	private LocationManager locationManager;
	private static Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.taxi_map);
		context = this;
		
		vacant = (Button) findViewById(R.id.vacant);
		vacant.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!TaxiConstants.isVacant)
				{
					TaxiConstants.isVacant = true;
					TaxiConstants.hasClient = false;
					setMarkers();
					new ListenServerAsyncTask().execute();
				}
				
				else
					Toast.makeText(TaxiMap.this, "Taxi is already vacant", Toast.LENGTH_SHORT).show();
			}
		});
		
		occupied = (Button) findViewById(R.id.occupied);
		occupied.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(TaxiConstants.isVacant)
				{
					TaxiConstants.isVacant = false;
					TaxiConstants.hasClient = true;
					setMarkers();
				}
				
				else
					Toast.makeText(TaxiMap.this, "Taxi is already occupied", Toast.LENGTH_SHORT).show();
			}
		});
		
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		
		if(map != null)
		{
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			provider = locationManager.getBestProvider(criteria, true);
			
			locationManager.requestLocationUpdates(provider, 300000, 500, TaxiMap.this);
		}
		
		new ListenServerAsyncTask().execute();
	}
	
	@Override
	public void onLocationChanged(Location location) {
		TaxiConstants.curLatLng = new LatLng(location.getLatitude(), location.getLongitude());		
		setMarkers();
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
		        Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
		        Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	public static void setMarkers() {
		if(line != null)
			map.clear();

		Marker curMarker = TaxiMap.map.addMarker(new MarkerOptions().position(TaxiConstants.curLatLng).title("Current Location"));
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(TaxiConstants.curLatLng, 16), 2000, null);

		if(TaxiConstants.hasClient)
		{
			String url = "";
			
			if(TaxiConstants.isVacant)
			{
				Marker clientMarker = TaxiMap.map.addMarker(new MarkerOptions().position(TaxiConstants.clientLatLng).title("Client Location"));
				url = makeJsonCompatibleUrlStr(TaxiConstants.curLatLng.latitude, TaxiConstants.curLatLng.longitude, TaxiConstants.clientLatLng.latitude, TaxiConstants.clientLatLng.longitude);
			}
			
			else
			{
				Marker destMarker =TaxiMap.map.addMarker(new MarkerOptions().position(TaxiConstants.destLatLng).title("Destination Location"));
				url = makeJsonCompatibleUrlStr(TaxiConstants.curLatLng.latitude, TaxiConstants.curLatLng.longitude, TaxiConstants.destLatLng.latitude, TaxiConstants.destLatLng.longitude);
			}
			
			new DrawPathAsyncTask(context, url).execute();
		}
		
		new SendServerAsyncTask().execute();
	}
	
	private static String makeJsonCompatibleUrlStr(double srclatt, double srclong, double destlatt, double destlong) {
		StringBuilder url = new StringBuilder();
		url.append("http://maps.googleapis.com/maps/api/directions/json");
		url.append("?origin=");		
		url.append(Double.toString(srclatt));
		url.append(",");
		url.append(Double.toString(srclong));
		url.append("&destination=");	
		url.append(Double.toString(destlatt));
		url.append(",");
		url.append(Double.toString(destlong));
		url.append("&sensor=false&mode=driving&alternatives=true");
		return url.toString();
	}
}
