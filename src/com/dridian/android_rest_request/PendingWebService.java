package com.dridian.android_rest_request;

import java.util.Calendar;

public class PendingWebService
{
    protected String id;
    protected WebServiceInterface service;
    protected Calendar lastRan;
    protected int frequency;
    protected int frequencyUnit;

    public PendingWebService(WebServiceInterface service, int frequency, int frequencyUnit)
    {
        this.service = service;
        this.frequency = frequency;
        this.frequencyUnit = frequencyUnit;
        lastRan = Calendar.getInstance();
    }

    public void updateRunTime()
    {
        lastRan = Calendar.getInstance();
    }

    public boolean timeToRun()
    {
        Calendar now = Calendar.getInstance();
        Calendar timeToRun = Calendar.getInstance();
        timeToRun.setTimeInMillis(lastRan.getTimeInMillis());
        timeToRun.add(frequencyUnit, frequency);

        return (timeToRun.before(now));
    }

    public WebServiceInterface getWebService()
    {
        return service;
    }

    public int getFrequency()
    {
        return frequency;
    }

    public int getFrequencyUnit()
    {
        return frequencyUnit;
    }
}
