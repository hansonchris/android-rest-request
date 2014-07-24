package com.dridian.android_rest_request;

import android.os.AsyncTask;

public class RequestAsyncTask
{
    public void execute(
        final AsyncTaskRequestHandlerInterface requestHandler,
        final AsyncTaskResponseHandlerInterface responseHandler
    ) {
        new AsyncTask<Void, Void, RestResponseInterface>() {
            protected RestResponseInterface doInBackground(Void... voids)
            {
                return requestHandler.handleRequest();
            }

            protected void onPostExecute(RestResponseInterface response)
            {
                responseHandler.handleResponse(response);
            }
        }.execute();
    }
}
