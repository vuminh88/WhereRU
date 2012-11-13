package com.ntu.whereRU.skeleton.data.requestmanager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.SparseArray;

import com.ntu.whereRU.data.memprovider.MemoryProvider;
import com.ntu.whereRU.requestmanager.RequestManager;
import com.ntu.whereRU.skeleton.data.service.SkeletonService;

/**
 * This class is used as a proxy to call the Service. It provides easy-to-use
 * methods to call the service and manages the Intent creation. It also assures
 * that a request will not be sent again if an exactly identical one is already
 * in progress
 * 
 * @author ZungLe
 */
public class SkeletonRequestManager extends RequestManager {

	private static final int MAX_RANDOM_REQUEST_ID = 1000000;
	private static Random sRandom = new Random();
	private MemoryProvider mMemoryProvider = MemoryProvider.getInstance();

	// Singleton management
	private static SkeletonRequestManager sInstance;

	public static SkeletonRequestManager from(final Context context) {
		if (sInstance == null) {
			sInstance = new SkeletonRequestManager(context);
		}

		return sInstance;
	}

	private SparseArray<Intent> mRequestSparseArray;
	private Context mContext;
	private ArrayList<WeakReference<OnRequestFinishedListener>> mListenerList;
	private Handler mHandler = new Handler();
	private EvalReceiver mEvalReceiver = new EvalReceiver(mHandler);

	private SkeletonRequestManager(final Context context) {
		mContext = context.getApplicationContext();
		mRequestSparseArray = new SparseArray<Intent>();
		mListenerList = new ArrayList<WeakReference<OnRequestFinishedListener>>();
	}

	/**
	 * The ResultReceiver that will receive the result from the Service
	 */
	private class EvalReceiver extends ResultReceiver {
		EvalReceiver(final Handler h) {
			super(h);
		}

		@Override
		public void onReceiveResult(final int resultCode,
				final Bundle resultData) {
			handleResult(resultCode, resultData);
		}
	}

	/**
	 * Clients may implements this interface to be notified when a request is
	 * finished
	 * 
	 * @author ZungLe
	 */
	public static interface OnRequestFinishedListener extends EventListener {

		/**
		 * Event fired when a request is finished.
		 * 
		 * @param requestId
		 *            The request Id (to see if this is the right request)
		 * @param resultCode
		 *            The result code (0 if there was no error)
		 * @param payload
		 *            The result of the service execution.
		 */
		public void onRequestFinished(int requestId, int resultCode,
				Bundle payload);
	}

	/**
	 * Add a {@link OnRequestFinishedListener} to this
	 * {@link SkeletonRequestManager}. Clients may use it in order to listen to
	 * events fired when a request is finished.
	 * <p>
	 * <b>Warning !! </b> If it's an {@link Activity} that is used as a
	 * Listener, it must be detached when {@link Activity#onPause} is called in
	 * an {@link Activity}.
	 * </p>
	 * 
	 * @param listener
	 *            The listener to add to this {@link SkeletonRequestManager} .
	 */
	public void addOnRequestFinishedListener(
			final OnRequestFinishedListener listener) {
		synchronized (mListenerList) {
			// Check if the listener is not already in the list
			if (!mListenerList.isEmpty()) {
				for (WeakReference<OnRequestFinishedListener> weakRef : mListenerList) {
					if (weakRef.get().equals(listener)) {
						return;
					}
				}
			}

			mListenerList.add(new WeakReference<OnRequestFinishedListener>(
					listener));
		}
	}

	/**
	 * Remove a {@link OnRequestFinishedListener} to this
	 * {@link SkeletonRequestManager}.
	 * 
	 * @param listenerThe
	 *            listener to remove to this {@link SkeletonRequestManager}.
	 */
	public void removeOnRequestFinishedListener(
			final OnRequestFinishedListener listener) {
		synchronized (mListenerList) {
			final int listenerListSize = mListenerList.size();
			for (int i = 0; i < listenerListSize; i++) {
				if (mListenerList.get(i).get().equals(listener)) {
					mListenerList.remove(i);
					return;
				}
			}
		}
	}

	/**
	 * Return whether a request (specified by its id) is still in progress or
	 * not
	 * 
	 * @param requestId
	 *            The request id
	 * @return whether the request is still in progress or not.
	 */
	public boolean isRequestInProgress(final int requestId) {
		return (mRequestSparseArray.indexOfKey(requestId) >= 0);
	}

	/**
	 * This method is call whenever a request is finished. Call all the
	 * available listeners to let them know about the finished request
	 * 
	 * @param resultCode
	 *            The result code of the request
	 * @param resultData
	 *            The bundle sent back by the service
	 */
	protected void handleResult(final int resultCode, final Bundle resultData) {

		// Get the request Id
		final int requestId = resultData.getInt(RECEIVER_EXTRA_REQUEST_ID);

		// Remove the request Id from the "in progress" request list
		mRequestSparseArray.remove(requestId);

		// Call the available listeners
		synchronized (mListenerList) {
			for (int i = 0; i < mListenerList.size(); i++) {
				final WeakReference<OnRequestFinishedListener> weakRef = mListenerList
						.get(i);
				final OnRequestFinishedListener listener = weakRef.get();
				if (listener != null) {
					listener.onRequestFinished(requestId, resultCode,
							resultData);
				} else {
					mListenerList.remove(i);
					i--;
				}
			}
		}
	}

	/**
	 * 
	 * @param returnFormat
	 *            0 for XML, 1 for JSON
	 * @param userLocation
	 *            : the current location of the user. This function will scan
	 *            the list of nearby friend based on this location
	 * 
	 * @return requestID
	 * @author ZungLe
	 */
	public int getNearbyFriendsActivity(final int returnFormat,
			final Location userLocation) {
		final int mRequestSparseArrayLength = mRequestSparseArray.size();

		for (int i = 0; i < mRequestSparseArrayLength; i++) {
			final Intent savedIntent = mRequestSparseArray.get(i);
			if (savedIntent.getIntExtra(
					SkeletonService.INTENT_EXTRA_WORKER_TYPE, -1) != SkeletonService.WORKER_TYPE_NEARBY_FRIENDS_LIST) {
				continue;
			}
			if (savedIntent
					.getIntExtra(
							SkeletonService.INTENT_EXTRA_NEARBY_FRIENDS_LIST_RETURN_FORMAT,
							-1) != returnFormat) {
				continue;
			}
			return mRequestSparseArray.keyAt(i);
		}
		final int requestID = sRandom.nextInt(MAX_RANDOM_REQUEST_ID);
		final Intent forwardIntent = new Intent(mContext, SkeletonService.class);
		forwardIntent.putExtra(SkeletonService.INTENT_EXTRA_WORKER_TYPE,
				SkeletonService.WORKER_TYPE_NEARBY_FRIENDS_LIST);
		forwardIntent.putExtra(SkeletonService.INTENT_EXTRA_RECEIVER,
				mEvalReceiver);
				if (userLocation == null) {
			forwardIntent.putExtra(SkeletonService.INTENT_EXTRA_USER_LAT,
					37.422006);
			forwardIntent.putExtra(SkeletonService.INTENT_EXTRA_USER_LON,
					-122.084095);

		} else {
			forwardIntent.putExtra(SkeletonService.INTENT_EXTRA_USER_LAT,
					userLocation.getLatitude());
			forwardIntent.putExtra(SkeletonService.INTENT_EXTRA_USER_LON,
					userLocation.getLongitude());
		}

		forwardIntent.putExtra(SkeletonService.INTENT_EXTRA_REQUEST_ID,
				requestID);
		forwardIntent.putExtra(
				SkeletonService.INTENT_EXTRA_NEARBY_FRIENDS_LIST_RETURN_FORMAT,
				returnFormat);
		mContext.startService(forwardIntent);

		mRequestSparseArray.append(requestID, forwardIntent);
		return requestID;
	}

	/**
	 * Here begin the special methods
	 */

	// TODO : This is where you will add your methods which will call the
	// service

}
