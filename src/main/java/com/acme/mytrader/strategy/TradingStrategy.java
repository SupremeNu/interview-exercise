package com.acme.mytrader.strategy;

import java.util.HashMap;
import java.util.Map;

import com.acme.mytrader.execution.ExecutionService;
import com.acme.mytrader.price.PriceListener;

import javafx.util.Pair;

/**
 * <pre>
 * User Story: As a trader I want to be able to monitor stock prices such
 * that when they breach a trigger level orders can be executed automatically
 * </pre>
 */
public class TradingStrategy implements PriceListener {
    private ExecutionService executionService;
    private Map<String, Pair<Integer, Double>> stocks = new HashMap<String, Pair<Integer, Double>>();

    /**
     * Create the trading strategy where we can monitor stock prices and specify when to buy them.
     * @param executionService the object used to buy and sell each stock.
     */
    public TradingStrategy(ExecutionService executionService) {
        this.executionService = executionService;
	}

    /**
     * Adds a stock which we will now monitor.
     * @param stock name
     * @param volume of the stock which we will buy
     * @param price at which price we will buy the stock
     */
    public void addStock(String stock, int volume, double price) {
        Pair<Integer, Double> volumeAndBuyTrigger = new Pair<Integer, Double>(volume, price);

        // If we currently have that stock listened to, then notify the trader we are replacing it
        if (stocks.containsKey(stock)) {
            System.out.println("Replacing current listener on " + stock);
		}

        stocks.put(stock, volumeAndBuyTrigger);
	}

    /**
     * Removes a stock which we are monitoring.
     * @param stock name
     */
    public void removeStock(String stock) {
        if (stocks.containsKey(stock)) {
            stocks.remove(stock);
            System.out.println(stock + " Removed");
		} else {
            System.err.println(stock + " is not being monitored!");
		}
	}

     /**
     * Stop monitoring all the stocks which we have.
     */
    public void clearAllStocks() {
        stocks = new HashMap<String, Pair<Integer, Double>>();
	}

     /**
     * Checks if the stock is being monitored.
     * @param stock name
     * @returns boolean of if the stock is being monitored or not
     */
    public boolean isBeingMonitored(String stock) {
        return stocks.containsKey(stock);
	}

     /**
     * Updates the current price of a stock. Check if we want to now buy that stock and if we do then buy it.
     * @param security name of the stock
     * @param price new price which the stock is trading at
     */
    public void priceUpdate(String security, double price) {
        // Make sure we are monitoring the stock
        if (stocks.containsKey(security)) {
            Pair<Integer, Double> volumeAndBuyTrigger = stocks.get(security);
            int volume = volumeAndBuyTrigger.getKey();
            double buyTrigger = volumeAndBuyTrigger.getValue();

            // If the price is equal to or lower than the buy trigger, buy it
            if (price <= buyTrigger) {
                executionService.buy(security, price, volume);

                // Now we have executed the buy command, we can remove this listener
                removeStock(security);

                System.out.println("Bought " + volume + " of " + security + " at " + price + ".");
		    }
		} else {
          System.err.println(security + " is not being monitored!");
		}
    }
}
