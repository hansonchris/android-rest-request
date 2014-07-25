package com.dridian.android_rest_request;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;

abstract public class WebServiceDeleteAbstract extends WebServiceAbstract
{
    public WebServiceDeleteAbstract(Context context)
    {
        super(context);
    }

    protected HttpUriRequest getHttpUriRequest()
    {
        return new HttpDelete(getUrl());
    }
}
