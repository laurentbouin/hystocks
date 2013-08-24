package com.lbo.hystocks.command;

import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicLongProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lbouin
 * Date: 19/08/13
 * Time: 20:05
 */
@Component
@Scope("prototype")
public class GoogleStockInfoCommand extends HystrixCommand<Map<String, String>> {

    private final String ticker;

    public GoogleStockInfoCommand(String ticker) {
        super(HystrixCommandGroupKey.Factory.asKey("GoogleGroup"));
        this.ticker = ticker;
    }

    @Override
    protected Map<String, String> run() throws Exception {

        DynamicBooleanProperty autoFailProperty = DynamicPropertyFactory.getInstance().getBooleanProperty("google.stock.autofail", false);

        // create a property whose value is type long and use 0 as the default
        // if the property is not defined
        DynamicLongProperty delayProperty = DynamicPropertyFactory.getInstance().getLongProperty("google.stock.delay", 0);

        if (autoFailProperty.get()) {
            Logger.warn("Command set to autofail");
            throw new RuntimeException("Command set to autofail");
        }

        Long delay = delayProperty.get();

        if (delay > 0) {
            System.out.println("Sleeping for: " + delay);
            Thread.sleep(delay);
        }

        DynamicStringProperty googleUrl = DynamicPropertyFactory.getInstance().getStringProperty("google.stock.url", "https://www.google.com/finance/info?infotype=infoquoteall&q=");

        // a real example would do work like a network call here
        String strUrl = googleUrl.get() + ticker;

        HttpClient client = new HttpClient();

        // Create a method instance.
        GetMethod method = new GetMethod(strUrl);

        // Provide custom retry handler is necessary
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

        // Execute the method.
        int statusCode = client.executeMethod(method);
        Logger.debug("status code: {}", statusCode);

        if (statusCode != HttpStatus.SC_OK) {
            throw new RuntimeException("Method failed: " + method.getStatusLine());
        }

        // Read the response body.
        byte[] responseBody = method.getResponseBody();

        // Deal with the response.
        // Use caution: ensure correct character encoding and is not binary data
        String jsonData = new String(responseBody);
        Logger.debug("JSON DATA: {}", jsonData);

        Map<String, String> map = null;

        //let's remove the unnecessary characters
        jsonData = jsonData.replace("// [", "");
        jsonData = jsonData.replace("]", "");

        ObjectMapper mapper = new ObjectMapper();

        //convert JSON string to Map
        map = mapper.readValue(jsonData, new TypeReference<HashMap<String, String>>() {
        });

        return map;
    }

    @Override
    protected Map<String, String> getFallback() {
        Logger.warn(" GOOGLE Command: FallBack called for ticker {}", ticker);
        return new HashMap<String, String>();
    }

    static final org.slf4j.Logger Logger = LoggerFactory.getLogger(GoogleStockInfoCommand.class);

}

//https://www.google.com/finance/info?infotype=infoquoteall&q=GOOG

 /*
The Google Finance feed can return some or all of the following
keys:

  avvo    * Average volume (float with multiplier, like '3.54M')
  beta    * Beta (float)
  c       * Amount of change while open (float)
  ccol    * (unknown) (chars)
  cl        Last perc. change
  cp      * Change perc. while open (float)
  e       * Exchange (text, like 'NASDAQ')
  ec      * After hours last change from close (float)
  eccol   * (unknown) (chars)
  ecp     * After hours last chage perc. from close (float)
  el      * After. hours last quote (float)
  el_cur  * (unknown) (float)
  elt       After hours last quote time (unknown)
  eo      * Exchange Open (0 or 1)
  eps     * Earnings per share (float)
  fwpe      Forward PE ratio (float)
  hi      * Price high (float)
  hi52    * 52 weeks high (float)
  id      * Company id (identifying number)
  l       * Last value while open (float)
  l_cur   * Last value at close (like 'l')
  lo      * Price low (float)
  lo52    * 52 weeks low (float)
  lt        Last value date/time
  ltt       Last trade time (Same as "lt" without the data)
  mc      * Market cap. (float with multiplier, like '123.45B')
  name    * Company name (text)
  op      * Open price (float)
  pe      * PE ratio (float)
  t       * Ticker (text)
  type    * Type (i.e. 'Company')
  vo      * Volume (float with multiplier, like '3.54M')
    */