package com.dridian.android_rest_request;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;

public class WebServiceSerializationTranslator
{
    public WebServiceInterface toObject(Object context, Class<?> classObj, String data)
    {
        WebServiceInterface service = null;
        try
        {
            Constructor<?> constructor = getConstructor(classObj);
            service = getWebService(constructor, context);
            service.unserializeValues(data);
        }
        catch (Exception e) {}

        return service;
    }

    public String fromObject(WebServiceInterface service)
    {
        return service.serializeValues();
    }

    protected Constructor<?> getConstructor(Class<?> classObj) throws SecurityException, NoSuchMethodException
    {
        return classObj.getConstructor(Context.class);
    }

    protected WebServiceInterface getWebService(Constructor<?> constructor, Object context)
        throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
    {
        return (WebServiceInterface)constructor.newInstance((Context)context);
    }
}