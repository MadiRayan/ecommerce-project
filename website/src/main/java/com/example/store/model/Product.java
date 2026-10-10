package com.example.store.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    private Long id;

    private String name;

    private Prices prices;

    private List<Image> images;

    @JsonProperty("is_in_stock")
    private boolean inStock;


    public Product() {
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Prices getPrices() {
        return prices;
    }


    public void setPrices(Prices prices) {
        this.prices = prices;
    }


    public List<Image> getImages() {
        return images;
    }


    public void setImages(List<Image> images) {
        this.images = images;
    }


    public boolean isInStock() {
        return inStock;
    }


    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }


    public String getStockText() {

        if (inStock) {
            return "In Stock";
        }

        return "Out of Stock";
    }


    public String getDisplayPrice() {

        if (prices == null || prices.price == null) {
            return "";
        }

        BigDecimal value =
                new BigDecimal(prices.price);

        value = value.movePointLeft(
                prices.currencyMinorUnit
        );

        return prices.currencySymbol
                + " "
                + value.stripTrailingZeros()
                       .toPlainString();
    }
    public BigDecimal getPriceValue() {

        if (prices == null || prices.price == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal value =
                new BigDecimal(prices.price);

        return value.movePointLeft(
                prices.currencyMinorUnit
        );
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Prices {

        private String price;

        @JsonProperty("currency_symbol")
        private String currencySymbol;

        @JsonProperty("currency_minor_unit")
        private int currencyMinorUnit;


        public Prices() {
        }


        public String getPrice() {
            return price;
        }


        public void setPrice(String price) {
            this.price = price;
        }


        public String getCurrencySymbol() {
            return currencySymbol;
        }


        public void setCurrencySymbol(
                String currencySymbol) {

            this.currencySymbol = currencySymbol;
        }


        public int getCurrencyMinorUnit() {
            return currencyMinorUnit;
        }


        public void setCurrencyMinorUnit(
                int currencyMinorUnit) {

            this.currencyMinorUnit =
                    currencyMinorUnit;
        }
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Image {

        private String src;


        public Image() {
        }


        public String getSrc() {
            return src;
        }


        public void setSrc(String src) {
            this.src = src;
        }
    }
}