package com.dridian.android_rest_request;

public interface TranslatorInterface
{
    public Object toObject(Class<?> classObj, String data);

    public String fromObject(Object object);
}