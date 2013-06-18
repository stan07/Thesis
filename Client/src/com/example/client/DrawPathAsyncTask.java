package com.example.client;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class DrawPathAsyncTask extends AsyncTask<Void, Void, String> {
	//parameters for onPreExecute, doInBackground, onPostExecute respectively
	
	private Context context;
	private String url;
	private ProgressDialog progressDialog;
	
	public DrawPathAsyncTask(Context clientMapContext, String stringUrl){
		context = clientMapContext;
		url = stringUrl; //this url is created by the makeUrl() function
	}
	
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage("Tracing route between markers, please wait...");
		progressDialog.setIndeterminate(true);
		progressDialog.show();
	}
	
	@Override
	protected String doInBackground(Void... arg0) {
		JSONParser jParser = new JSONParser();
		String mJsonizedStringUrl = jParser.getJSONfromURL(url);
		return mJsonizedStringUrl;
	}
	
	protected void onPostExecute(String jsonizedStringUrlResult) {
		super.onPostExecute(jsonizedStringUrlResult);
		progressDialog.hide();
		
		if(jsonizedStringUrlResult != null)
			drawPath(jsonizedStringUrlResult);
	}		
	
	private void drawPath(String jsonizedStringUrl) {
		try{
			final JSONObject json = new JSONObject(jsonizedStringUrl); //json object
			JSONArray routeArray = json.getJSONArray("routes");
			JSONObject routes = routeArray.getJSONObject(0);
			JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
			String encodedString = overviewPolylines.getString("points");
			List<LatLng> list = decodePoly(encodedString);
			
			for(int i = 0; i < list.size()-1; i++){ //-1 because it is using a look ahead manipulation method to prevent array out of bounds
				LatLng src = list.get(i);
				LatLng dest = list.get(i+1);
				ClientMap.line = ClientMap.map.addPolyline(new PolylineOptions().add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
				.width(2).color(Color.BLUE).geodesic(true));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
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
}
