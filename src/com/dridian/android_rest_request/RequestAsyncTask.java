package com.dridian.android_rest_request;

import android.os.AsyncTask;

public class RequestAsyncTask
{
	protected AsyncTaskRequestHandlerInterface requestHandler;
	protected AsyncTaskResponseHandlerInterface responseHandler;
	
	public RequestAsyncTask(AsyncTaskRequestHandlerInterface requestHandler,
		AsyncTaskResponseHandlerInterface responseHandler
	) {
		this.requestHandler = requestHandler;
		this.responseHandler = responseHandler;
	}
	
	public void execute()
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
