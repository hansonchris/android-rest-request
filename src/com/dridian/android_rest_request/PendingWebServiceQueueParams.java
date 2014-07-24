package com.dridian.android_rest_request;

import java.util.Calendar;

public class PendingWebServiceQueueParams
{
    protected int frequencyInSeconds;

    public void setFrequency(int frequency, int frequencyUnits) throws Exception
    {
        int multiplier = 1;
        switch (frequencyUnits) {
            case Calendar.SECOND:
                multiplier = 1;
                break;
            case Calendar.MINUTE:
                multiplier = 60;
                break;
            case Calendar.HOUR:
                multiplier = 60 * 60;
                break;
            default:
                throw new Exception("Unrecognized frequencyUnits value.");
        }
        frequencyInSeconds = frequency * multiplier;
    }

    public int getFrequencyInSeconds()
    {
        return frequencyInSeconds;
    }
}
