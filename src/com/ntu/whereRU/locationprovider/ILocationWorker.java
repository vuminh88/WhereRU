package com.ntu.whereRU.locationprovider;

import android.location.Location;

public interface ILocationWorker {
	/**
	 * 
	 * @param maxWaitingTime: time to wait for a location request. The unit is ms
	 * @param updateTimeout: time out. If the waiting time is longer than this, stop requesting. The unit is ms.
	 * @return
	 */
	public Location getUserLocation(int maxWaitingTime, int updateTimeout);
}
