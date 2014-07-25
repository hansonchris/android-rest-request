# android-rest-request

 - Provides a simple interface for making HTTP requests asynchronously
 - Allows client complete control over request and response handling
 - Optionally queues failed (no response) requests to be run in the future
  - Client sets frequency to retry
  - Works in background service, so requests can be made when the app is not running
  - Queue is stored persistently
  - Background service can be started when device boots

## Setup
### Basic procedure

 1. Download this project and add it to your project's build path.
 1. If you haven't already created an `Application` subclass, create one that has at least this in the `onCreate()` method (more on this below in the __Other sample code__ section): `PendingWebServiceQueueApplicationHelper.getInstance(this);`
 1. Register your `Application` subclass in the manifest by adding the name of your `Application` subclass as an attribute to the `application` node. For example: `android:name=".MyApplication"`

### Optional additional steps

Queued HTTP requests are run on a regular interval, specified by you, after the failure in a background service. If you'd like queued HTTP requests to be run when the device reboots, as opposed to the next time the app starts after a reboot, create a `BroadcastReceiver` subclass, and register the `BroadcastReceiver` in the manifest. You can find more on this below in the __Other sample code__ section.

## Usage

This library includes classes to handle GET, POST, PUT, and DELETE requests. The classes are:
 - `WebServiceGetAbstract`
 - `WebServicePostAbstract`
 - `WebServicePutAbstract`
 - `WebServiceDeleteAbstract`

All four of those inherit from `WebServiceAbstract`. So you'll extend one of those four classes, depending on which HTTP method you need.

### Sample GET request

Here's a class which makes a GET request to the Yahoo Finance API for stock quotes:
```java
public class GetYahooFinanceQuote extends WebServiceGetAbstract
{
    /**
     * This property is implementation-specific,
     * used to hold the stock symbol to be used by getUri()
     */
    protected String symbol;

    public GetYahooFinanceQuote(Context context)
    {
        super(context);
    }

    /**
     * This method is implementation-specific,
     * used to set the stock symbol to be used by getUri()
     */
    public void setStockSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    protected String getDomain()
    {
        return "query.yahooapis.com";
    }

    protected String getUri()
    {
        String uri = "/v1/public/yql";
        String yql = "select * from yahoo.finance.quote where symbol = '" + symbol + "'";
        try {
            yql = URLEncoder.encode(yql, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String queryString = "?q=" + yql + "&format=json&diagnostics=false&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

        return uri + queryString;
    }

    protected boolean canMakeRequest()
    {
        return (symbol != null);
    }
}
```

As you can see, you simply inherit from `WebServiceGetAbstract` and implement the specifics, which are often very simple.

To make the request and handle the response, you'll do something like this:
```java
GetYahooFinanceQuote api = new GetYahooFinanceQuote(this);
api.setStockSymbol(secFiling.getIssuer().getTradingSymbol());
AsyncTaskResponseHandlerInterface responseHandler = new AsyncTaskResponseHandlerInterface() {
    public void handleResponse(RestResponseInterface response)
    {
        handleGetFinanceQuoteServiceResponse(view, response);
    }
};
api.makeRequest(responseHandler);
```

There are four things happening in the above snippet:
 1. Instantiate your class that extends `WebServiceAbstract`.
 1. Set any implementation-specific values, such as the trading symbol in the example.
 1. Define and create an instance of `AsyncTaskResponseHandlerInterface` which implements the `handleResponse` method.
 1. Make the request and pass in your `AsyncTaskResponseHandlerInterface`.

### Sample POST request + queueing if the request fails

Here's a class which makes a POST request:
```java
public class PostAccountSettings extends WebServicePostAbstract
{
    /**
     * This property is implementation-specific,
     * used to hold values for the post body
     */
    Map<String, String> values;

    public PostAccountSettings(Context context)
    {
        super(context);
    }

    /**
     * This method is implementation-specific,
     * used to set values for the post body
     */
    public void setValues(Map<String, String> values)
    {
        this.values = values;
    }

    /**
     * Override default implementation so that this request can be queued
     */
    public String serializeValues()
    {
        Gson gson = new Gson();
        String encoded = gson.toJson(values);

        return encoded;
    }

    /**
     * Override default implementation so that this request can be queued
     */
    public void unserializeValues(String values)
    {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> decoded = gson.fromJson(values, type);
        this.values = decoded;
    }

    /**
     * Override default implementation, which is simpy an MD5 of class name
     * This is used for the identifier when the serialized request is stored
     * In this case, I can have more than one request by this class queued,
     * so I want to differentiate by the keys of the values being posted
     */
    public String getId()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(getUri());
        Set<String> keys = values.keySet();
        for (String key : keys) {
            builder.append(key);
        }

        return md5(builder.toString());
    }

    protected String getPostBody()
    {
        return serializeValues();
    }

    protected String getDomain()
    {
        return Config.DOMAIN;
    }

    protected String getUri()
    {
        return Config.POST_ACCOUNT_SETTINGS;
    }

    protected boolean canMakeRequest()
    {
        return (values != null);
    }
}
```

Note the methods, which were not present in the GET example above. `getPostBody()` is required by the `WebServicePostAbstract` class. The other methods, `serializeValues()`, `unserializeValues()`, and `getId()`, are to support queueing the request in case of failure. `getId()` is optinonal and is only overridden if you want to queue multiple requests of the same class.

To make the request, handle the response, and queue the request if it fails, you'll do something like this:
```java
PostAccountSettings api = getApi();
Map<String, String> values = new HashMap<String, String>();
values.put(NOTIFICATION_THRESHOLD_DOLLAR_AMOUNT, String.valueOf(value));
api.setValues(values);
AsyncTaskResponseHandlerInterface responseHandler = new AsyncTaskResponseHandlerInterface() {
    public void handleResponse(RestResponseInterface response)
    {
        handleAccountSettingsPostResponse(response);
    }
};
PendingWebServiceQueueParams webServiceQueueParams = new PendingWebServiceQueueParams();
try {
    webServiceQueueParams.setFrequency(30, Calendar.SECOND);
} catch (Exception e) {
    e.printStackTrace();
}
api.makeRequest(responseHandler, webServiceQueueParams);
```

This is _almost_ the same as in the GET example above, except that we pass `PendingWebServiceQueueParams` to the `makeRequest()` method. This is all that it takes to tell the application to queue the request if it fails. Upon failure, the app will make the request every 30 seconds (or whatever your parameters are) until it succeeds. This happens even if your app isn't in the foreground, and it can persist across device reboots.

### Handling the response

The response comes packaged in a `RestResponseInterface` object, which has two self-explanatory methods:
 - `public int getStatusCode()`
 - `public String getResponseBody()`

This object is passed to the `AsyncTaskResponseHandlerInterface.handleResponse()` method which you implemented. The rest is up to you!

## Other sample code
### Sample `Application` subclass

The thing of importance is the line `PendingWebServiceQueueApplicationHelper.getInstance(this);`. This starts the background service when your app starts, and it continues to run, even if the app is no longer running, until there are no pending requests to make, at which point it stops.
```java
package com.yourproject;

import android.app.Application;

import com.dridian.android_rest_request.PendingWebServiceQueueApplicationHelper;

public class MyApplication extends Application
{
    public void onCreate()
    {
        super.onCreate();
        /**
         * This next line starts the background
         * service that runs queued HTTP request
         * which failed previously
         */
        PendingWebServiceQueueApplicationHelper.getInstance(this);
    }
}
```

### Sample `BroadcastReceiver` subclass to handle queued requests after a reboot
```java
package com.dridian.android_rest_request_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver
{
    public void onReceive(Context context, Intent intent) 
    {
        /**
         * Do nothing, handled in Application subclass
         * because the onCreate() method will run.
         * Just be sure that your Application subclass has
         * something like this in the onCreate() method:
         * PendingWebServiceQueueApplicationHelper.getInstance(this);
         */
    }
}
```

And don't forget to
 register your `BroadcastReceiver` in the manifest. Add this in the `application` node (change the package name accordingly):
```xml
<receiver android:name="com.dridian.android_rest_request_test.BootBroadcastReceiver">
      <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
        <category android:name="android.intent.category.HOME" />
      </intent-filter>
</receiver>
```

And, of course, you'll need permission to receive boot notifications, so add this in the manifest's `application` node:
```xml
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```
