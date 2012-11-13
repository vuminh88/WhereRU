/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ntu.whereRU.util;

import java.lang.ref.WeakReference;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Slightly more abstract {@link AsyncQueryHandler} that helps keep a
 * {@link WeakReference} back to a listener. Will properly close any
 * {@link Cursor} if the listener ceases to exist.
 * <p>
 * This pattern can be used to perform background queries without leaking
 * {@link Context} objects.
 * 
 * @hide pending API council review
 */
public class NotifyingAsyncQueryHandler extends AsyncQueryHandler {
    private WeakReference<AsyncQueryListener> mListener;

    /**
     * Interface to listen for completed query operations.
     */
    public interface AsyncQueryListener {
        void onQueryComplete(int token, Object cookie, Cursor cursor);
    }

    public NotifyingAsyncQueryHandler(final ContentResolver resolver, final AsyncQueryListener listener) {
        super(resolver);
        setQueryListener(listener);
    }

    /**
     * Assign the given {@link AsyncQueryListener} to receive query events from
     * asynchronous calls. Will replace any existing listener.
     */
    public void setQueryListener(final AsyncQueryListener listener) {
        mListener = new WeakReference<AsyncQueryListener>(listener);
    }

    /**
     * Clear any {@link AsyncQueryListener} set through
     * {@link #setQueryListener(AsyncQueryListener)}
     */
    public void clearQueryListener() {
        mListener = null;
    }

    /**
     * Begin an asynchronous query with the given arguments. When finished,
     * {@link AsyncQueryListener#onQueryComplete(int, Object, Cursor)} is called
     * if a valid {@link AsyncQueryListener} is present.
     */
    public void startQuery(final Uri uri, final String[] projection) {
        startQuery(-1, null, uri, projection, null, null, null);
    }

    /**
     * Begin an asynchronous query with the given arguments. When finished,
     * {@link AsyncQueryListener#onQueryComplete(int, Object, Cursor)} is called
     * if a valid {@link AsyncQueryListener} is present.
     * 
     * @param token Unique identifier passed through to
     *            {@link AsyncQueryListener#onQueryComplete(int, Object, Cursor)}
     */
    public void startQuery(final int token, final Uri uri, final String[] projection) {
        startQuery(token, null, uri, projection, null, null, null);
    }

    /**
     * Begin an asynchronous query with the given arguments. When finished,
     * {@link AsyncQueryListener#onQueryComplete(int, Object, Cursor)} is called
     * if a valid {@link AsyncQueryListener} is present.
     */
    public void startQuery(final Uri uri, final String[] projection, final String sortOrder) {
        startQuery(-1, null, uri, projection, null, null, sortOrder);
    }

    /**
     * Begin an asynchronous query with the given arguments. When finished,
     * {@link AsyncQueryListener#onQueryComplete(int, Object, Cursor)} is called
     * if a valid {@link AsyncQueryListener} is present.
     */
    public void startQuery(final Uri uri, final String[] projection, final String selection,
            final String[] selectionArgs, final String orderBy) {
        startQuery(-1, null, uri, projection, selection, selectionArgs, orderBy);
    }

    /**
     * Begin an asynchronous update with the given arguments.
     */
    public void startUpdate(final Uri uri, final ContentValues values) {
        startUpdate(-1, null, uri, values, null, null);
    }

    public void startInsert(final Uri uri, final ContentValues values) {
        startInsert(-1, null, uri, values);
    }

    public void startDelete(final Uri uri) {
        startDelete(-1, null, uri, null, null);
    }

    /** {@inheritDoc} */
    @Override
    protected void onQueryComplete(final int token, final Object cookie, final Cursor cursor) {
        final AsyncQueryListener listener = mListener == null ? null : mListener.get();
        if (listener != null) {
            listener.onQueryComplete(token, cookie, cursor);
        } else if (cursor != null) {
            cursor.close();
        }
    }
}
