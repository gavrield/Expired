package com.myapps.expired.DAL.data;

import java.util.ArrayList;
import java.util.List;

public class SupplierEntity{

    private String Name;
    private List<String> productsBarcodes = new ArrayList<>();

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public List<String> getProducts() {
        return productsBarcodes;
    }

    public void setProducts(List<String> products) {
        this.productsBarcodes = products;
    }

    public void addProductBarcode(String barcode){
        if (!productsBarcodes.contains(barcode))
            productsBarcodes.add(barcode);
    }
}
