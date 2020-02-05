package com.justindodson.Models;

import java.util.Objects;

public class Stock {

    // == Fields ==
    private int id;
    private String stockSymbol;
    private double currentPrice;
    private double qppPrice;
    private double percentDiff;
    private String date;

    // == Constructors ==
    public Stock(String stockSymbol, double currentPrice, double qppPrice, double percentDiff, String date) {
        this.stockSymbol = stockSymbol;
        this.currentPrice = currentPrice;
        this.qppPrice = qppPrice;
        this.percentDiff = percentDiff;
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
        return qppPrice;
    }

    public void setQppPrice(double qppPrice) {
        this.qppPrice = qppPrice;
    }

    public double getPercentDiff() {
        return percentDiff;
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
}
