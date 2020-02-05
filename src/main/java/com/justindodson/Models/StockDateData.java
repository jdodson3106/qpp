package com.justindodson.Models;

import java.util.*;

/*******************************************************************
* This is a temporary data storage for holding stock date data
* this will eventually be rolled into a shared SQL db to minimize the
* number of API calls to find dates.
********************************************************************/

public class StockDateData {

    // == fields ==
    public static int id = 1;
    public final List<StockDateItem> stockItems = new ArrayList<>();

    // == Constructors ==
    public StockDateData() {

    }

    // == Public Methods ==

    // return an unmodifiable list to get all items
    public List<StockDateItem> getItems() {
        return Collections.unmodifiableList(stockItems);
    }

    // retrieve the dates map by a given stock symbol
    public Map<Integer, String> getDatesByStockSymbol(String stockSymbol) {
        for(StockDateItem stock: stockItems) {
            if(stock.getStockSymbol().equalsIgnoreCase(stockSymbol)) {
                return stock.getDates();
            }
        }

        return null;
    }

    // return stock item based on stock symbol search
    public StockDateItem getStockItemBySymbol(String stockSymbol) {
        for(StockDateItem stock: stockItems) {
            if(stock.getStockSymbol().equalsIgnoreCase(stockSymbol)){
                return stock;
            }
        }
        return null;
    }
    // get the stockdateitem object by the id
    public StockDateItem getStockItemById(int id) {
        for(StockDateItem item : stockItems) {
            if(item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    // assigns an id to the new stock object, adds to data list, then increments id variable for next object.
    public void addStockDateItem(StockDateItem newStock) {
        newStock.setId(id);
        stockItems.add(newStock);
        id++;
    }

    // removes a data object by the id
    public void removeStockDateItemById(int id) {
        ListIterator<StockDateItem> stockDateItemListIterator = stockItems.listIterator();

        while(stockDateItemListIterator.hasNext()) {
            StockDateItem item = stockDateItemListIterator.next();

            if(item.getId() == id) {
                stockItems.remove(item);
                break;
            }
        }
    }

    // removes a data object by the stock symbol
    public void removeStockDateItemByStockSymbol(String stockSymbol) {
        ListIterator<StockDateItem> stockDateItemListIterator = stockItems.listIterator();

        while(stockDateItemListIterator.hasNext()) {
            StockDateItem item = stockDateItemListIterator.next();

            if(item.getStockSymbol().equalsIgnoreCase(stockSymbol)) {
                stockItems.remove(item);
                break;
            }
        }
    }

    // replaces an old stock entry with the newly updated entry
    public void updateStockDateItem(StockDateItem updatedStock) {
        ListIterator<StockDateItem> stockIterator = stockItems.listIterator();

        while(stockIterator.hasNext()) {
            StockDateItem toUpdate = stockIterator.next();

            if(toUpdate.equals(updatedStock)) {
                stockIterator.set(updatedStock);
                break;
            }
        }
    }


}
