package com.ntu.whereRU.skeleton.data.provider;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.BaseColumns;

import com.ntu.whereRU.model.Person;
import com.ntu.whereRU.provider.util.DatabaseUtil;

/**
 * {@link SkeletonContent} is the superclass of the various classes of content
 * stored by {@link SkeletonProvider}.
 * <p>
 * <b>This class is a skeleton of the normal class. Replace the TODOs by your
 * code</b>
 * </p>
 */
public abstract class SkeletonContent {
	// TODO : Set the SkeletonProvider authority
	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ SkeletonProvider.AUTHORITY);

	// TODO : Create an interface with the columns for your table
	public interface SkeletonColumns {
		public static final String FONE_NUMBER = "phoneNumber";
		public static final String NAME = "name";
		public static final String DISTANCE = "distance";
		public static final String LAT = "lat";
		public static final String LON = "lon";
		public static final String LAST_SEEN = "lastSeen";
		public static final String EMAIL = "email";
		public static final String LAST_CHECK_IN = "lastCheckIn";
	}

	public static final class Skeleton extends SkeletonContent implements
			SkeletonColumns, BaseColumns {
		// TODO : Set the table name
		public static final String TABLE_NAME = "users";
		public static final Uri CONTENT_URI = Uri
				.parse(SkeletonContent.CONTENT_URI + "/" + TABLE_NAME);
		// TODO : Set the elem and dir types
		public static final String TYPE_ELEM_TYPE = "vnd.android.cursor.item/com.ntu.whereRU.skeleton.data.provider.Skeleton";
		public static final String TYPE_DIR_TYPE = "vnd.android.cursor.dir/com.ntu.whereRU.skeleton.data.provider.Skeleton";

		// TODO : Add constants for the selection/sort order you will use in
		// your project
		// public static final String COLUMN_NAME_ONE_SELECTION =
		// COLUMN_NAME_ONE + " = ?";
		// public static final String COLUMN_NAME_TWO_ORDER_BY = COLUMN_NAME_TWO
		// + " ASC";

		public static final String NAME_ORDER_BY = NAME + " ASC";

		// TODO : This is the default projection which returns all the columns
		public static final int CONTENT_ID_COLUMN = 0;
		public static final int CONTENT_FONE_NUMBER_COLUMN = 1;
		public static final int CONTENT_NAME_COLUMN = 2;
		public static final int CONTENT_EMAIL_COLUMN = 3;
		public static final int CONTENT_DISTANCE_COLUMN = 4;
		public static final int CONTENT_LAT_COLUMN = 5;
		public static final int CONTENT_LON_COLUMN = 6;
		public static final int CONTENT_LAST_CHECK_IN_COLUMN = 7;
		public static final int CONTENT_LAST_SEEN_COLUMN = 8;
		public static final String[] CONTENT_PROJECTION = new String[] { _ID,
				FONE_NUMBER, NAME, EMAIL, DISTANCE, LAT, LON, LAST_CHECK_IN,
				LAST_SEEN };

		// TODO : Add the other projections (if any) you will use using the same
		// model as
		// the content projection

		// TODO : The following 2 methods are used for creation and upgrade of a
		// table.
		static void createTable(final SQLiteDatabase db) {
			final String s = " (" + _ID
					+ " integer primary key autoincrement, " + FONE_NUMBER
					+ " text, " + NAME + " text, " + EMAIL + " text, "
					+ DISTANCE + " integer, " + LAT + " real, " + LON
					+ " real, " + LAST_CHECK_IN + " text, " + LAST_SEEN
					+ " text " + ");";

			db.execSQL("create table " + TABLE_NAME + s);

			// TODO : Add the table's indexes (if any) using the
			// getCreateIndexString() method
			db.execSQL(DatabaseUtil.getCreateIndexString(TABLE_NAME,
					FONE_NUMBER));

			// TODO : Add the table's triggers (if any)
		}

		static void upgradeTable(final SQLiteDatabase db, final int oldVersion,
				final int newVersion) {
			try {
				db.execSQL("drop table " + TABLE_NAME);
			} catch (final SQLException e) {
			}
			createTable(db);
		}

		// TODO : The 2 following methods allow to save in bulk (it should be
		// used if you have more than one insert to do)
		public static String getBulkInsertString() {
			final StringBuffer sqlRequest = new StringBuffer("INSERT INTO ");
			sqlRequest.append(TABLE_NAME);
			sqlRequest.append(" ( ");
			sqlRequest.append(FONE_NUMBER);
			sqlRequest.append(", ");
			sqlRequest.append(NAME);
			sqlRequest.append(", ");
			sqlRequest.append(EMAIL);
			sqlRequest.append(", ");
			sqlRequest.append(DISTANCE);
			sqlRequest.append(", ");
			sqlRequest.append(LAT);
			sqlRequest.append(", ");
			sqlRequest.append(LON);
			sqlRequest.append(", ");
			sqlRequest.append(LAST_CHECK_IN);
			sqlRequest.append(", ");
			sqlRequest.append(LAST_SEEN);
			sqlRequest.append(" ) ");
			sqlRequest.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			return sqlRequest.toString();
		}

		public static void bindValuesInBulkInsert(final SQLiteStatement stmt,
				final ContentValues values) {
			try {
				int i = 1;
				String value = values.getAsString(FONE_NUMBER);
				stmt.bindString(i++, value != null ? value : "");
				value = values.getAsString(NAME);
				stmt.bindString(i++, value != null ? value : "");
				value = values.getAsString(EMAIL);
				stmt.bindString(i++, value != null ? value : "");
				stmt.bindLong(i++, values.getAsInteger(DISTANCE));
				stmt.bindDouble(i++, values.getAsDouble(LAT));
				stmt.bindDouble(i++, values.getAsDouble(LON));
				value = values.getAsString(LAST_CHECK_IN);
				stmt.bindString(i++, value != null ? value : "");
				value = values.getAsString(LAST_SEEN);
				stmt.bindString(i++, value != null ? value : "");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public static ContentValues getContentValues(Person person) {
			ContentValues result = new ContentValues();
			result.put(FONE_NUMBER, person.foneNumber);
			result.put(NAME, person.name);
			result.put(EMAIL, person.email);
			result.put(DISTANCE, person.distance);
			result.put(LAT, person.lat);
			result.put(LON, person.lon);
			result.put(LAST_CHECK_IN, person.lastCheckin);
			result.put(LAST_SEEN, person.lastSeen);
			return result;
		}
	}
}
