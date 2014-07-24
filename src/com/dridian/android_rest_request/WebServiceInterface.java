package com.dridian.android_rest_request;

import java.util.List;

import android.app.PendingIntent;

public interface WebServiceInterface
{
    public void makeRequest(AsyncTaskResponseHandlerInterface responseHandler);

    public String serializeValues();

    public void unserializeValues(String values);

    public PendingIntent getPendingIntentSuccess(RestResponseInterface response);

    public PendingIntent getPendingIntentFailure();

    public boolean shouldAddPendingRequest(List<PendingWebService> pendingServices);

    public String getRequestUrl();

    public String getId();
}
