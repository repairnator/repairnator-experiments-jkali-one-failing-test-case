package org.knowm.xchange.huobi.service;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.huobi.HuobiAdapters;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.io.IOException;

public class HuobiMarketDataService extends HuobiMarketDataServiceRaw implements MarketDataService {

    public HuobiMarketDataService(Exchange exchange) {
        super(exchange);
    }

    @Override
    public Ticker getTicker(CurrencyPair currencyPair, Object... args) throws IOException {
        return HuobiAdapters.adaptTicker(getHuobiTicker(currencyPair), currencyPair);
    }

    @Override
    public OrderBook getOrderBook(CurrencyPair currencyPair, Object... objects) throws IOException {
        return null;
    }

    @Override
    public Trades getTrades(CurrencyPair currencyPair, Object... args) throws IOException {
        return null;
    }

}
