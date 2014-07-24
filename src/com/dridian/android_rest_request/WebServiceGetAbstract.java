package com.dridian.android_rest_request;

import android.content.Context;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

abstract public class WebServiceGetAbstract extends WebServiceAbstract
{
    public WebServiceGetAbstract(Context context)
    {
        super(context);
    }

    protected HttpUriRequest getHttpUriRequest()
    {
        return new HttpGet(getUrl());
    }
}
