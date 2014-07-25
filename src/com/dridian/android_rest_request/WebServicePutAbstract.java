package com.dridian.android_rest_request;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;

import android.content.Context;

abstract public class WebServicePutAbstract extends WebServiceAbstract
{
    abstract protected String getPutBody();

    public WebServicePutAbstract(Context context)
    {
        super(context);
    }

    protected HttpUriRequest getHttpUriRequest()
    {
        HttpPut httpRequest = new HttpPut(getUrl());
        httpRequest.setEntity(new ByteArrayEntity(getPutBody().getBytes()));

        return httpRequest;
    }
}
