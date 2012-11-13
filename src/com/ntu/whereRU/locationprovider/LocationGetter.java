package com.ntu.whereRU.locationprovider;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationGetter implements ILocationWorker {
	private Context context;
	private LocationManager locationManager;
	private Location myLocation;

	final private LocationListener gpsListener = new LocationListener() {

		public void onLocationChanged(Location location) {
			locationManager.removeUpdates(this);
			locationManager.removeUpdates(networkLstnr);
			if (myLocation == null) {
				myLocation = location;
				return;
			} else {
				if (myLocation.getTime() < location.getTime())
					myLocation = location;
			}

		}

		public void onProviderDisabled(String provider) {

		}

		public void onProviderEnabled(String provider) {

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	};

	final private LocationListener networkLstnr = new LocationListener() {

		public void onLocationChanged(Location location) {
			locationManager.removeUpdates(this);
			locationManager.removeUpdates(gpsListener);
			if (myLocation == null) {
				myLocation = location;
				return;
			} else {
				if (myLocation.getTime() < location.getTime())
					myLocation = location;
			}

		}

		public void onProviderDisabled(String provider) {

		}

		public void onProviderEnabled(String provider) {

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	};

	public LocationGetter(Context context) {
		if (context == null)
			throw new IllegalArgumentException("context == null");

		this.context = context;

	}

	public Location getUserLocation(int maxWaitingTime, int updateTimeout) {
		this.locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		Location networkLocation = null, gpsLocation = null;
		boolean gpsEnabled = false;
		boolean networkEnabled = false;
		try {
			gpsEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		try {
			networkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}
		if (gpsEnabled) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
			gpsLocation = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		if (networkEnabled) {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, networkLstnr);
			networkLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}

		if (gpsLocation != null && networkLocation != null) {
			if (gpsLocation.getTime() > networkLocation.getTime())
				return gpsLocation;
			else
				return networkLocation;
		}

		if (gpsLocation != null) {
			return gpsLocation;
		}
		if (networkLocation != null) {
			return networkLocation;
		}
		return myLocation;
	}
}
