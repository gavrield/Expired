package com.myapps.expired.ui.main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.util.Map;
import java.util.Set;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapps.expired.DAL.data.ExpirationEventEntity;
import com.myapps.expired.DAL.data.ProductEntity;
import com.myapps.expired.DAL.data.util.EntityException;
import com.myapps.expired.DAL.data.util.EntityFactory;
import com.myapps.expired.R;
import com.myapps.expired.services.NotificationBoundary;

public class EnterExpDatePage2 extends AppCompatActivity {

    public static final int MINVALUE = 0;
    public static final int MAX_PRODUCTS = 1000;
    public static final int MAX_DAYS = 14;
    private Date date;
    private int amount;
    private int daysBefore;
    private String store;
    private String product;

    private TextView dateView;
    private TextView amountView;
    private TextView daysBeforeView;
    private Button amountButton;
    private Button daysBeforeButton;
    private Button submitButton;

    private EntityFactory entityFactory = new EntityFactory();
    private ExpirationEventEntity expirationEventEntity;
    private DatabaseReference fbRef = FirebaseDatabase.getInstance().getReference();
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        setContentView(R.layout.activity_enter_exp_date_page2);
        dateView = findViewById(R.id.date_view);
        amountView = findViewById(R.id.amount_view);
        daysBeforeView = findViewById(R.id.days_view);
        amountButton = findViewById(R.id.amount_button);
        daysBeforeButton = findViewById(R.id.days_button);
        submitButton = findViewById(R.id.submit_button2);

        store = preferences.getString("store", null);
        product = getIntent().getExtras().getString("productBarcode");

        amountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPickerFragment numberPickerFragment = new NumberPickerFragment(NumberPickerFragment.AMOUNT);
                numberPickerFragment.setPickerValues(MINVALUE, MAX_PRODUCTS);
                numberPickerFragment.show(getSupportFragmentManager(), "AmountPicker");
            }
        });
        daysBeforeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPickerFragment numberPickerFragment =
                        new NumberPickerFragment(NumberPickerFragment.DAYS);
                numberPickerFragment.setPickerValues(MINVALUE, MAX_DAYS);
                numberPickerFragment.show(getSupportFragmentManager(), "DaysBeforePicker");
            }
        });
        final Context context = this;
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    expirationEventEntity = entityFactory
                            .createNewExpirationEvent(store, product, amount, date, daysBefore);
                    uploadNotification();
                }
                catch (EntityException e){
                    Toast.makeText
                            (
                                    EnterExpDatePage2.this,
                                    "Not All Fields Are Filled!",
                                    Toast.LENGTH_SHORT
                            ).show();
                    return;
                }

                String key = fbRef.child(store).child("expirationEvents").push().getKey();
                fbRef
                        .child(store)
                        .child("expirationEvents")
                        .child(key)
                        .setValue(expirationEventEntity);
                Intent nextPage = new Intent(context, MenuActivity.class);
                startActivity(nextPage);
            }
        });


    }
    public void showDatePickerDialog(View v) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void setDate(Date date) {
        this.date = date;
        if (date != null)
            dateView.setText(date.toString());
    }

    public void setAmount(int amount) {
        this.amount = amount;
        amountView.setText(amount);
    }

    public void setDaysBefore(int daysBefore) {
        this.daysBefore = daysBefore;
        daysBeforeView.setText(daysBefore);
    }

    private void uploadNotification(){
        long timeInMillis = expirationEventEntity.getExpirationDate().getTime() -
                expirationEventEntity.getWhenToNotify()* DateUtils.DAY_IN_MILLIS;
        String supplierId = getIntent().getExtras().getString("supplierId");
        NotificationBoundary notificationBoundary =
                new NotificationBoundary(
                        expirationEventEntity.getProductBarcode(),
                        supplierId,
                        expirationEventEntity.getId(),
                        new Date(timeInMillis)
                        );
        String key = fbRef
                .child(expirationEventEntity.getStore())
                .child("notifications").push().getKey();
        fbRef
                .child(expirationEventEntity.getStore())
                .child("notifications")
                .child(key).setValue(notificationBoundary);
    }
}
