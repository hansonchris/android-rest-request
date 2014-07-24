package com.dridian.android_rest_request;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class PendingWebServiceQueueBackgroundService extends Service
{
    volatile static private PendingWebServiceQueueBackgroundService instance;
    protected Timer timer;
    protected TimerTask timerTask;
    volatile protected Map<PendingWebService, PendingWebServiceSubscriberInterface> subscribers;
    volatile protected boolean isRunning;
    static protected int DELAY = 15;
    static protected int INTERVAL = 15;

    synchronized public void onCreate()
    {
        super.onCreate();
        instance = this;
    }

    public void stopService()
    {
        super.stopSelf();
        isRunning = false;
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        isRunning = true;
        startTimerTask();

        return START_STICKY;
    }

    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public void onDestroy()
    {
        super.onDestroy();
        timer.cancel();
    }

    public void onLowMemory()
    {
        super.onLowMemory();
    }

    public void startTimerTask()
    {
        timer = getTimer();
        timerTask = new TimerTask() {
            public void run()
            {
                runServiceRequests();
            }
        };
        timer.scheduleAtFixedRate(timerTask, getDelay(), getInterval());
    }

    static public PendingWebServiceQueueBackgroundService getInstance()
    {
        return instance;
    }

    static public PendingWebServiceQueueBackgroundService getInstance(Context context)
    {
        if (instance == null) {
            Intent pendingWebServiceIntent = new Intent();
            pendingWebServiceIntent.setAction(".service.PendingWebServiceQueueBackgroundService");
            context.startService(pendingWebServiceIntent);
        }

        return instance;
    }

    public void addPendingWebService(PendingWebService pendingService)
    {
        boolean threadIsRunning = threadIsRunning();
        if (!threadIsRunning) {
            PendingWebServiceQueueApplicationHelper helper = PendingWebServiceQueueApplicationHelper.getInstance(this);
            helper.startService();
        }
    }

    public void addPendingWebService(
        PendingWebService pendingService,
        PendingWebServiceSubscriberInterface subscriber
    ) {
        addPendingWebService(pendingService);
        getSubscribers().put(pendingService, subscriber);
    }

    public boolean threadIsRunning()
    {
        return isRunning;
    }

    protected Map<PendingWebService, PendingWebServiceSubscriberInterface> getSubscribers()
    {
        return PendingWebServiceQueueApplicationHelper.getInstance(this).getPendingWebServiceSubscribers();
    }

    protected Map<PendingWebService, PendingWebServiceSubscriberInterface>
        getMapPendingWebServicePendingWebServiceSubscriberInterface()
    {
        return new HashMap<PendingWebService, PendingWebServiceSubscriberInterface>();
    }

    protected void notifySubscriber(PendingWebService pendingService, RestResponseInterface response)
    {
        if (pendingService != null) {
            Map<PendingWebService, PendingWebServiceSubscriberInterface> subscribers = getSubscribers();
            for (PendingWebService currentService : subscribers.keySet()) {
                if (currentService.getWebService().getId() == pendingService.getWebService().getId()) {
                    subscribers.get(currentService).notify(response);
                }
            }
        }
    }

    protected void runServiceRequests()
    {
        Map<String, PendingWebService> pendingWebServices = getPendingWebServiceQueueManager().getPendingWebServices();
        if (pendingWebServices != null && pendingWebServices.size() > 0) {
            Set<String> ids = pendingWebServices.keySet();
            for (String id : ids) {
                final PendingWebService currentPendingService = pendingWebServices.get(id);
                if (currentPendingService.timeToRun()) {
                    AsyncTaskResponseHandlerInterface responseHandler = new AsyncTaskResponseHandlerInterface() {
                        public void handleResponse(RestResponseInterface response)
                        {
                            notifySubscriber(currentPendingService, response);
                            PendingIntent pendingIntent;
                            if (response != null) {
                                getPendingWebServiceQueueManager().removePendingWebService(currentPendingService);
                                pendingIntent =
                                    currentPendingService.getWebService().getPendingIntentSuccess(response);
                            } else {
                                pendingIntent = currentPendingService.getWebService().getPendingIntentFailure();
                            }
                            try {
                                if (pendingIntent != null) {
                                    pendingIntent.send();
                                }
                            } catch (CanceledException e) {}
                        }
                    };
                    currentPendingService.getWebService().makeRequest(responseHandler);
                }
            }
        } else {
            stopService();
        }
    }

    protected Timer getTimer()
    {
        return new Timer();
    }

    protected int getDelay()
    {
        return DELAY * 1000;
    }

    protected int getInterval()
    {
        return INTERVAL * 1000;
    }

    protected PendingWebServiceQueueManager getPendingWebServiceQueueManager()
    {
        return PendingWebServiceQueueManager.getInstance(this);
    }
}
