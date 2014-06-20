package com.dridian.android_rest_request;

import android.os.AsyncTask;

public class RequestAsyncTask
{
    protected AsyncTaskRequestHandlerInterface requestHandler;
    protected AsyncTaskResponseHandlerInterface responseHandler;

    public RequestAsyncTask(AsyncTaskResponseHandlerInterface responseHandler)
    {
        this.responseHandler = responseHandler;
    }

    public void execute(final AsyncTaskRequestHandlerInterface requestHandler)
    {
        new AsyncTask<Void, Void, RestResponse>()
        {
            protected RestResponse doInBackground(Void... voids)
            {
                return requestHandler.handleRequest();
            }

            protected void onPostExecute(RestResponse response)
            {
                responseHandler.handleResponse(response);
            }
        }.execute();
    }
}
