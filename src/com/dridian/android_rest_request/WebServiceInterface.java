package com.dridian.android_rest_request;

import java.util.List;

import android.app.PendingIntent;

public interface WebServiceInterface
{
    public void makeRequest(RequestAsyncTask requestAsyncTask);

    public String serializeValues();

    public void unserializeValues(String values);

    public PendingIntent getPendingIntentSuccess(RestResponse response);

    public PendingIntent getPendingIntentFailure();

    public boolean shouldAddPendingRequest(List<PendingWebService> pendingServices);

    public String getRequestUrl();
}