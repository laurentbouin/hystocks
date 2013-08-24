package com.lbo.hystocks.listener;

import com.netflix.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * Created with IntelliJ IDEA.
 * User: lbouin
 * Date: 18/08/13
 * Time: 13:55
 */
public class HystocksListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		System.out.println("ServletContextListener destroyed");
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {

		System.out.println("ServletContextListener started");

        DynamicLongProperty delayProperty = DynamicPropertyFactory.getInstance().getLongProperty("command.delay", 1000);
        DynamicBooleanProperty autoFailProperty = DynamicPropertyFactory.getInstance().getBooleanProperty("yahoo.history.autofail", false);
        DynamicBooleanProperty googleAutoFailProperty = DynamicPropertyFactory.getInstance().getBooleanProperty("google.stock.autofail", false);
        DynamicStringProperty googleUrl = DynamicPropertyFactory.getInstance().getStringProperty("google.stock.url", "https://www.google.com/finance/info?infotype=infoquoteall&q=");

        Logger.info("command.delay: {}", delayProperty.get());
        Logger.info("yahoo.history.autofail: {}", autoFailProperty.get());
        Logger.info("google.stock.url: {}", googleUrl.get());
        Logger.info("google.stock.autofail: {}", googleAutoFailProperty.get());

        delayProperty.addCallback(new Runnable() {
            @Override
            public void run() {
                Logger.info("The delay property has been changed;");
            }
           }
        );
	}

    static final Logger Logger = LoggerFactory.getLogger(HystocksListener.class);

}
