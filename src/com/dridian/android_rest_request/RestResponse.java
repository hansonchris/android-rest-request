package com.dridian.android_rest_request;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

public class RestResponse implements RestResponseInterface
{
	protected HttpResponse httpResponse;
	protected String responseBody;
	
	public RestResponse(HttpResponse httpResponse)
	{
		this.httpResponse = httpResponse;
	}
	
	public int getStatusCode()
	{
		return httpResponse.getStatusLine().getStatusCode();
	}
	
	public String getResponseBody()
	{
		if (responseBody == null)
		{
			StringBuilder stringBuilder = _getStringBuilder();
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
		}
		return responseBody;
	}
	
	protected StringBuilder _getStringBuilder()
	{
		return new StringBuilder();
	}
}