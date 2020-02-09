package com.justindodson.Models;

import java.util.Objects;

public class Stock {

    // == Fields ==
    private int id;
    private String stockSymbol;
    private double currentPrice;
    private String marketCap;
    private double qppPrice;
    private double percentDiff;
    private int calls;
    private int puts;
    private String date;

    // == Constructors ==
    public Stock(String stockSymbol, String marketCap, double currentPrice, double qppPrice, double percentDiff, int calls, int puts, String date) {
        this.stockSymbol = stockSymbol;
        this.currentPrice = currentPrice;
        this.marketCap = marketCap;
        this.qppPrice = qppPrice;
        this.percentDiff = percentDiff;
        this.calls = calls;
        this.puts = puts;
        this.date = date;
    }

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

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getQppPrice() {
        return round(qppPrice, 2);
    }

    public void setQppPrice(double qppPrice) {
        this.qppPrice = qppPrice;
    }

    public double getPercentDiff() {
        return round(percentDiff, 2);
    }

    public void setPercentDiff(double percentDiff) {
        this.percentDiff = percentDiff;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }

    public int getCalls() {
        return calls;
    }

    public void setCalls(int calls) {
        this.calls = calls;
    }

    public int getPuts() {
        return puts;
    }

    public void setPuts(int puts) {
        this.puts = puts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return id == stock.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", stockSymbol='" + stockSymbol + '\'' +
                ", currentPrice=" + currentPrice +
                ", qppPrice=" + qppPrice +
                ", percentDiff=" + percentDiff +
                ", date='" + date + '\'' +
                '}';
    }

    // == private methods ==
    private double round(double value, int places) {
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long temp = Math.round(value);
        return (double) temp / factor;
    }
}
