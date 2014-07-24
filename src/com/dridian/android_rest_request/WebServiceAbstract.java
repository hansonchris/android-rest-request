package com.dridian.android_rest_request;

import android.content.Context;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;

abstract public class WebServiceAbstract implements WebServiceInterface
{
    protected Context context;

    abstract protected String getDomain();

    abstract protected HttpUriRequest getHttpUriRequest();

    abstract protected String getUri();

    abstract protected boolean canMakeRequest();

    public WebServiceAbstract(Context context)
    {
        this.context = context;
    }

    public void makeRequest(AsyncTaskResponseHandlerInterface responseHandler)
    {
        RequestAsyncTask requestAsyncTask = new RequestAsyncTask();
        requestAsyncTask.execute(new AsyncTaskRequestHandlerInterface() {
            public RestResponseInterface handleRequest()
            {
                return doRequest();
            }
        }, responseHandler);
    }

    public void makeRequest(
        final AsyncTaskResponseHandlerInterface responseHandler,
        final PendingWebServiceQueueParams params
    ) {
        AsyncTaskResponseHandlerInterface responseHandlerWrapper = new AsyncTaskResponseHandlerInterface() {
            public void handleResponse(RestResponseInterface response)
            {
                if (response == null) {
                    registerPendingWebService(params);
                }
                responseHandler.handleResponse(response);
            }
        };
        makeRequest(responseHandlerWrapper);
    }

    protected void registerPendingWebService(PendingWebServiceQueueParams params)
    {
        PendingWebServiceQueueManager queueManager =
            PendingWebServiceQueueManager.getInstance(context);
        PendingWebService pendingService =
            new PendingWebService(this, params.getFrequencyInSeconds(), Calendar.SECOND);
        queueManager.addPendingWebService(pendingService);
    }

    protected RestResponseInterface doRequest()
    {
        RestResponseInterface restResponse = null;
        if (canMakeRequest())
        {
            HttpUriRequest httpRequest = getHttpUriRequest();
            String authKeyName = getAuthKeyName();
            String authKey = getAuthKey();
            if (authKeyName != null && authKey != null)
            {
                httpRequest.setHeader(authKeyName, authKey);
            }
            HttpClient client = getHttpClient();
            try
            {
                HttpResponse response = client.execute(httpRequest);
                restResponse = getRestResponse(response);
            }
            catch (Exception e) {
                restResponse = null;
            }

            return restResponse;
        }

        return restResponse;
    }

    public boolean shouldAddPendingRequest(List<PendingWebService> pendingServices)
    {
        return true;
    }

    protected RestResponseInterface getRestResponse(HttpResponse httpResponse)
    {
        return new RestResponse(httpResponse);
    }

    protected HttpClient getHttpClient()
    {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpParams params = getHttpParams();
        int timeoutConnection = getTimeoutConnection();
        HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
        int timeoutSocket = getTimeoutSocket();
        HttpConnectionParams.setSoTimeout(params, timeoutSocket);
        httpClient.setParams(params);

        return httpClient;
    }

    protected HttpParams getHttpParams()
    {
        return new BasicHttpParams();
    }

    protected int getTimeoutConnection()
    {
        return 3000;
    }

    protected int getTimeoutSocket()
    {
        return 5000;
    }

    protected String getUrl()
    {
        String url = getScheme() + "://" + getDomain() + getUri();
        return url;
    }

    protected String getScheme()
    {
        return "http";
    }

    public String getRequestUrl()
    {
        return getUri();
    }

    /**
     * Overridden by subclasses that need to queue failed requests
     */
    public String serializeValues()
    {
        return null;
    }

    /**
     * Overridden by subclasses that need to queue failed requests
     */
    public void unserializeValues(String values)
    {

    }

    /**
     * Should be overridden in most cases if a service will be queued upon failure.
     */
    public String getId()
    {
        return md5(getClass().getCanonicalName());
    }

    protected String md5(String value)
    {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            byte[] array = digest.digest(value.getBytes());
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                buffer.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }

            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {}

        return null;
    }

    /**
     * Override to send authorization header
     */
    protected String getAuthKeyName()
    {
        return "";
    }

    /**
     * Override to send authorization header
     */
    protected String getAuthKey()
    {
        return "";
    }
}
