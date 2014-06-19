package com.dridian.android_rest_request;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;

abstract public class WebServiceAbstract implements WebServiceInterface
{
	protected Context context;
	
	abstract protected String _getDomain();
	
	abstract protected HttpUriRequest _getHttpUriRequest(String uri);
	
	abstract protected String _getUri();
	
	abstract protected boolean _canMakeRequest();
	
	abstract protected String _getAuthKeyName();
	
	abstract protected String _getAuthKey();
	
	public WebServiceAbstract(Context context)
	{
		this.context = context;
	}
	
	public RestResponse doRequest()
	{
		RestResponse restResponse = null;
		if (_canMakeRequest())
		{
			HttpUriRequest httpRequest = _getHttpUriRequest(_getUri());
			String authKeyName = _getAuthKeyName();
			String authKey = _getAuthKey();
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
			catch (ClientProtocolException e) {}
			catch (IOException e) {}
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
	
	public String getRequestUrl()
	{
		return _getUri();
	}
}