package com.example.mapstest;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;

public class MainActivity extends MapActivity implements LocationListener, LocationSource {
  static final LatLng HAMBURG = new LatLng(53.558, 9.927);
  static final LatLng KIEL = new LatLng(53.551, 9.993);
  private GoogleMap map;
  private LocationManager locationManager;
  private OnLocationChangedListener mListener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
        .getMap();
    map.setMyLocationEnabled(true);
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1000, this);
    String provider = locationManager.getBestProvider(new Criteria(), false);
    Location location = locationManager.getLastKnownLocation(provider);
    LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
    
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15));

    // Zoom in, animating the camera.
    map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
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
//	  if (mListener != null) {
//		  mListener.onLocationChanged(location);
		  LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
		  CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(newLatLng, 15);
		  map.animateCamera(cameraUpdate);
		  locationManager.removeUpdates(this);
//		  map.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
//	  }
  }

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, provider + " disabled", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, provider + " enabled", Toast.LENGTH_SHORT).show();	
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Toast.makeText(this, "Status changed", Toast.LENGTH_SHORT).show();		
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

} 