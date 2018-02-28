package eu.captaincode.popularmovies.handler;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.net.Uri;

/**
 * Handles database CRUD operations on a background thread and notifies listeners on completion.
 */

public class FavoritesAsyncQueryHandler extends AsyncQueryHandler {
    private AsyncQueryListener mListener;

    public FavoritesAsyncQueryHandler(ContentResolver cr, AsyncQueryListener listener) {
        super(cr);
        this.mListener = listener;
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        mListener.onInsertComplete();
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        mListener.onDeleteComplete();
    }

    public interface AsyncQueryListener {
        void onInsertComplete();
        void onDeleteComplete();
    }
}