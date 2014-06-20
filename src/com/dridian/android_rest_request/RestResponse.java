package com.dridian.android_rest_request;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

public class RestResponse implements RestResponseInterface
{
    protected String responseBody;
    protected int statusCode;

    public RestResponse(HttpResponse httpResponse)
    {
        statusCode = getStatusCodeFromResponse(httpResponse);
        responseBody = getResponseBodyFromResponse(httpResponse);
    }

    protected int getStatusCodeFromResponse(HttpResponse httpResponse)
    {
        return httpResponse.getStatusLine().getStatusCode();
    }

    protected String getResponseBodyFromResponse(HttpResponse httpResponse)
    {
        StringBuilder stringBuilder = getStringBuilder();
        HttpEntity entity = httpResponse.getEntity();
        InputStream stream;
        try
        {
            stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1)
            {
                stringBuilder.append((char) b);
            }
        }
        catch (IllegalStateException e) {}
        catch (IOException e) {}
        responseBody = stringBuilder.toString();

        return responseBody;
    }

    public int getStatusCode()
    {
        return statusCode;
    }

    public String getResponseBody()
    {
        return responseBody;
    }

    protected StringBuilder getStringBuilder()
    {
        return new StringBuilder();
    }
}