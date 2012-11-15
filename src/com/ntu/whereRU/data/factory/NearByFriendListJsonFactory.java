package com.ntu.whereRU.data.factory;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ntu.whereRU.app.config.JSONTag;
import com.ntu.whereRU.model.Person;

/**
 * This class handle the Json object from server
 * 
 * @author ZungLe
 * 
 */
public class NearByFriendListJsonFactory {

	public static ArrayList<Person> parseResult(final String wsResponse)
			throws JSONException {
		final ArrayList<Person> personList = new ArrayList<Person>();

		final JSONObject jsonRoot = new JSONObject(wsResponse);
		final Boolean respond_status = jsonRoot
				.getBoolean(JSONTag.RESPOND_STATUS);
		if (respond_status) {
			final JSONArray jsonPersonArray = jsonRoot
					.getJSONArray(JSONTag.DATA_RESPON_TAG);
			final int size = jsonPersonArray.length();
			for (int i = 0; i < size; i++) {
				final JSONObject jsonPerson = jsonPersonArray.getJSONObject(i);
				final Person person = new Person();

				person.foneNumber = jsonPerson
						.getString(JSONTag.PERSON_LIST_FONE_NUMBER);
				person.name = jsonPerson
						.getString(JSONTag.PERSON_LIST_ELEM_PERSON_NAME);
				person.email = jsonPerson
						.getString(JSONTag.PERSON_LIST_ELEM_PERSON_EMAIL);
				person.lat = Float.parseFloat(jsonPerson
						.getString(JSONTag.PERSON_LIST_ELEM_PERSON_LAT));
				person.lon = Float.parseFloat(jsonPerson
						.getString(JSONTag.PERSON_LIST_ELEM_PERSON_LON));
				person.distance = jsonPerson
						.getInt(JSONTag.PERSON_LIST_ELEM_PERSON_DISTANCE);
				person.lastCheckin = jsonPerson
						.getString(JSONTag.PERSON_LIST_ELEM_PERSON_LAST_CHECKIN);
				person.lastSeen = jsonPerson
						.getString(JSONTag.PERSON_LIST_ELEM_PERSON_LAST_SEEN);

				personList.add(person);
			}
		}

		return personList;
	}
}
