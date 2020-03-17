package com.myapps.expired.DAL.data;

import com.myapps.expired.services.NotificationBoundary;

public class EmployeeEntity {

    private String store;
    private String firstName;
    private String lastName;
    private String id;
    private NotificationBoundary[] notifications;

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NotificationBoundary[] getNotifications() {
        return notifications;
    }

    public void setNotifications(NotificationBoundary[] notifications) {
        this.notifications = notifications;
    }
}
