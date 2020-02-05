package com.justindodson.Models;

import java.util.Map;
import java.util.Objects;

public class StockDateItem {

    //== fields ==
    private int id;

    private String stockSymbol;
    private Map<Integer, String> dates;


    // == constructors ==
    public StockDateItem(String stockSymbol, Map<Integer, String> dates) {
        this.stockSymbol = stockSymbol;
        this.dates = dates;
    }

    // == public methods ==
    public int getTimestampByDateString(String dateString) {
        for(Map.Entry<Integer, String> date : dates.entrySet()) {
            if (date.getValue().equals(dateString)) {
                return date.getKey();
            }
        }
        return 0;
    }

    // TODO: 2020-01-12 - Make a method to check the saved data and remove any old dates.

    // == Public Methods ==

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public Map<Integer, String> getDates() {
        return dates;
    }

    public void setDates(Map<Integer, String> dates) {
        this.dates = dates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockDateItem that = (StockDateItem) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "StockDateItem{" +
                "id=" + id +
                ", stockSymbol='" + stockSymbol + '\'' +
                ", dates=" + dates +
                '}';
    }
}
