package com.justindodson.Service;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;


public class StockSearch {

    private String searchString;
    private boolean isSearchFound;
    private static HttpURLConnection connection;

    // Constructors
    public StockSearch(String searchString) {
        this.searchString = searchString;
    }


    // Getters and Setters
    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public boolean isSearchFound() {
        return isSearchFound;
    }

    public void setSearchFound(boolean searchFound) {
        isSearchFound = searchFound;
    }


    /**************************
    *      PUBLIC METHODS
    ***************************/
    public Map<String, String> performSearch() {
        String apiURL = "https://api.worldtradingdata.com/api/v1/stock?symbol=" + this.searchString + "&api_token=" + System.getenv("REALTIME_API_KEY");
        org.json.JSONObject returnObject = new org.json.JSONObject(getResponseContent(apiURL, null).toString());
        Map<String, String> searchResults = new HashMap<>();

        if(returnObject.has("Message")){
            System.out.println("ERROR");
            searchResults.put("error", "Invalid Stock Symbol");
        }
        else{
            searchResults = parseStockSearchData(returnObject);
        }
        return searchResults;
    }


    public HashMap<Integer, String> getOptionsDates(String stockSymbol, String timestamp) {
        StringBuffer responseContent = optionsApiCall(stockSymbol, timestamp);
        HashMap<Integer, String> dates = parseDateOptions(responseContent.toString());
        return dates;
    }

    // this will return the qpp price and options data (total contracts)
    public Map<String, Double> getQppOptionsData(String stock, int timestamp) {
        Map<String, Double> optionsData = new HashMap<>();

        StringBuffer responseContent = optionsApiCall(stock, String.valueOf(timestamp));

        optionsData.put("qppPrice", getQppPrice(responseContent.toString()));
        optionsData.put("calls", getCalls(responseContent.toString()));
        optionsData.put("puts", getPuts(responseContent.toString()));

        return optionsData;
    }

    /***********************
    *    PRIVATE METHODS
    ************************/

    private StringBuffer optionsApiCall(String stockSymbol, String timestamp) {
        String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-options?symbol=" + stockSymbol + "&date=" + timestamp;
        Map<String, String> headers = new HashMap<>();
        headers.put("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com");
        headers.put("x-rapidapi-key", System.getenv("RAPID_API_KEY"));

       return getResponseContent(url, headers);

    }

    private StringBuffer getResponseContent(String url, Map<String,String> headers){
        try{
            connection.disconnect();
        } catch (NullPointerException e) {
            System.out.println("Closed Already");
        }

        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();


        try {
            URL requestURL = new URL(url);
            connection = (HttpURLConnection) requestURL.openConnection();

            // Request setup
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // input headers if needed
            if(headers != null) {
                headers.forEach((k, v) -> {
                   connection.setRequestProperty(k, v);
                });
            }

            // GET status code
            int statusCode = connection.getResponseCode();

            if(statusCode > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null ) {
                    responseContent.append(line);
                }
                reader.close();
            } else { // return the input stream response body
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            connection.disconnect();
        }


        return responseContent;
    }

    // parse the price and market cap from the response API
    private Map<String, String> parseStockSearchData(org.json.JSONObject jsonDataObject) {
        Map<String, String> jsonData = new HashMap<>();

        org.json.JSONArray dataArray = new org.json.JSONArray(jsonDataObject.get("data").toString());

        String symbol = dataArray.getJSONObject(0).get("symbol").toString();
        jsonData.put("symbol", symbol);

        String name = dataArray.getJSONObject(0).get("name").toString();
        jsonData.put("name", name);

        String stock_exchange_long = dataArray.getJSONObject(0).get("stock_exchange_long").toString();
        jsonData.put("stock_exchange_long", stock_exchange_long);

        String price = dataArray.getJSONObject(0).get("price").toString();
        jsonData.put("price", price);

        String marketCap = dataArray.getJSONObject(0).get("market_cap").toString();
        jsonData.put("market_cap", marketCap);

        return jsonData;
    }


    private HashMap<Integer,String> parseDateOptions(String responseBody) {
        HashMap<Integer, String> datesMap = new HashMap<>();
        org.json.JSONObject object = new org.json.JSONObject(responseBody);
        org.json.JSONObject metaData = object.getJSONObject("meta");

        org.json.JSONArray dates = metaData.getJSONArray("expirationDates");

        for(int i = 0; i < dates.length(); i++) {
            datesMap.put((Integer) dates.get(i), getDateFromTimestamp((Integer) dates.get(i)));
        }

        return datesMap;
    }

    private double getQppPrice(String responseBody) {
        double qppPrice = 0;
        double productTotal = 0;
        double oiTotal = 0;
        org.json.JSONObject object = new org.json.JSONObject(responseBody);
        org.json.JSONObject contracts = object.getJSONObject("contracts");

        org.json.JSONArray calls = contracts.getJSONArray("calls");

        for(Object data : calls) {
            try {
                org.json.JSONObject contract = new org.json.JSONObject(data.toString());
                org.json.JSONObject strike = contract.getJSONObject("strike");
                org.json.JSONObject openInterest = contract.getJSONObject("openInterest");
                oiTotal += openInterest.getDouble("raw");
                productTotal += (strike.getDouble("raw") * openInterest.getDouble("raw"));
            } catch (JSONException je) {
                je.printStackTrace();
                if(je.getMessage().contains("JSONObject[\"openInterest\"] not found")){
                    org.json.JSONObject contract = new org.json.JSONObject(data.toString());
                    org.json.JSONObject strike = contract.getJSONObject("strike");
                    double openInterest = 0.0;
                    oiTotal += openInterest;
                    productTotal += (strike.getDouble("raw") * openInterest);
                }
            }
        }
        if(oiTotal == 0) {
            return 0;
        } else{
            qppPrice = productTotal / oiTotal;
            return qppPrice;
        }
    }

    private double getPuts(String responseBody) {
        org.json.JSONObject object = new org.json.JSONObject(responseBody);
        org.json.JSONObject contracts = object.getJSONObject("contracts");

        org.json.JSONArray puts = contracts.getJSONArray("puts");
        return puts.length();
    }

    private double getCalls(String responseBody) {
        org.json.JSONObject object = new org.json.JSONObject(responseBody);
        org.json.JSONObject contracts = object.getJSONObject("contracts");

        org.json.JSONArray calls = contracts.getJSONArray("calls");
        return calls.length();
    }

    private String getDateFromTimestamp(int timestamp) {
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Instant time = Instant.ofEpochSecond(timestamp);
        Date date = Date.from(time);
        return formatter.format(date);
    }

}
