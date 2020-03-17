package com.myapps.expired.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapps.expired.DAL.data.ExpirationEventEntity;
import com.myapps.expired.DAL.data.NotificationEntity;
import com.myapps.expired.DAL.data.ProductEntity;
import com.myapps.expired.DAL.data.SupplierEntity;
import com.myapps.expired.R;
import com.myapps.expired.services.NotificationBoundary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Massages extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference fbRef = FirebaseDatabase.getInstance().getReference();
    private SharedPreferences preferences;
    private List<NotificationBoundary> notificationBoundaryList = new ArrayList<>();
    private Map<String,ProductEntity> productEntities = new HashMap<>();
    private Map<String,SupplierEntity> supplierEntities = new HashMap<>();
    private Map<String,ExpirationEventEntity> expirationEventEntities = new HashMap<>();
    private int semaphore = 3;
    private String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masseges);
        recyclerView = findViewById(R.id.massages_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        preferences = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        final String store = preferences.getString("store",null);
        String employeeId = preferences.getString("employeeId", null);
        if (store == null || employeeId == null){
            Toast.makeText
                    (
                            this,
                            getString(R.string.not_registered_massage),
                            Toast.LENGTH_SHORT
                    ).show();
            finish();
        }
        fbRef.child(store).child("employees").child(employeeId).child("notifications")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot node: dataSnapshot.getChildren()){
                    notificationBoundaryList.add(node.getValue(NotificationBoundary.class));
                }
                createMaps(store);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (semaphore > 0);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        data = createDataSet();
                        mAdapter = new RecyclerViewAdapter(data);
                        recyclerView.setAdapter(mAdapter);
                    }
                });
            }
        }).start();

    }

    private void createMaps(String store) {
        for (NotificationBoundary n : notificationBoundaryList) {
            final String barcode = n.getProductBarcode();
            final String supplierId = n.getSupplierId();
            final String expirationEventId = n.getExpirationEventId();
            fbRef.child("products").child(barcode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    productEntities.put(barcode, dataSnapshot.getValue(ProductEntity.class));
                    semaphore--;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            fbRef.child("suppliers").child(supplierId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    supplierEntities.put(supplierId, dataSnapshot.getValue(SupplierEntity.class));
                    semaphore--;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            fbRef.child(store).child("expirationEvents").child(expirationEventId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    expirationEventEntities.put(expirationEventId, dataSnapshot.getValue(ExpirationEventEntity.class));
                    semaphore--;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });


        }
    }

    private String[] createDataSet(){
        List<String> notificationList = new ArrayList<>();
        NotificationEntity notification;
        for (NotificationBoundary n: notificationBoundaryList){
            notification = new NotificationEntity();
            notification.setSupplierEntity(supplierEntities.get(n.getSupplierId()));
            notification.setExpirationEventEntity(expirationEventEntities.get(n.getExpirationEventId()));
            notification.setProductEntity(productEntities.get(n.getProductBarcode()));
            notificationList.add(notification.toString());
        }


        return (String[]) notificationList.toArray();
    }
}
