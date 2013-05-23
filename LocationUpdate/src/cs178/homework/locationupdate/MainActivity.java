package cs178.homework.locationupdate;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint({ "NewApi", "ShowToast" })
public class MainActivity extends Activity implements LocationListener{
	
	private GoogleMap map;
	private String provider;
	private Marker position;
	private Polyline line;
	private Button start, stop;
	//private double prevlat, prevlon;
	private LocationManager locationManager;
	final LatLng MAIN = new LatLng(10.30046, 123.88822);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		start = (Button) findViewById(R.id.start);
		stop = (Button) findViewById(R.id.stop);
		
		if(map != null)
		{
			Marker main = map.addMarker(new MarkerOptions().position(MAIN));
			
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			Criteria criteria = new Criteria();
			provider = locationManager.getBestProvider(criteria, false);	//uses gps as default
			
			
			start.setOnClickListener(new OnClickListener() {
					
				@Override
				public void onClick(View v) {
					/*Location location = locationManager.getLastKnownLocation(provider);
					
					if(location != null)
					{
						prevlat = location.getLatitude();
						prevlon = location.getLongitude();
					}*/
					
					locationManager.requestLocationUpdates(provider, 1000, 10, MainActivity.this);
				}
			});
			
			
			stop.setOnClickListener(new OnClickListener() {
					
				@Override
				public void onClick(View v) {
					locationManager.removeUpdates(MainActivity.this);
					Location location = locationManager.getLastKnownLocation(provider);
					
					if(location != null)							
						Toast.makeText(MainActivity.this, "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude(), Toast.LENGTH_LONG).show();
						
					else
						Toast.makeText(MainActivity.this, "Location not available", Toast.LENGTH_LONG).show();	
				}
			});
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if(position != null)
			position.remove();

		if(line !=null)
			line.remove();
		
		double curlat = location.getLatitude(), curlon = location.getLongitude();
		LatLng pos = new LatLng(curlat, curlon);
		
		if(pos == MAIN)
		{
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(MAIN, 16), 2000, null);
			Toast.makeText(MainActivity.this, "You have reached your final destination!", Toast.LENGTH_LONG).show();
		}
		
		else
		{
			position = map.addMarker(new MarkerOptions().position(pos));
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 16), 2000, null);
		
			String url = createURL(curlat, curlon, MAIN.latitude, MAIN.longitude);
			new ConnectAsyncTask(url).execute();
		
			/*prevlat = curlat;
			prevlon = curlon;*/
		}
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
	
	public String createURL(double srclat, double srclon, double destlat, double destlon) {
		StringBuilder url = new StringBuilder();
		url.append("http://maps.googleapis.com/maps/api/directions/json");
		url.append("?origin=");		//source
		url.append(Double.toString(srclat));
		url.append(",");
		url.append(Double.toString(srclon));
		url.append("&destination=");	//destination
		url.append(Double.toString(destlat));
		url.append(",");
		url.append(Double.toString(destlon));
		url.append("&sensor=false&mode=driving&alternatives=true");
		
		return url.toString();
	}
	
	public void drawPath(String result) {
		try{
			//Transform the String into a JSON object
			final JSONObject json = new JSONObject(result);
			JSONArray routeArray = json.getJSONArray("routes");
			JSONObject routes = routeArray.getJSONObject(0);
			JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
			String encodedString = overviewPolylines.getString("points");
			List<LatLng> list = decodePoly(encodedString);
			
			for(int i = 0; i < list.size()-1; i++)
			{
				LatLng src = list.get(i);
				LatLng dest = list.get(i+1);
				line = map.addPolyline(new PolylineOptions().add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
				.width(2).color(Color.RED).geodesic(true));
				
			}
		} catch (JSONException e) {

	    }
	}
	
	private List<LatLng> decodePoly(String encoded) {

	    List<LatLng> poly = new ArrayList<LatLng>();
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;

	    while (index < len) {
	        int b, shift = 0, result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;

	        LatLng p = new LatLng( (((double) lat / 1E5)),
	                 (((double) lng / 1E5) ));
	        poly.add(p);
	    }

	    return poly;
	}
	
	private class ConnectAsyncTask extends AsyncTask<Void, Void, String> {
		
		private ProgressDialog progress;
		String url;
		
		public ConnectAsyncTask(String url){
			this.url = url;
		}
		
		protected void onPreExecute() {
			super.onPreExecute();
			progress = new ProgressDialog(MainActivity.this);
			progress.setMessage("Tracing path between markers! Please wait...");
			progress.show();
		}
		
		@Override
		protected String doInBackground(Void... arg0) {
			JSONParser jParser = new JSONParser();
			String json = jParser.getJSONfromURL(url);
			return json;
		}
		
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			progress.hide();
			
			if(result != null)
				drawPath(result);
		}		
	}
}
