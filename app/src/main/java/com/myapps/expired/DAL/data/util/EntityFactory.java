package com.myapps.expired.DAL.data.util;

import com.myapps.expired.DAL.data.*;

import java.sql.Date;
import java.util.ArrayList;

public class EntityFactory {

    public EmployeeEntity createNewEmployee
            (String store,  String firstName, String lastName)
    throws EntityException{

        if (store.equals("") || firstName.equals("") || lastName.equals(""))
            throw new EntityException();
        EmployeeEntity employee = new EmployeeEntity();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setStore(store);

        return employee;
    }

    public ExpirationEventEntity createNewExpirationEvent
            (String store, String product, Integer amount, Date expirationDate, Integer whenToNotify)
            throws EntityException{

        if (store.equals("") || product.equals("")|| amount == null
                        || expirationDate == null || whenToNotify == null)
            throw new EntityException();
        ExpirationEventEntity expirationEvent = new ExpirationEventEntity();
        expirationEvent.setStore(store);
        expirationEvent.setProductBarcode(product);
        expirationEvent.setAmount(amount);
        expirationEvent.setExpirationDate(expirationDate);
        expirationEvent.setWhenToNotify(whenToNotify);

        return expirationEvent;
    }

    public ProductEntity createNewProductEntity
            (String barcode, String description, String supplier, HowToStore howToStore)
            throws EntityException{
        if (barcode == "" || description == ""|| supplier == "")
            throw new EntityException();
        ProductEntity product = new ProductEntity();
        product.setBarcode(barcode);
        product.setDescription(description);
        product.setSupplier(supplier);
        product.setHowToStore(howToStore);

        return product;
    }



    public SupplierEntity createNewSupplierEntity(String name) throws EntityException {

        if (name == "") throw new EntityException();
        SupplierEntity supplier = new SupplierEntity();
        supplier.setProducts(new ArrayList<String>());
        supplier.setName(name);


        return supplier;
    }

    public NotificationEntity createNewNotificationEntity(SupplierEntity supplier,
                                                          ExpirationEventEntity expirationEvent,
                                                          ProductEntity product)throws EntityException{
        if (supplier == null|| expirationEvent == null || product == null)
            throw new EntityException();
        NotificationEntity notification = new NotificationEntity();
        notification.setSupplierEntity(supplier);
        notification.setExpirationEventEntity(expirationEvent);
        notification.setProductEntity(product);
        return  notification;
    }
}
