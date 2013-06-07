package com.example.mapstest;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class SecondActivity extends Activity implements LocationListener, LocationSource {
  static final LatLng HAMBURG = new LatLng(53.558, 9.927);
  static final LatLng KIEL = new LatLng(53.551, 9.993);
  private GoogleMap map;
  private LocationManager locationManager;
  private OnLocationChangedListener mListener;
  private FollowMeLocationSource followMeLocationSource;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
        .getMap();
    
    map.setMyLocationEnabled(true);
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    String provider = locationManager.getBestProvider(new Criteria(), false);
    Location location = locationManager.getLastKnownLocation(provider);
    LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
    followMeLocationSource = new FollowMeLocationSource(locationManager);
    
    // Move the camera instantly to hamburg with a zoom of 15.
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15));

    // Zoom in, animating the camera.
    map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
  }

  @Override
  public void onResume() {
	  super.onResume();
	  followMeLocationSource.getBestAvailableProvider();
	  map.setMyLocationEnabled(true);
  }
  
  @Override
  public void onPause() {
	  map.setMyLocationEnabled(false);
	  super.onPause();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
  
  @Override
  public void activate(OnLocationChangedListener listener) {
	  mListener = listener;
  }
  
  @Override
  public void deactivate() {
	  mListener = null;	
  }

  @Override
  public void onLocationChanged(Location location) {
	  if (mListener != null) {
		  mListener.onLocationChanged(location);
		  LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
		  map.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
	  }
  }

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "provider disabled", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "provider ensabled", Toast.LENGTH_SHORT).show();	
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Toast.makeText(this, "Status changed", Toast.LENGTH_SHORT).show();		
	}

	 private class FollowMeLocationSource implements LocationSource, LocationListener {

	        private OnLocationChangedListener mListener;
	        private LocationManager locationManager;
	        private final Criteria criteria = new Criteria();
	        private String bestAvailableProvider;
	        /* Updates are restricted to one every 10 seconds, and only when
	         * movement of more than 10 meters has been detected.*/
	        private final int minTime = 10000;     // minimum time interval between location updates, in milliseconds
	        private final int minDistance = 10;    // minimum distance between location updates, in meters

	        private FollowMeLocationSource() {

	            // Specify Location Provider criteria
	            criteria.setAccuracy(Criteria.ACCURACY_FINE);
	            criteria.setPowerRequirement(Criteria.POWER_LOW);
	            criteria.setAltitudeRequired(true);
	            criteria.setBearingRequired(true);
	            criteria.setSpeedRequired(true);
	            criteria.setCostAllowed(true);
	        }
	        
	        private FollowMeLocationSource(LocationManager locationManager) {
	        	this.locationManager = locationManager;
	        	criteria.setAccuracy(Criteria.ACCURACY_FINE);
	            criteria.setPowerRequirement(Criteria.POWER_LOW);
	            criteria.setAltitudeRequired(true);
	            criteria.setBearingRequired(true);
	            criteria.setSpeedRequired(true);
	            criteria.setCostAllowed(true);
	        }

	        private void getBestAvailableProvider() {
	            /* The preffered way of specifying the location provider (e.g. GPS, NETWORK) to use 
	             * is to ask the Location Manager for the one that best satisfies our criteria.
	             * By passing the 'true' boolean we ask for the best available (enabled) provider. */
	            bestAvailableProvider = locationManager.getBestProvider(criteria, true);
	        }

	        /* Activates this provider. This provider will notify the supplied listener
	         * periodically, until you call deactivate().
	         * This method is automatically invoked by enabling my-location layer. */
	        @Override
	        public void activate(OnLocationChangedListener listener) {
	            // We need to keep a reference to my-location layer's listener so we can push forward
	            // location updates to it when we receive them from Location Manager.
	            mListener = listener;

	            // Request location updates from Location Manager
	            if (bestAvailableProvider != null) {
	                locationManager.requestLocationUpdates(bestAvailableProvider, minTime, minDistance, this);
	            } else {
	                // (Display a message/dialog) No Location Providers currently available.
	            }
	        }

	        /* Deactivates this provider.
	         * This method is automatically invoked by disabling my-location layer. */
	        @Override
	        public void deactivate() {
	            // Remove location updates from Location Manager
	            locationManager.removeUpdates(this);

	            mListener = null;
	        }

	        @Override
	        public void onLocationChanged(Location location) {
	            /* Push location updates to the registered listener..
	             * (this ensures that my-location layer will set the blue dot at the new/received location) */
	            if (mListener != null) {
	                mListener.onLocationChanged(location);
	            }

	            /* ..and Animate camera to center on that location !
	             * (the reason for we created this custom Location Source !) */
	            map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
	        }

	        @Override
	        public void onStatusChanged(String s, int i, Bundle bundle) {

	        }

	        @Override
	        public void onProviderEnabled(String s) {

	        }

	        @Override
	        public void onProviderDisabled(String s) {

	        }
	    }
	
} 