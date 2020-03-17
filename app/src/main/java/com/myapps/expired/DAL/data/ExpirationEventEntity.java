package com.myapps.expired.DAL.data;

import java.sql.Date;

public class ExpirationEventEntity{

    private String store;
    private String id;
    private String productBarcode;
    private Integer amount;
    private Date expirationDate;
    private Integer whenToNotify; //in days
    private String whereToFind;


    public ExpirationEventEntity(){};

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    public Integer getWhenToNotify() {
        return whenToNotify;
    }

    public void setWhenToNotify(Integer whenToNotify) {
        this.whenToNotify = whenToNotify;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getWhereToFind() {
        return whereToFind;
    }

    public void setWhereToFind(String whereToFind) {
        this.whereToFind = whereToFind;
    }

    public Integer getAmount() {
        return amount;
    }


}
