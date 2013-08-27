package com.lbo.hystocks.command;

import com.google.common.base.Splitter;
import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicLongProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

/**
 * User: lbouin
 * Date: 17/08/13
 * Time: 19:29
 */
@Component
@Scope("prototype")
public class YahooStockHistoryCommand extends HystrixCommand<String> {

    private final String ticker;

    public YahooStockHistoryCommand(String ticker) {
        super(HystrixCommandGroupKey.Factory.asKey("YahooGroup"));
        this.ticker = ticker;
    }

    @Override
    protected String run() throws Exception {

        DynamicBooleanProperty autoFailProperty = DynamicPropertyFactory.getInstance().getBooleanProperty("yahoo.history.autofail", false);

        // create a property whose value is type long and use 1000 as the default
        // if the property is not defined
        DynamicLongProperty delayProperty = DynamicPropertyFactory.getInstance().getLongProperty("command.delay", 1000);

        if (autoFailProperty.get()) {
            Logger.warn("Command set to autofail");
            throw new RuntimeException("Command set to autofail");
        }

        Long delay = delayProperty.get();

        if (delay > 0) {
            Logger.info("Sleeping for: {} ms", delay);
            Thread.sleep(delay);
        }

        // a real example would do work like a network call here
        String strUrl = "http://ichart.finance.yahoo.com/table.csv?s=" + ticker + "&a=0&b=1&c=2008&d=7&e=30&f=2013&ignore=.csv";

        HttpClient client = new HttpClient();

        // Create a method instance.
        GetMethod method = new GetMethod(strUrl);

        // Provide custom retry handler is necessary
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

        // Execute the method.
        int statusCode = client.executeMethod(method);

        if (statusCode != HttpStatus.SC_OK) {
            throw new RuntimeException("Method failed: " + method.getStatusLine());
        }

        // Read the response body.
        byte[] responseBody = method.getResponseBody();

        // Deal with the response.
        // Use caution: ensure correct character encoding and is not binary data

        return new String(responseBody);

        //return the CSV for the ticker
        // return csvData;
    }

    @Override
    protected String getFallback() {
        Logger.warn("YAHOO Command: FallBack called for ticker {}", ticker);
        return null;
    }

    static final org.slf4j.Logger Logger = LoggerFactory.getLogger(YahooStockHistoryCommand.class);

}
