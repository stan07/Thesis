package com.example.client;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

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

public class ClientMap extends FragmentActivity implements LocationListener{
	
	public static GoogleMap map;
	public static Polyline line;
	
	private LatLng curLatLng, destLatLng;
	private LocationManager locationManager;
	
	private String provider;
	private Button confirm, exit;
	private boolean isMapClicked = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_map);
		
		confirm = (Button) findViewById(R.id.confirm);
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isMapClicked)
					new ConnectServerAsyncTask(ClientMap.this, curLatLng, destLatLng).execute();
			}
		});
		
		exit = (Button) findViewById(R.id.exit);
		exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ClientMap.this.finish();
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
			
			locationManager.requestLocationUpdates(provider, 1000, 10, ClientMap.this);
			
			map.setOnMapClickListener(new OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng point) {
					if(!isMapClicked)
						isMapClicked = true;
					
					destLatLng = point;				
					setMarkers();
				}
			});
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		curLatLng = new LatLng(location.getLatitude(), location.getLongitude());
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, 16), 2000, null);
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
	
	private void setMarkers() {
		if(line != null)
			map.clear();
			
		Marker curMarker = map.addMarker(new MarkerOptions().position(curLatLng).title("Current Location"));
		
		if(isMapClicked)
		{
			Marker destMarker = map.addMarker(new MarkerOptions().position(destLatLng).title("Destination Location"));
			String url = makeJsonCompatibleUrlStr(curLatLng.latitude, curLatLng.longitude, destLatLng.latitude, destLatLng.longitude); 
			new DrawPathAsyncTask(ClientMap.this, url).execute();
		}
	}
	
	private String makeJsonCompatibleUrlStr(double srclatt, double srclong, double destlatt, double destlong) {
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