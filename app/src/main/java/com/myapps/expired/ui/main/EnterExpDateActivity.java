package com.myapps.expired.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myapps.expired.DAL.data.HowToStore;
import com.myapps.expired.DAL.data.ProductEntity;
import com.myapps.expired.DAL.data.util.EntityException;
import com.myapps.expired.DAL.data.util.EntityFactory;
import com.myapps.expired.R;

public class EnterExpDateActivity extends AppCompatActivity {


    private static final int CAMERA_PERMISSION_CODE = 100;
    private DatabaseReference fbRef;
    private Button searchButton;
    private Button scanBarcodeButton;
    private EditText manualBarcode;
    private LinearLayout productDefPage;
    private ProductEntity productEntity;
    private ValueEventListener productValListener = new ValueEventListener(){

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            productEntity = null;
            if(dataSnapshot.getValue() != null) {
                productEntity = dataSnapshot.getValue(ProductEntity.class);
                if (productEntity != null)
                {
                    editBarcode.setText(productEntity.getBarcode());
                    editDescription.setText(productEntity.getDescription());
                    editSupplier.setText(productEntity.getSupplier());
                    switch (productEntity.getHowToStore()){
                        case FREEZE:
                            radioButton = findViewById(R.id.freeze);
                            break;
                        case REFRIGERATE:
                            radioButton = findViewById(R.id.refrigerator);
                            break;
                        case ROOM_TEMPERATURE:
                            radioButton = findViewById(R.id.room_temp);
                            break;
                    }
                    radioButton.setActivated(true);
                    editSupplier.setText(productEntity.getSupplier());
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private EditText editBarcode;
    private EditText editDescription;
    private EditText editSupplier;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private EntityFactory entityFactory = new EntityFactory();
    private Button submitButton;
    private Intent cameraIntent;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE);
        if (preferences.getString("store", null) == null)
        {
            Toast.makeText(this,
                    getText(R.string.not_registered_massage)
                    , Toast.LENGTH_SHORT ).show();
            finish();
        }
        setContentView(R.layout.activity_enter_exp_date);
        FirebaseApp.initializeApp(this);
        fbRef = FirebaseDatabase.getInstance().getReference();
        searchButton = findViewById(R.id.search_product_button);
        scanBarcodeButton = findViewById(R.id.scan_button);
        manualBarcode = findViewById(R.id.barcode_read);
        manualBarcode.setActivated(true);
        productDefPage = findViewById(R.id.product_def);
        editBarcode = findViewById(R.id.barcode_edit);
        editDescription = findViewById(R.id.description_edit);
        editSupplier = findViewById(R.id.supplier_edit);
        submitButton = findViewById(R.id.submit_button);
        radioGroup = findViewById(R.id.how_to_store);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String barcode = String.valueOf(manualBarcode.getText());
                if (barcode.equals(""))
                    Toast.makeText
                            (
                                    EnterExpDateActivity.this,
                                    "Not Filled!",
                                    Toast.LENGTH_SHORT
                            ).show();
                else {
                    Toast.makeText
                            (
                                    EnterExpDateActivity.this,
                                    barcode,
                                    Toast.LENGTH_SHORT
                            ).show();
                    editBarcode.setText(barcode);
                    fbRef.child("products").child(barcode).addListenerForSingleValueEvent(productValListener);
                    productDefPage.setVisibility(View.VISIBLE);
                }

            }
        });

        final Context context = this;
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int buttonId = radioGroup.getCheckedRadioButtonId();
                HowToStore howToStore;
                switch (buttonId){
                    case R.id.freeze:
                        howToStore = HowToStore.FREEZE;
                        break;
                    case R.id.refrigerator:
                        howToStore = HowToStore.REFRIGERATE;
                        break;
                        default:
                            howToStore = HowToStore.ROOM_TEMPERATURE;
                            break;
                }
                boolean flag = true;
                try {
                    productEntity = entityFactory.createNewProductEntity
                            (
                                    editBarcode.getText().toString(),
                                    editDescription.getText().toString(),
                                    editSupplier.getText().toString(),
                                    howToStore
                            );
                } catch (EntityException e) {
                    Toast.makeText
                            (
                                    EnterExpDateActivity.this,
                                    "Not All Fields Are Filled!",
                                    Toast.LENGTH_SHORT
                            ).show();
                    return;
                }

                fbRef.child("products")
                        .child(productEntity.getBarcode())
                        .setValue(productEntity);
                fbRef.child("suppliers")
                        .child(productEntity.getSupplier())
                        .child("products")
                        .child(productEntity.getBarcode())
                        .setValue(true);
                Intent nextPage = new Intent(context, EnterExpDatePage2.class);
                nextPage.putExtra("productBarcode", productEntity.getBarcode());
                nextPage.putExtra("supplierId", productEntity.getSupplier());
                startActivity(nextPage);
            }
        });
        cameraIntent = new Intent(this, Scan.class);
        scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode, data);
        // Check which request we're responding to
        if (requestCode == Intent.FILL_IN_ACTION)
            // Make sure the request was successful
            if (resultCode == RESULT_OK){
                String result = data.getStringExtra("barcode");
                if(result != null)
                    manualBarcode.setText(result);
            }
    }

    // Function to check and request permission
    public void checkPermission(String permission, int requestCode)
    {

        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(
                this,
                permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(
                            this,
                            new String[] { permission },
                            requestCode);
        }
        else {
            Toast
                    .makeText(this,
                            "Permission already granted",
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }


// This function is called when user accept or decline the permission.
// Request Code is used to check which permission called this function.
// This request code is provided when user is prompt for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Toast.makeText(this,
                        "Camera Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
                startActivityForResult(cameraIntent, Intent.FILL_IN_ACTION);
            } else {
                Toast.makeText(this,
                        "Camera Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
