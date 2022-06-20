package com.acme.mytrader.strategy;

import com.acme.mytrader.price.PriceSource;
import com.acme.mytrader.execution.ExecutionService;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Assert.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Matchers.*;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TradingStrategyTest {

    TradingStrategy tradingStrategy;
    @Mock PriceSource priceSource;
    @Mock ExecutionService executionService;

    String STOCK = "IBM";
    int VOLUME = 150;
    double PRICE = 200;
    
    @Before
    public void init() {
        tradingStrategy = new TradingStrategy(executionService);
    }
    
    @Test
    public void testAddingRemovingStock() {
        // Add the stock
        tradingStrategy.addStock(STOCK, VOLUME, PRICE);

        // stock should be monitored
        assertTrue(tradingStrategy.isBeingMonitored(STOCK));

        // Remove the stock
        tradingStrategy.removeStock(STOCK);

        // stock should not be monitored
        assertFalse(tradingStrategy.isBeingMonitored(STOCK));
    }

    @Test
    public void clearAllStocks() {
        // Add the stock
        tradingStrategy.addStock(STOCK, VOLUME, PRICE);
        tradingStrategy.addStock("GIB", VOLUME, PRICE);
        tradingStrategy.addStock("META", VOLUME, PRICE);

        // stocks should be monitored
        assertTrue(tradingStrategy.isBeingMonitored(STOCK));
        assertTrue(tradingStrategy.isBeingMonitored("GIB"));
        assertTrue(tradingStrategy.isBeingMonitored("META"));

        // Stop monitoring all the stocks
        tradingStrategy.clearAllStocks();

        // stocks should not be monitored
        assertFalse(tradingStrategy.isBeingMonitored(STOCK));
        assertFalse(tradingStrategy.isBeingMonitored("GIB"));
        assertFalse(tradingStrategy.isBeingMonitored("META"));
    }

    @Test
    public void testBuyingStocks() {
        // Add the stock
        tradingStrategy.addStock(STOCK, VOLUME, PRICE);

        // Update price to above the buy trigger
        tradingStrategy.priceUpdate(STOCK, PRICE + 1.5);
        // Check it hasnt bought it
        Mockito.verify(executionService, Mockito.times(0)).buy(STOCK, VOLUME, PRICE + 1.5);

        // Update price to below the buy trigger
        tradingStrategy.priceUpdate(STOCK, PRICE - 2.5);
        // Check it has bought it at the current price and specified volume
        Mockito.verify(executionService, Mockito.times(1)).buy(STOCK, VOLUME, PRICE - 2.5);
    }
}