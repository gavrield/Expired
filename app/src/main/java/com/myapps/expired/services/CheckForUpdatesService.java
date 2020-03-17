package com.myapps.expired.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
import com.myapps.expired.ui.main.Massages;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckForUpdatesService extends Service {

    public final String CHANNEL_ID = "expiredChanel";
    private NotificationManagerCompat nManager;
    private DatabaseReference fbRef = FirebaseDatabase.getInstance().getReference();
    private Date toDay;
    private Map<String,ProductEntity> productEntities = new HashMap<>();
    private Map<String,SupplierEntity> supplierEntities = new HashMap<>();
    private Map<String,ExpirationEventEntity> expirationEventEntities = new HashMap<>();
    private List<NotificationBoundary> notificationBoundaryList= new ArrayList<>();
    private SharedPreferences preferences;
    private PendingIntent pendingIntent;

    public class LocalBinder  extends Binder {
        CheckForUpdatesService getService() {
            return CheckForUpdatesService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        nManager = NotificationManagerCompat.from(this) ;
        toDay = new Date(System.currentTimeMillis());
        preferences = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        final String store = preferences.getString("store", null);
        createNotificationChannel();
        Intent intent = new Intent(this, Massages.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                queryForExpiration(store);
            }
        }).start();

    }

    public void queryForExpiration(final String store){
        if (store == null) return;
        fbRef.child(store).child("notifications").orderByChild("dateOfPublish").equalTo(toDay.getTime())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()){
                            NotificationBoundary notification = data.getValue(NotificationBoundary.class);
                            notificationBoundaryList.add(notification);
                        }

                        addToEmployee(store);
                        createMaps(store);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void createMaps(String store) {
        for(NotificationBoundary n: notificationBoundaryList)
        {
            final String barcode = n.getProductBarcode();
            final String supplierId = n.getSupplierId();
            final String expirationEventId = n.getExpirationEventId();
            fbRef.child("products").child(barcode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    productEntities.put(barcode,dataSnapshot.getValue(ProductEntity.class));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            fbRef.child("suppliers").child(supplierId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    supplierEntities.put(supplierId, dataSnapshot.getValue(SupplierEntity.class));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            fbRef.child(store).child("expirationEvents").child(expirationEventId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    expirationEventEntities.put(expirationEventId, dataSnapshot.getValue(ExpirationEventEntity.class));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

    }

    private void addToEmployee(String store) {

        String id = preferences.getString("employeeId", null);
        if (id == null || store == null) return;

        for(NotificationBoundary n : notificationBoundaryList){
            String key = fbRef.child(store).child("employees").child(id).child("notifications").push().getKey();
            fbRef.child(store).child("employees").child(id).child("notifications").child(key).setValue(n);
        }

        notificationsBuildAndShow();

    }

    private void notificationsBuildAndShow() {

        NotificationEntity notification;
        for(NotificationBoundary n : notificationBoundaryList)
        {
            notification = new NotificationEntity();
            notification.setProductEntity(productEntities.get(n.getProductBarcode()));
            notification.setExpirationEventEntity(expirationEventEntities.get(n.getExpirationEventId()));
            notification.setSupplierEntity(supplierEntities.get(n.getSupplierId()));

            NotificationCompat.Builder builder = new NotificationCompat.Builder
                    (this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon
                            (
                                BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                R.mipmap.ic_launcher)
                            )
                    .setContentTitle(getString(R.string.massage_title))
                    .setContentText(notification.toString())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notification.toString()))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            nManager.notify(NotificationID.getID(), builder.build());
        }
    }

    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            try {
                notificationManager.createNotificationChannel(channel);
            } catch (NullPointerException e){
                e.printStackTrace();
            }


        }
    }
}


