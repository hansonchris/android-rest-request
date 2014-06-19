package com.dridian.android_rest_request;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;

abstract public class WebServiceGetAbstract extends WebServiceAbstract
{
	public WebServiceGetAbstract(Context context)
	{
		super(context);
	}
	
	protected HttpUriRequest _getHttpUriRequest(String uri)
	{
		return new HttpGet(uri);
	}
}