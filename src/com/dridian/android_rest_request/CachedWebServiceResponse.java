package com.dridian.android_rest_request;

import java.util.Calendar;

public class CachedWebServiceResponse implements RestResponseInterface
{
    protected String responseBody;
    protected long expirationTime;
    
    public CachedWebServiceResponse(String responseBody, long expirationTime)
    {
        this.responseBody = responseBody;
        this.expirationTime = expirationTime;
    }
    
    public String getResponseBody()
    {
        return responseBody;
    }
    
    public boolean isExpired()
    {
        long now = Calendar.getInstance().getTimeInMillis();

        return (now > expirationTime);
    }

    public int getStatusCode()
    {
        //200 is hard-coded because I wouldn't cache a non-200 response for now
        //but if I ever have other success codes, such as 201, then this may
        //not be entirely accurate
        return 200;
    }
}
