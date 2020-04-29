package com.myapps.expired.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapps.expired.DAL.data.ExpirationEventEntity;
import com.myapps.expired.DAL.data.util.EntityException;
import com.myapps.expired.DAL.data.util.EntityFactory;
import com.myapps.expired.R;
import com.myapps.expired.services.NotificationBoundary;

public class EnterExpDatePage2 extends AppCompatActivity {

    private Date date;
    private int amount;
    private int daysBefore;
    private String store;
    private String product;

    private TextView dateView;
    private EditText amountEdit;
    private EditText daysBeforeEdit;
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
        amountEdit = findViewById(R.id.amount_edit);
        daysBeforeEdit = findViewById(R.id.days_edit);
        submitButton = findViewById(R.id.submit_button2);

        store = preferences.getString("store", null);
        product = getIntent().getExtras().getString("productBarcode");
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    setAmount(
                            Integer.parseInt(amountEdit.getText().toString())
                    );
                    setDaysBefore(
                            Integer.parseInt(daysBeforeEdit.getText().toString())
                    );
                } catch (NumberFormatException e){
                    Toast.makeText
                            (
                                    EnterExpDatePage2.this,
                                    "numbers only!",
                                    Toast.LENGTH_SHORT
                            ).show();
                    return;
                }

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
                finish();
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
        if (amount > 0)
            this.amount = amount;
        else
            Toast.makeText
                    (
                            EnterExpDatePage2.this,
                            "must be above 0",
                            Toast.LENGTH_SHORT
                    ).show();
    }

    public void setDaysBefore(int daysBefore) {
        if (daysBefore >= 1 && daysBefore <= 7)
            this.daysBefore = daysBefore;
        else
            Toast.makeText
                    (
                            EnterExpDatePage2.this,
                            "must be between 1-7",
                            Toast.LENGTH_SHORT
                    ).show();
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
