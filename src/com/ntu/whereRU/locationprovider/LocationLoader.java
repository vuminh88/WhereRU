package com.ntu.whereRU.locationprovider;

import com.ntu.whereRU.locationprovider.LocationResolver.LocationResult;

import android.content.Context;
import android.location.Location;
import android.os.Looper;


/**
 * Implement ILocationWorker to get current user's location.
 * 
 * @author ZungLe
 * 
 */
public class LocationLoader implements ILocationWorker {
	private final Context context;
	private Location location = null;
	private final Object gotLocationLock = new Object();
	private final LocationResult locationResult = new LocationResult() {
		@Override
		public void gotLocation(Location location) {
			synchronized (gotLocationLock) {
				LocationLoader.this.location = location;
				gotLocationLock.notifyAll();
				Looper.myLooper().quit();
			}
		}
	};

	public LocationLoader(Context context) {
		if (context == null)
			throw new IllegalArgumentException("context == null");

		this.context = context;
	}

	public synchronized Location getUserLocation(int maxWaitingTime,
			int updateTimeout) {
		try {
			final int updateTimeoutPar = updateTimeout;
			synchronized (gotLocationLock) {
				new Thread() {
					public void run() {
						Looper.prepare();
						LocationResolver locationResolver = new LocationResolver();
						locationResolver.prepare();
						locationResolver.getLocation(context, locationResult,
								updateTimeoutPar);
						Looper.loop();
					}
				}.start();

				gotLocationLock.wait(maxWaitingTime);
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		return location;
	}

}
