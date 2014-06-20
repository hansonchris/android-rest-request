package com.dridian.android_rest_request;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.util.List;

abstract public class WebServiceAbstract implements WebServiceInterface
{
    protected Context context;

    abstract protected String getDomain();

    abstract protected HttpUriRequest getHttpUriRequest();

    abstract protected String getUri();

    abstract protected boolean canMakeRequest();

    abstract protected String getAuthKeyName();

    abstract protected String getAuthKey();

    public WebServiceAbstract(Context context)
    {
        this.context = context;
    }

    public void makeRequest(RequestAsyncTask requestAsyncTask)
    {
        requestAsyncTask.execute(new AsyncTaskRequestHandlerInterface() {
            public RestResponse handleRequest()
            {
                return doRequest();
            }
        });
    }

    protected RestResponse doRequest()
    {
        RestResponse restResponse = null;
        if (canMakeRequest())
        {
            HttpUriRequest httpRequest = getHttpUriRequest();
            String authKeyName = getAuthKeyName();
            String authKey = getAuthKey();
            if (authKeyName != null && authKey != null)
            {
                httpRequest.setHeader(authKeyName, authKey);
            }
            HttpClient client = _getHttpClient();
            try
            {
                HttpResponse response = client.execute(httpRequest);
                restResponse = _getRestResponse(response);
            }
            catch (ClientProtocolException e) {Log.i("Inv", "ClientProtocolException" + e.getMessage());}
            catch (IOException e) {
                restResponse = null;
                Log.i("Inv", "IOException" + e.getMessage());
            }
            catch (Exception e) {Log.i("Inv", "Exception" + e.getMessage());}

            return restResponse;
        }

        return restResponse;
    }

    public boolean shouldAddPendingRequest(List<PendingWebService> pendingServices)
    {
        return true;
    }

    protected RestResponse _getRestResponse(HttpResponse httpResponse)
    {
        return new RestResponse(httpResponse);
    }

    protected HttpClient _getHttpClient()
    {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpParams params = _getHttpParams();
        int timeoutConnection = _getTimeoutConnection();
        HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
        int timeoutSocket = _getTimeoutSocket();
        HttpConnectionParams.setSoTimeout(params, timeoutSocket);
        httpClient.setParams(params);

        return httpClient;
    }

    protected HttpParams _getHttpParams()
    {
        return new BasicHttpParams();
    }

    protected int _getTimeoutConnection()
    {
        return 3000;
    }

    protected int _getTimeoutSocket()
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
}