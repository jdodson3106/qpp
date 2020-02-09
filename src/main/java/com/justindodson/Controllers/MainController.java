package com.justindodson.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.justindodson.Models.Stock;
import com.justindodson.Models.StockData;
import com.justindodson.Models.StockDateData;
import com.justindodson.Models.StockDateItem;
import com.justindodson.Service.StockSearch;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.toMap;

public class MainController implements Initializable {

    public void initialize(URL location, ResourceBundle resources) {
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockSymbol"));
        currentPriceColumn.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
        qppPriceColumn.setCellValueFactory(new PropertyValueFactory<>("qppPrice"));
        percentageDifferenceColumn.setCellValueFactory(new PropertyValueFactory<>("percentDiff"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
    }


    private static StockDateData stockDateData = new StockDateData();
    private static StockData stockData = new StockData();
    private StockSearch stockSearch;
    private Double currentStockPrice = null;
    private Double marketCap = null;
    private Double qppPrice;
    private String searchQuery;

    // text fields
    @FXML
    private TextField stockSearchBar;
    @FXML
    private JFXTextField stockSymbolResult;
    @FXML
    private JFXTextField stockCompanyResult;
    @FXML
    private JFXTextField stockMarketResult;
    @FXML
    private JFXTextField stockPriceLabel;
    @FXML
    private JFXTextField marketCapLabel;

    // buttons
    @FXML
    private JFXButton qppCalculateBtn;
    @FXML
    private Button stockPriceButton;
    @FXML
    private JFXButton clearTable;

    // spinners
    @FXML
    private JFXSpinner searchSpinner;
    @FXML
    private JFXSpinner loadTableSpinner;

    // combo boxes
    @FXML
    private JFXComboBox optionsDateSelector;

    // table elements
    @FXML
    private TableView qppTable;
    @FXML
    private TableColumn stockColumn;
    @FXML
    private TableColumn currentPriceColumn;
    @FXML
    private TableColumn qppPriceColumn;
    @FXML
    private TableColumn percentageDifferenceColumn;
    @FXML
    private TableColumn dateColumn;


    /***********************
    // == FXML METHODS ==
    ************************/
    @FXML
    public void close(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    public void searchButtonClicked(ActionEvent event){
        clearSearchResults();
        Runnable task = () -> {
            this.searchQuery = stockSearchBar.getText();
            searchSpinner.setVisible(true);

            if(searchQuery.equals("")) {
                clearSearchResults();
            } else {
                stockSearch = new StockSearch(this.searchQuery);
                try {
                    JSONObject searchResult = stockSearch.performSearch();
                    showSearchResults(searchResult);
                    getCurrentStockPrice();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            searchSpinner.setVisible(false);
        };

        Thread thread = new Thread(task);
        thread.start();

        // TODO: 2020-02-04 CREATE METHOD TO CALL THE API WARNING DIALOG
    }

    @FXML
    public void clearSearchResults(ActionEvent event) {
        clearSearchResults();
        stockSearchBar.clear();
    }

    @FXML
    private void calculateQppStockData(ActionEvent event) {
        String stock = stockSymbolResult.getText();
        Object selectedDate = optionsDateSelector.getValue();
        qppPrice = null;

        if(currentStockPrice == null || marketCap == null) {
            currentStockPrice = stockSearch.getCurrentPriceAndMarketCap(stock).get("price");
            marketCap = stockSearch.getCurrentPriceAndMarketCap(stock).get("marketCap");
        }

        if(selectedDate == null) {
            showNoDateWarning(event);
        } else {
            try {
                Date date = new SimpleDateFormat("MM/dd/yyyy").parse(optionsDateSelector.getValue().toString());
                String dateString = new SimpleDateFormat("MM/dd/yyyy").format(date);
                StockDateItem item = stockDateData.getStockItemBySymbol(stock);
                int timestamp = item.getTimestampByDateString(dateString);

                Runnable task = () -> {
                    loadTableSpinner.setVisible(true);

                    qppPrice = stockSearch.calculateQppPrice(stock, timestamp);
                    System.out.println("Stock: " + stock + " current Price: " + currentStockPrice + " QPP: " + qppPrice + " date: " + selectedDate);
                    populateTableData(stock, currentStockPrice, qppPrice, selectedDate.toString());

                    loadTableSpinner.setVisible(false);
                };

                new Thread(task).start();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @FXML
    private void clearTable(ActionEvent event) {
        qppTable.getItems().clear();
    }

    @FXML
    private void onEnter(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
            ActionEvent ae = new ActionEvent();
            searchButtonClicked(ae);
        }
    }

    /****************************
    // == PRIVATE UTIL METHODS ==
     ****************************/

    // This method will populate the stock information based on the search results
    private void showSearchResults(JSONObject searchResult) {
        if(searchResult.size() != 0) {
            // access data and update view
            populateDropdown(searchResult.get("symbol").toString());
            qppCalculateBtn.setDisable(false);
            stockSymbolResult.setText(searchResult.get("symbol").toString());
            stockCompanyResult.setText(searchResult.get("name").toString());
            stockMarketResult.setText(searchResult.get("stock_exchange_long").toString());
        }
        else {
            // update view to show not a valid stock
            qppCalculateBtn.setDisable(true);
            stockPriceButton.setDisable(true);
            stockSymbolResult.setText(searchQuery.toUpperCase() + ": Did not return a valid result.");
            stockCompanyResult.setText("");
            stockMarketResult.setText("");
            stockPriceLabel.setText("$ ");
        }
    }

    private void getCurrentStockPrice() {
        Runnable task = () -> {
            Map<String, Double> priceAndCapData = stockSearch.getCurrentPriceAndMarketCap(stockSymbolResult.getText());
            currentStockPrice =  priceAndCapData.get("price");
            marketCap = priceAndCapData.get("marketCap");
            if(currentStockPrice != null) {
                NumberFormat formatter = NumberFormat.getInstance();
                Platform.runLater(() -> stockPriceLabel.setText("$ " + currentStockPrice));
                Platform.runLater(() -> marketCapLabel.setText("$ " + formatter.format(marketCap)));
            }
        };
        new Thread(task).start();
    }

    // when the search is made, this method will populate the dropdown list with the correct dates.
    private void populateDropdown(String symbol) {
        Map<Integer, String> dates = new HashMap<>();

        // create a timestamp from today
        long timestamp = (new Date().getTime() / 1000L);

        // check if the entry is available already. If not then make the api call , else use the existing entry.
        if(!isStockDataAvailable(symbol)){
            dates = stockSearch.getOptionsDates(symbol, String.valueOf(timestamp));
            addSearchToDataModel(symbol, dates); // create db entry
        } else{

            // TODO: 2020-01-12 - Create logic to rid of any old dates.
            dates = stockDateData.getDatesByStockSymbol(symbol);
        }

        // sort map by timestamp
        Map<Integer, String> sortedDates = dates.entrySet().stream().sorted(comparingByKey()).collect(
                toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));

        // Create new array list and port the dates from the date map into the list to put into the dropdown
        ArrayList dateStrings = new ArrayList();
        for (Map.Entry<Integer, String> entry: sortedDates.entrySet()) {
            dateStrings.add(entry.getValue());
        }

        // Create the observable list from the array list of date strings and populate the dropdown
        ObservableList dateList = FXCollections.observableList(dateStrings);
        optionsDateSelector.getItems().addAll(dateList);

    }

    // adds new search to data model if it does not exist.
    private void addSearchToDataModel(String stockSymbol, Map<Integer, String> datesMap) {
        StockDateItem newItem = new StockDateItem(stockSymbol, datesMap);
        if(stockDateData.getStockItemBySymbol(stockSymbol) == null) {
            stockDateData.addStockDateItem(newItem);
        }
    }

    // Check if the stock is already in the data model.
    private boolean isStockDataAvailable(String stockSymbol) {
        if(stockDateData.getDatesByStockSymbol(stockSymbol) != null) {
            return true;
        }
        return false;
    }

    // clears the UI data from previous search
    private void clearSearchResults() {
        qppCalculateBtn.setDisable(true);
        stockSymbolResult.setText("");
        stockCompanyResult.setText("");
        stockMarketResult.setText("");
        stockPriceLabel.setText("$ ");
        marketCapLabel.setText("$ ");
        optionsDateSelector.getItems().clear();
    }

    private void populateTableData(String stockSymbol, Double currentStockPrice, double qppPrice, String date) {
        if(currentStockPrice == null || currentStockPrice <= 0) {
            System.out.println("Null currentStockPrice");
        } else{
            double percentDiff = calculatePercentageDifference(currentStockPrice, qppPrice);
            Stock newStock = new Stock(stockSymbol, currentStockPrice, qppPrice, percentDiff, date);
            stockData.addNewStock(newStock);
            Runnable task = () -> {
                qppTable.getItems().add(newStock);
//            System.out.println("Populate Table");
            };
            new Thread(task).start();
        }

    }

    private double calculatePercentageDifference(double currentStockPrice, double qppPrice) {
        return ((qppPrice - currentStockPrice) / currentStockPrice) * 100;
    }

    // opens the date warning modal to alert user no date was selected
    private void showNoDateWarning(ActionEvent event) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(
                    ModalController.class.getResource("/date_modal.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(root, 350, 350));
        stage.setTitle("Warning - No Date Selected");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(
                ((Node)event.getSource()).getScene().getWindow() );
        stage.show();
    }


    private void showApiWarningModal(ActionEvent event) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(
                    ModalController.class.getResource("/api_error.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(root, 350, 350));
        stage.setTitle("Warning - API Call Failure");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(
                ((Node)event.getSource()).getScene().getWindow() );
        stage.show();
    }



}
