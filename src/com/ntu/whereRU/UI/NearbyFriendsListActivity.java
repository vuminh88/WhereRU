package com.ntu.whereRU.UI;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.ntu.whereRU.R;
import com.ntu.whereRU.app.config.DialogConfig;
import com.ntu.whereRU.app.config.WSConfig;
import com.ntu.whereRU.locationprovider.ILocationWorker;
import com.ntu.whereRU.locationprovider.LocationGetter;
import com.ntu.whereRU.skeleton.data.provider.SkeletonContent.Skeleton;
import com.ntu.whereRU.skeleton.data.requestmanager.SkeletonRequestManager;
import com.ntu.whereRU.skeleton.data.requestmanager.SkeletonRequestManager.OnRequestFinishedListener;
import com.ntu.whereRU.skeleton.data.service.SkeletonService;
import com.ntu.whereRU.util.NotifyingAsyncQueryHandler;
import com.ntu.whereRU.util.NotifyingAsyncQueryHandler.AsyncQueryListener;

public class NearbyFriendsListActivity extends ListActivity implements
		OnRequestFinishedListener, AsyncQueryListener {

	private SkeletonRequestManager mRequestManager;
	private NotifyingAsyncQueryHandler mQueryHandler;
	private LayoutInflater mInflater;
	private int mRequestId = -1;
	private ILocationWorker locationWorker;

	private String mErrorDialogTitle;
	private String mErrorDialogMessage;

	private static final String SAVED_STATE_REQUEST_ID = "savedStateRequestId";
	private static final String SAVED_STATE_ERROR_TITLE = "savedStateErrorTitle";
	private static final String SAVED_STATE_ERROR_MESSAGE = "savedStateErrorMessage";

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		if (savedInstanceState != null) {
			mRequestId = savedInstanceState.getInt(SAVED_STATE_REQUEST_ID, -1);
			mErrorDialogTitle = savedInstanceState
					.getString(SAVED_STATE_ERROR_TITLE);
			mErrorDialogMessage = savedInstanceState
					.getString(SAVED_STATE_ERROR_MESSAGE);
		}

		mRequestManager = SkeletonRequestManager.from(this);
		mQueryHandler = new NotifyingAsyncQueryHandler(getContentResolver(),
				this);
		mInflater = getLayoutInflater();
		mQueryHandler.startQuery(Skeleton.CONTENT_URI,
				Skeleton.CONTENT_PROJECTION, Skeleton.NAME_ORDER_BY);
		callPersonListWS();
		setContentView(R.layout.friend_list);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mRequestId != -1) {
			if (mRequestManager.isRequestInProgress(mRequestId)) {
				mRequestManager.addOnRequestFinishedListener(this);
				setProgressBarIndeterminateVisibility(true);
			} else {
				mRequestId = -1;

				// Get the number of persons in the database
				int number = ((UserListAdapter) getListAdapter()).getCursor()
						.getCount();

				if (number < 1) {
					// In this case, we don't have a way to know if the request
					// was correctly executed with 0 result or if an error
					// occurred. Here I choose to display an error but it's up
					// to you
					showDialog(DialogConfig.DIALOG_CONNECTION_ERROR);
				}
				// Nothing to do if it works as the cursor is automatically
				// updated
			}
		}
	}

	protected void onPause() {
		super.onPause();
		if (mRequestId != -1) {
			mRequestManager.removeOnRequestFinishedListener(this);
		}
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(SAVED_STATE_REQUEST_ID, mRequestId);
		outState.putString(SAVED_STATE_ERROR_TITLE, mErrorDialogTitle);
		outState.putString(SAVED_STATE_ERROR_MESSAGE, mErrorDialogMessage);
	}

	@Override
	protected void onPrepareDialog(final int id, final Dialog dialog) {
		switch (id) {
		case DialogConfig.DIALOG_ERROR:
			dialog.setTitle(mErrorDialogTitle);
			((AlertDialog) dialog).setMessage(mErrorDialogMessage);
			break;
		default:
			super.onPrepareDialog(id, dialog);
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		Builder b;
		switch (id) {
		case DialogConfig.DIALOG_ERROR:
			b = new Builder(this);
			b.setTitle(mErrorDialogTitle);
			b.setMessage(mErrorDialogMessage);
			b.setCancelable(true);
			b.setNeutralButton(android.R.string.ok, null);
			return b.create();
		case DialogConfig.DIALOG_CONNECTION_ERROR:
			b = new Builder(this);
			b.setCancelable(true);
			b.setNeutralButton(getString(android.R.string.ok), null);
			b.setPositiveButton(getString(R.string.dialog_button_retry),
					new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog,
								final int which) {
							callPersonListWS();
						}
					});
			b.setTitle(R.string.dialog_error_connection_error_title);
			b.setMessage(R.string.dialog_error_connection_error_message);
			return b.create();
		default:
			return super.onCreateDialog(id);
		}
	}

	private void callPersonListWS() {
		locationWorker = new LocationGetter(this);
		Location userLocation = locationWorker.getUserLocation(2000, 2000);
		setProgressBarIndeterminateVisibility(true);
		mRequestManager.addOnRequestFinishedListener(this);
		mRequestId = mRequestManager.getNearbyFriendsActivity(
				WSConfig.JSON_RETURN_FORMAT, userLocation);
	}

	@Override
	public void onRequestFinished(final int requestId, final int resultCode,
			final Bundle payload) {
		if (requestId == mRequestId) {
			setProgressBarIndeterminateVisibility(false);
			mRequestId = -1;
			mRequestManager.removeOnRequestFinishedListener(this);
			if (resultCode == SkeletonService.ERROR_CODE) {
				if (payload != null) {
					final int errorType = payload.getInt(
							SkeletonRequestManager.RECEIVER_EXTRA_ERROR_TYPE,
							-1);
					if (errorType == SkeletonRequestManager.RECEIVER_EXTRA_VALUE_ERROR_TYPE_DATA) {
						mErrorDialogTitle = getString(R.string.dialog_error_data_error_title);
						mErrorDialogMessage = getString(R.string.dialog_error_data_error_message);
						showDialog(DialogConfig.DIALOG_ERROR);
					} else {
						showDialog(DialogConfig.DIALOG_CONNECTION_ERROR);
					}
				} else {
					showDialog(DialogConfig.DIALOG_CONNECTION_ERROR);
				}
			}
			// Nothing to do if it works as the cursor is automatically updated
		}
	}

	@Override
	public void onQueryComplete(final int token, final Object cookie,
			final Cursor cursor) {
		UserListAdapter adapter = (UserListAdapter) getListAdapter();
		if (adapter == null) {
			adapter = new UserListAdapter(this, cursor);
			setListAdapter(adapter);
		} else {
			adapter.changeCursor(cursor);
		}
	}

	class ViewHolder {
		private TextView mTextViewFullName;
		private CharArrayBuffer mCharArrayBufferFullName;

		private TextView mTextViewLastCheckin;
		// private CharArrayBuffer mCharArrayBufferLastCheckin;

		private TextView mTextViewLastSeen;
		private CharArrayBuffer mCharArrayBufferLastSeen;

		private TextView mTextViewFoneNumber;
		private CharArrayBuffer mCharArrayFoneNumber;

		private TextView mTextViewDistance;
		private CharArrayBuffer mCharArrayBufferDistance;

		public ViewHolder(final View view) {
			mTextViewFullName = (TextView) view.findViewById(R.id.tv_name);
			mTextViewFoneNumber = (TextView) view
					.findViewById(R.id.tv_fone_number);
			mTextViewLastCheckin = (TextView) view
					.findViewById(R.id.tv_last_checkin_add);
			mTextViewLastSeen = (TextView) view.findViewById(R.id.tv_last_seen);
			mTextViewDistance = (TextView) view.findViewById(R.id.tv_distance);

			mCharArrayBufferFullName = new CharArrayBuffer(20);
			mCharArrayFoneNumber = new CharArrayBuffer(20);
//			mCharArrayBufferLastCheckin = new CharArrayBuffer(20);
			mCharArrayBufferLastSeen = new CharArrayBuffer(20);
			mCharArrayBufferDistance = new CharArrayBuffer(20);
		}

		public void populateView(final Cursor c) {

			c.copyStringToBuffer(Skeleton.CONTENT_NAME_COLUMN,
					mCharArrayBufferFullName);
			mTextViewFullName.setText(mCharArrayBufferFullName.data, 0,
					mCharArrayBufferFullName.sizeCopied);

			c.copyStringToBuffer(Skeleton.CONTENT_FONE_NUMBER_COLUMN,
					mCharArrayFoneNumber);
			mTextViewFoneNumber.setText(mCharArrayFoneNumber.data, 0,
					mCharArrayFoneNumber.sizeCopied);

			// c.copyStringToBuffer(Skeleton.CONTENT_LAST_CHECK_IN_COLUMN,
			// mCharArrayBufferLastCheckin);
			StringBuilder lastCheckin = new StringBuilder("at ");
			lastCheckin.append(c
					.getString(Skeleton.CONTENT_LAST_CHECK_IN_COLUMN));
			mTextViewLastCheckin.setText(lastCheckin.toString());

			c.copyStringToBuffer(Skeleton.CONTENT_LAST_SEEN_COLUMN,
					mCharArrayBufferLastSeen);
			mTextViewLastSeen.setText(mCharArrayBufferLastSeen.data, 0,
					mCharArrayBufferLastSeen.sizeCopied);

			c.copyStringToBuffer(Skeleton.CONTENT_DISTANCE_COLUMN,
					mCharArrayBufferDistance);
			mTextViewDistance.setText(mCharArrayBufferDistance.data, 0,
					mCharArrayBufferDistance.sizeCopied);
			mTextViewDistance.append("m from here");
		}
	}

	class UserListAdapter extends CursorAdapter {

		public UserListAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			((ViewHolder) view.getTag()).populateView(cursor);

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = mInflater.inflate(R.layout.friend_list_item, null);
			view.setTag(new ViewHolder(view));
			return view;
		}

	}

}
