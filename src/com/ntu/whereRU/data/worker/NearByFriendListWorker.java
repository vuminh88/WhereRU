package com.ntu.whereRU.data.worker;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.xml.sax.SAXException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.ntu.whereRU.app.config.WSConfig;
import com.ntu.whereRU.data.factory.NearByFriendListJsonFactory;
import com.ntu.whereRU.data.factory.NearByFriendListXmlFactory;
import com.ntu.whereRU.exception.RestClientException;
import com.ntu.whereRU.model.Person;
import com.ntu.whereRU.network.NetworkConnection;
import com.ntu.whereRU.network.NetworkConnection.NetworkConnectionResult;
import com.ntu.whereRU.skeleton.data.provider.SkeletonContent.Skeleton;
import com.ntu.whereRU.skeleton.data.service.SkeletonService;

/**
 * This class provider a worker for invoking data. Refer the pattern for more
 * information about worker
 * 
 * @author ZungLe
 * 
 */
public class NearByFriendListWorker {

	public static final int RETURN_FORMAT_XML = 0;
	public static final int RETURN_FORMAT_JSON = 1;

	public static void start(final Context context, final int returnFormat,
			String lat, String lon) throws IllegalStateException, IOException,
			URISyntaxException, RestClientException,
			ParserConfigurationException, SAXException, JSONException {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(SkeletonService.INTENT_EXTRA_USER_LAT, lat);
		parameters.put(SkeletonService.INTENT_EXTRA_USER_LON, lon);
		NetworkConnectionResult wsResult = NetworkConnection
				.retrieveResponseFromService(
						returnFormat == RETURN_FORMAT_XML ? WSConfig.WS_PERSON_LIST_URL_XML
								: WSConfig.WS_PERSON_LISTs_URL_JSON,
						NetworkConnection.METHOD_GET, parameters);

		ArrayList<Person> personList = null;
		if (returnFormat == RETURN_FORMAT_XML) {
			personList = NearByFriendListXmlFactory
					.parseResult(wsResult.wsResponse);
		} else {
			personList = NearByFriendListJsonFactory
					.parseResult(wsResult.wsResponse);
		}

		// Clear the table
		context.getContentResolver().delete(Skeleton.CONTENT_URI, null, null);

		// Adds the persons in the database
		final int personListSize = personList.size();
		if (personList != null && personListSize > 0) {
			ContentValues[] valuesArray = new ContentValues[personListSize];
			for (int i = 0; i < personListSize; i++) {
				valuesArray[i] = Skeleton.getContentValues(personList.get(i));
			}
			context.getContentResolver().bulkInsert(Skeleton.CONTENT_URI,
					valuesArray);
		}
	}
}
