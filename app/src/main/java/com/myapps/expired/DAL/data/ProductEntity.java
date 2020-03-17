package com.myapps.expired.DAL.data;

public class ProductEntity{

    private String barcode;
    private String description;
    private String supplier;
    private HowToStore howToStore;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplierId) {
        this.supplier = supplierId;
    }

    public HowToStore getHowToStore() {
        return howToStore;
    }

    public void setHowToStore(HowToStore howToStore) {
        this.howToStore = howToStore;
    }
}
