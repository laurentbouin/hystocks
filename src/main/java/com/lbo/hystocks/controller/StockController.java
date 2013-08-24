package com.lbo.hystocks.controller;

import com.lbo.hystocks.command.YahooStockHistoryCommand;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lbouin
 * Date: 17/08/13
 * Time: 15:46
 */

@Controller
@RequestMapping("/stock")
public class StockController {


    //TODO: java.lang.NullPointerException
    //	com.lbo.hystocks.controller.StockController.getStock(StockController.java:44)
    //	com.lbo.hystocks.controller.StockController.getStock(StockController.java:49)



    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(value = "/{symbol}")
    @ResponseBody
    public String getStock( @PathVariable String symbol ) {

        Map<String, Object> result = new LinkedHashMap<String, Object>();

        HystrixCommand<String> yahooStockCommand = (HystrixCommand<String>) applicationContext.getBean("yahooStockHistoryCommand", symbol);
        HystrixCommand<Map<String, String>> googleStockInfoCommand = (HystrixCommand<Map<String, String>>) applicationContext.getBean("googleStockInfoCommand", symbol);

        String yahoohistory = yahooStockCommand.execute();
        Map<String, String> googleInfos = googleStockInfoCommand.execute();

        result.putAll(googleInfos);
        result.put("history", yahoohistory);

        ObjectMapper mapper = new ObjectMapper();

        String s = null;

        try {
            s = mapper.writeValueAsString(googleInfos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    @RequestMapping(value = "/view/{symbol}", method = RequestMethod.GET)
    public String view(@PathVariable String symbol, ModelMap model) {

        HystrixCommand<String> yahooStockCommand = (HystrixCommand<String>) applicationContext.getBean("yahooStockHistoryCommand", symbol);
        HystrixCommand<Map<String, String>> googleStockInfoCommand = (HystrixCommand<Map<String, String>>) applicationContext.getBean("googleStockInfoCommand", symbol);

        String yahooHistory = yahooStockCommand.execute();
        Map<String, String> googleInfos = googleStockInfoCommand.execute();

        model.addAllAttributes(googleInfos);

        model.addAttribute( "symbol", symbol);
        model.addAttribute( "history", yahooHistory);

        return "list2";

    }

    @RequestMapping(value = "/metrics")
    @ResponseBody
    public String getMetrics() {

        HystrixCommandMetrics yahooStockMetrics = HystrixCommandMetrics.getInstance(HystrixCommandKey.Factory.asKey(YahooStockHistoryCommand.class.getSimpleName()));
        // print out metrics
        StringBuilder out = new StringBuilder();
        out.append("\n");
        out.append("# YahooStockHistoryCommand: " + getStatsStringFromMetrics(yahooStockMetrics)).append("\n");
        return (out.toString());
    }

    private String getStatsStringFromMetrics(HystrixCommandMetrics metrics) {
        StringBuilder m = new StringBuilder();
        if (metrics != null) {
            HystrixCommandMetrics.HealthCounts health = metrics.getHealthCounts();
            m.append("Requests: ").append(health.getTotalRequests()).append(" ");
            m.append("Errors: ").append(health.getErrorCount()).append(" (").append(health.getErrorPercentage()).append("%)   ");
            m.append("Mean: ").append(metrics.getExecutionTimePercentile(50)).append(" ");
            m.append("75th: ").append(metrics.getExecutionTimePercentile(75)).append(" ");
            m.append("90th: ").append(metrics.getExecutionTimePercentile(90)).append(" ");
            m.append("99th: ").append(metrics.getExecutionTimePercentile(99)).append(" ");
        }
        return m.toString();
    }

}