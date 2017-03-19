package com.getsmartkart.smartkart;

/**
 * Created by Nathan on 2017-03-19.
 */

public class ShoppingCartItem {

    // {"id":"1","cart":null,"name":"water","price":"1.00","quantity":"3","percentOff":"0","isOnSale":false,"type":"essentials","image":"lol"}

    private String id;
    private String cart;
    private String name;
    private double price;
    private int quantity;
    private int percentOff;
    private boolean isOnSale;
    private String type;
    private String image;

    ShoppingCartItem(){}


    public String getId() {
        return id;
    }

    public String getCart() {
        return cart;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPercentOff() {
        return percentOff;
    }

    public boolean isOnSale() {
        return isOnSale;
    }

    public String getType() {
        return type;
    }

    public String getImage() {
        return image;
    }
}
