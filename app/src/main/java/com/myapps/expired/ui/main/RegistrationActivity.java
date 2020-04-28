package com.myapps.expired.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapps.expired.DAL.data.EmployeeEntity;
import com.myapps.expired.DAL.data.util.EntityException;
import com.myapps.expired.DAL.data.util.EntityFactory;
import com.myapps.expired.R;
import com.myapps.expired.services.AlarmReceiver;

import java.util.Calendar;



public class RegistrationActivity extends AppCompatActivity {

    private final int HOUR = 6;
    private EditText firstName;
    private EditText lastName;
    private EditText storeName;
    private Button submitButton;
    private EmployeeEntity employeeEntity;
    private DatabaseReference fbRef = FirebaseDatabase.getInstance().getReference();
    private SharedPreferences preferences;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        firstName = findViewById(R.id.first_name_edit);
        lastName = findViewById(R.id.last_name_edit);
        storeName = findViewById(R.id.store_name_edit);
        submitButton = findViewById(R.id.submit_button_registration);
        preferences = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EntityFactory factory = new EntityFactory();
                try {
                    employeeEntity = factory.createNewEmployee(
                            storeName.getText().toString(),
                            firstName.getText().toString(),
                            lastName.getText().toString()
                    );
                }
                catch (EntityException e){
                    Toast.makeText
                            (
                                    RegistrationActivity.this,
                                    "Not All Fields Are Filled!",
                                    Toast.LENGTH_SHORT
                            ).show();
                    return;
                }
                SharedPreferences.Editor prefEditor = preferences.edit();
                prefEditor.putString("store", employeeEntity.getStore());

                String id = fbRef.child(employeeEntity.getStore()).child("employees")
                        .push().getKey();
                employeeEntity.setId(id);
                prefEditor.putString("employeeId", id);
                fbRef.child(employeeEntity.getStore()).child("employees").child(id)
                        .setValue(employeeEntity);
                prefEditor.apply();
                initializeAlarmManager();
                RegistrationActivity.this.finish();
            }
        });

    }

    private void initializeAlarmManager(){
        // Set the alarm to start at approximately 6:00
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, HOUR);

        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(this, 0, intent, 0));



    }
}
