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
import android.content.Intent;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint({ "NewApi", "ShowToast" })
public class MainActivity extends Activity implements LocationListener{
	
	private GoogleMap map;
	private String provider;
	/*private static String SENT = "SMS_SENT";
    private static String DELIVERED = "SMS_DELIVERED";*/
	private Marker position;
<<<<<<< HEAD
	private Button prev, next, send;
=======
	private Button start, stop;
>>>>>>> parent of aa728f1... Test Project Committed 3
	//private double prevlat, prevlon;
	private LocationManager locationManager;
	final LatLng MAIN = new LatLng(10.30046, 123.88822);
	private LatLng[] paths;
<<<<<<< HEAD
	private int lastDataIndex = -1, curIdx;
	private boolean isClicked = false;
	
=======
	private int index = -1, curIdx;
	private boolean isClicked = false;
>>>>>>> parent of aa728f1... Test Project Committed 3
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		prev = (Button) findViewById(R.id.prev);
		next = (Button) findViewById(R.id.next);
		send = (Button) findViewById(R.id.send);
		
		if(map != null)
		{	
<<<<<<< HEAD
			
=======
>>>>>>> parent of aa728f1... Test Project Committed 3
			paths = new LatLng[10];
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			Criteria criteria = new Criteria();
			provider = locationManager.getBestProvider(criteria, false);	//uses gps as default
			
			locationManager.requestLocationUpdates(provider, 1000, 10, MainActivity.this);
<<<<<<< HEAD
			
			if(getIntent().hasExtra("sms_location"))
				getCoordinates();
=======
>>>>>>> parent of aa728f1... Test Project Committed 3
			
			prev.setOnClickListener(new OnClickListener() {
					
				@Override
				public void onClick(View v) {
					/*Location location = locationManager.getLastKnownLocation(provider);
					
					if(location != null)
					{
						prevlat = location.getLatitude();
						prevlon = location.getLongitude();
					}*/
					
					//locationManager.requestLocationUpdates(provider, 1000, 10, MainActivity.this);
					
					if(curIdx > 0)
					{
						isClicked = true;
						curIdx--;
						Location location = new Location("");
						location.setLatitude(paths[curIdx].latitude);
						location.setLongitude(paths[curIdx].longitude);
						onLocationChanged(location);
					}
				}
			});
			
			
			next.setOnClickListener(new OnClickListener() {
					
				@Override
				public void onClick(View v) {
					/*locationManager.removeUpdates(MainActivity.this);
					Location location = locationManager.getLastKnownLocation(provider);
					
					if(location != null)							
						Toast.makeText(MainActivity.this, "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude(), Toast.LENGTH_LONG).show();
						
					else
						Toast.makeText(MainActivity.this, "Location not available", Toast.LENGTH_LONG).show();*/
					
<<<<<<< HEAD
					if(lastDataIndex < paths.length - 1)
=======
					if(index < paths.length - 1)
>>>>>>> parent of aa728f1... Test Project Committed 3
					{
						isClicked = true;
						curIdx++;
						Location location = new Location("");
						location.setLatitude(paths[curIdx].latitude);
						location.setLongitude(paths[curIdx].longitude);
						onLocationChanged(location);
					}
<<<<<<< HEAD
				}
			});
			
			
			send.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Location location = locationManager.getLastKnownLocation(provider);
					
					String msg = location.getLatitude() + ", " +
								 location.getLongitude();
					//PendingIntent intent = PendingIntent.getActivity(MainActivity.this, 0, new Intent(SENT), 0);
					SmsManager sms = SmsManager.getDefault();
					sms.sendTextMessage("5556", null, msg, null, null);
					
					/*Intent smsIntent = new Intent(Intent.ACTION_VIEW);
					smsIntent.putExtra("sms_location", location);
					smsIntent.setType("vnd.android-dir/mms-sms");
					startActivity(smsIntent);*/
=======
>>>>>>> parent of aa728f1... Test Project Committed 3
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
		
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		getCoordinates();
	}
	
	
	private void getCoordinates() {
		Intent intent = getIntent();
		String sms = intent.getStringExtra("sms_location");
		String[] coordinates = sms.split(",");
		coordinates[1] = coordinates[1].trim();
		Location location = new Location("");
		location.setLatitude(Double.parseDouble(coordinates[0]));
		location.setLongitude(Double.parseDouble(coordinates[1]));
		onLocationChanged(location);
	}
	
	
	@Override
	public void onLocationChanged(Location location) {
		double curlat = location.getLatitude(), curlon = location.getLongitude();
		LatLng mCurrPos = new LatLng(curlat, curlon);
		
		if(isClicked == false)
		{
<<<<<<< HEAD
			lastDataIndex++;
			paths[lastDataIndex] = mCurrPos;
			curIdx = lastDataIndex;
=======
			index++;
			paths[index] = pos;
			curIdx = index;
>>>>>>> parent of aa728f1... Test Project Committed 3
		}
		
		else
			isClicked = false;
			
		if(position != null)
			map.clear();
			
<<<<<<< HEAD
		if(mCurrPos == MAIN)
=======
		if(pos == MAIN)
>>>>>>> parent of aa728f1... Test Project Committed 3
		{
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(MAIN, 16), 2000, null);
			Toast.makeText(MainActivity.this, "You have reached your final destination!", Toast.LENGTH_LONG).show();
		}
		
		else
		{
			Marker main = map.addMarker(new MarkerOptions().position(MAIN));
<<<<<<< HEAD
			position = map.addMarker(new MarkerOptions().position(mCurrPos));
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrPos, 16), 2000, null);
=======
			position = map.addMarker(new MarkerOptions().position(pos));
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 16), 2000, null);
>>>>>>> parent of aa728f1... Test Project Committed 3
		
			//makes a string to be converted as a JSON object for drawing paths on the map between markers
			String url = makeJsonCompatibleUrlStr(curlat, curlon, MAIN.latitude, MAIN.longitude); 
			new DrawPathAsyncTask(url).execute(); //allocates another thread for executing connection of paths between markers
		
			/*prevlat = curlat;
			prevlon = curlon;*/
		}
	}
	
	public String makeJsonCompatibleUrlStr(double srclatt, double srclong, double destlatt, double destlong) {
		StringBuilder url = new StringBuilder();
		
		url.append("http://maps.googleapis.com/maps/api/directions/json");
		url.append("?origin=");		//source
		url.append(Double.toString(srclatt));
		url.append(",");
		url.append(Double.toString(srclong));
		url.append("&destination=");	//destination
		url.append(Double.toString(destlatt));
		url.append(",");
		url.append(Double.toString(destlong));
		url.append("&sensor=false&mode=driving&alternatives=true");
		
		return url.toString();
	}	
	
	private class DrawPathAsyncTask extends AsyncTask<Void, Void, String> {
		//parameters for onPreExecute, doInBackground, onPostExecute respectively
		
		private ProgressDialog progressDialog;
		String mStringUrl;
		
		public DrawPathAsyncTask(String stringUrl){
			this.mStringUrl = stringUrl; //this url is created by the makeUrl() function
		}
		
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setMessage("Tracing route between markers, please wait...");
			progressDialog.show();
		}
		
		@Override
		protected String doInBackground(Void... arg0) {
			JSONParser jParser = new JSONParser();
			String mJsonizedStringUrl = jParser.getJSONfromURL(mStringUrl);
			return mJsonizedStringUrl;
		}
		
		protected void onPostExecute(String jsonizedStringUrlResult) {
			super.onPostExecute(jsonizedStringUrlResult);
			progressDialog.hide();
			
			if(jsonizedStringUrlResult != null)
				drawPath(jsonizedStringUrlResult);
		}		
	}
	
	public void drawPath(String jsonizedStringUrl) {
		try{
			
			final JSONObject json = new JSONObject(jsonizedStringUrl); //json object
			JSONArray routeArray = json.getJSONArray("routeArrayAsString");
			
			JSONObject routes = routeArray.getJSONObject(0);
			JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
			String encodedString = overviewPolylines.getString("points");
			List<LatLng> list = decodePoly(encodedString);
			
			for(int i = 0; i < list.size()-1; i++){ //-1 because it is using a look ahead manipulation method to prevent array out of bounds
				LatLng src = list.get(i);
				LatLng dest = list.get(i+1);
				Polyline line = map.addPolyline(new PolylineOptions().add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
				.width(2).color(Color.RED).geodesic(true));
			}
			
		} catch (JSONException e) {

	    }
	}
	
	private List<LatLng> decodePoly(String encodedStr) {

	    List<LatLng> poly = new ArrayList<LatLng>();
	    int index = 0, len = encodedStr.length();
	    int lat = 0, lng = 0;

	    while (index < len) {
	        int b, shift = 0, result = 0;
	        do {
	            b = encodedStr.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do {
	            b = encodedStr.charAt(index++) - 63;
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
	
<<<<<<< HEAD
	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
		        Toast.LENGTH_SHORT).show();
=======
	private class ConnectAsyncTask extends AsyncTask<Void, Void, String> {
		
		private ProgressDialog progress;
		String url;
		
		public ConnectAsyncTask(String url){
			this.url = url;
		}
		
		protected void onPreExecute() {
			super.onPreExecute();
			progress = new ProgressDialog(MainActivity.this);
			progress.setMessage("Tracing route between markers, please wait...");
			progress.show();
		}
>>>>>>> parent of aa728f1... Test Project Committed 3
		
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
}
