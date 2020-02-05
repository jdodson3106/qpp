package com.justindodson.Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class StockData {

    // == Fields ==
    public static int id = 1;
    private final ArrayList<Stock> stocks = new ArrayList<>();

    // == Constructors ==
    public StockData() {

    }

    public List<Stock> getStocks() {
        return Collections.unmodifiableList(stocks);
    }

    public void addNewStock(Stock newStock) {
        newStock.setId(id);
        stocks.add(newStock);
        id++;
    }

    public void deleteStock(int id) {
        ListIterator<Stock> stockListIterator = stocks.listIterator();

        while(stockListIterator.hasNext()) {
            Stock stock = stockListIterator.next();

            if(stock.getId() == id) {
                stocks.remove(stock);
                break;
            }

        }
    }

    public Stock getStockById(int id) {
        ListIterator<Stock> stockListIterator = stocks.listIterator();

        while (stockListIterator.hasNext()) {
            Stock stock = stockListIterator.next();

            if (stock.getId() == id) {
                return stock;
            }
        }
        return null;
    }

    public void updateStock(Stock updatedStock) {
        ListIterator<Stock> stockListIterator = stocks.listIterator();

        while (stockListIterator.hasNext()) {
            Stock stock = stockListIterator.next();

            if (stock.equals(updatedStock)) {
                stockListIterator.set(updatedStock);
                break;
            }
        }
    }
}
