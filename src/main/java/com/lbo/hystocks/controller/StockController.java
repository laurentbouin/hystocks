package com.lbo.hystocks.controller;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
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
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: lbouin
 * Date: 17/08/13
 * Time: 21:46
 */

@Controller
@RequestMapping("/")
public class StockController {

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(value ="/index", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/csv/{symbol}", method = RequestMethod.GET)
    @ResponseBody
    public String csv(@PathVariable String symbol) throws Exception  {

        HystrixCommand<String> yahooStockCommand = (HystrixCommand<String>) applicationContext.getBean("yahooStockHistoryCommand", symbol);

        Future<String> futureYahoohistory = yahooStockCommand.queue();
        String history = futureYahoohistory.get();

        StringBuilder buffer = new StringBuilder();

        Iterable<String> lines = Splitter.on("\n").split(history);
        Joiner joiner = Joiner.on(",");

        for (String line : lines) {

            if (!line.isEmpty()) {
                Iterable<String> columns = Splitter.on(",").split(line);

                String[] columnsAsArray = Iterables.toArray(columns, String.class);

                buffer = joiner.appendTo(buffer, columnsAsArray[0], columnsAsArray[2],columnsAsArray[3] + "\n");
            }
        }

        return buffer.toString();

    }

    @RequestMapping(value = "/data/{symbol}")
    @ResponseBody
    public String getStock( @PathVariable String symbol ) throws Exception {

        Map<String, Object> result = new LinkedHashMap<String, Object>();

        HystrixCommand<String> yahooStockCommand = (HystrixCommand<String>) applicationContext.getBean("yahooStockHistoryCommand", symbol);
        HystrixCommand<Map<String, String>> googleStockInfoCommand = (HystrixCommand<Map<String, String>>) applicationContext.getBean("googleStockInfoCommand", symbol);

        Future<String> yahoohistory = yahooStockCommand.queue();
        Future<Map<String, String>> googleInfos = googleStockInfoCommand.queue();

        result.putAll(googleInfos.get());
        result.put("history", yahoohistory.get());

        ObjectMapper mapper = new ObjectMapper();

        String s = null;

        try {
            s = mapper.writeValueAsString(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    @RequestMapping(value = "/{symbol}", method = RequestMethod.GET)
    public String view(@PathVariable String symbol, ModelMap model) {

        HystrixCommand<String> yahooStockCommand = (HystrixCommand<String>) applicationContext.getBean("yahooStockHistoryCommand", symbol);
        HystrixCommand<Map<String, String>> googleStockInfoCommand = (HystrixCommand<Map<String, String>>) applicationContext.getBean("googleStockInfoCommand", symbol);

        String yahooHistory = yahooStockCommand.execute();
        Map<String, String> googleInfos = googleStockInfoCommand.execute();

        model.addAllAttributes(googleInfos);

        model.addAttribute( "symbol", symbol);
        model.addAttribute( "history", yahooHistory);

        return "ticker";

    }

    @RequestMapping(value = "/metrics")
    @ResponseBody
    public String getMetrics() {

        HystrixCommandMetrics yahooStockMetrics = HystrixCommandMetrics.getInstance(HystrixCommandKey.Factory.asKey(YahooStockHistoryCommand.class.getSimpleName()));
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