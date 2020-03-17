package com.myapps.expired.services;

import java.sql.Date;

public class NotificationBoundary {

    private String productBarcode;
    private String supplierId;
    private String expirationEventId;
    private long dateOfPublish;


    public NotificationBoundary(String pb, String si, String eei, Date date){
        productBarcode = pb;
        supplierId = si;
        expirationEventId = eei;
        dateOfPublish = date.getTime();
    }

    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getExpirationEventId() {
        return expirationEventId;
    }

    public void setExpirationEventId(String expirationEventId) {
        this.expirationEventId = expirationEventId;
    }

    public long getDateOfPublish() {
        return dateOfPublish;
    }

    public void setDateOfPublish(long dateOfPublish) {
        this.dateOfPublish = dateOfPublish;
    }
}
