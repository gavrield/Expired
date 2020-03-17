package com.myapps.expired.DAL.data;

import com.google.firebase.database.DatabaseReference;

public class NotificationEntity {

    private ExpirationEventEntity expirationEventEntity;
    private ProductEntity productEntity;
    private SupplierEntity supplierEntity;

    public ExpirationEventEntity getExpirationEventEntity() {
        return expirationEventEntity;
    }

    public void setExpirationEventEntity(ExpirationEventEntity expirationEventEntity) {
        this.expirationEventEntity = expirationEventEntity;
    }

    public ProductEntity getProductEntity() {
        return productEntity;
    }

    public void setProductEntity(ProductEntity productEntity) {
        this.productEntity = productEntity;
    }

    public SupplierEntity getSupplierEntity() {
        return supplierEntity;
    }

    public void setSupplierEntity(SupplierEntity supplierEntity) {
        this.supplierEntity = supplierEntity;
    }

    @Override
    public String toString(){
        return "In " + expirationEventEntity.getExpirationDate()
                +"\n" +  expirationEventEntity.getStore()+ ", " + expirationEventEntity.getWhereToFind()
                + "\nproduct no: " + productEntity.getBarcode() +", " + productEntity.getDescription()
                + "\noriginal amount: " + expirationEventEntity.getAmount()
                + "\nreturn them to: " + supplierEntity.getName();
    }
}
