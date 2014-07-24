package com.dridian.android_rest_request;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;

import android.content.Context;

abstract public class WebServicePostAbstract extends WebServiceAbstract
{
    abstract protected String getPostBody();

    public WebServicePostAbstract(Context context)
    {
        super(context);
    }

    protected HttpUriRequest getHttpUriRequest(String uri)
    {
        HttpPost httpRequest = new HttpPost(uri);
        httpRequest.setEntity(new ByteArrayEntity(getPostBody().getBytes()));

        return httpRequest;
    }
}
