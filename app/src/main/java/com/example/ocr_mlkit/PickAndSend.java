package com.example.ocr_mlkit;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PickAndSend extends AppCompatActivity {

    //Received Text Array
    private String[] textArray;
    private String[] categoryArray;

    //Spinner
    private Spinner spinnerProduct;
    private Spinner spinnerPrice;

    //Adapter
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapterCategory;
    private ArrayAdapter<String> priceAdapter;

    //Buttons
    private Button addNextProcuctButton;
    private Button send;

    //Layout
    private LayoutInflater inflater;
    private LinearLayout layoutList;

    //Date
    private EditText date;
    private SimpleDateFormat dateF;
    private String sdate;
    private String cdate;

    //Random price
    private EditText price;

    //Calendar
    Calendar calendar;
    private int year;
    private int month;
    private int day;

    //Price & product arrays
    private String[] pricesArray;
    private String[] productsArray;

    //Username
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_and_send);

        if(!checkConnection()) {
            Toast.makeText(this, "Brak dostępu do internetu", Toast.LENGTH_SHORT).show();
        }

        //Username
        username = getIntent().getStringExtra("Username");

        //Receive string text array
        //Intent intent = getIntent();
        //textArray = intent.getStringArrayExtra("Text Array");
        Intent intent = getIntent();
        int size = intent.getIntExtra("Size", 0);
        String text = "";
        textArray = new String[size];
        File fileOCRed = new File(getApplicationContext().getCacheDir(), "textOCR");
        //if(fileOCRed.exists()) Toast.makeText(PickAndSend.this, "File exists", Toast.LENGTH_SHORT).show();
        //else Toast.makeText(PickAndSend.this, "File does not exists", Toast.LENGTH_SHORT).show();

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileOCRed));
            int c;
            while ((c = br.read()) != -1){
                text = text + (char)c;
            }
            textArray = text.split("\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Toast.makeText(PickAndSend.this, text, Toast.LENGTH_SHORT).show();

        //Prices and products array
        if(textArray.length > 0) {
            pricesArray = new String[textArray.length/2];
            productsArray = new String[textArray.length/2];
        }
        else {
            Toast.makeText(PickAndSend.this, "Zły obszar zdjęcia lub edytuj tekst", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        int x = 0;
        int y = 0;

        for(int i = 0; i < textArray.length; ++i) {
            try {
                if(textArray[i].matches(".*\\d.*") && textArray[i].length() < 7) {
                    pricesArray[x] = textArray[i];
                    x++;
                }
                else {
                    productsArray[y] = textArray[i];
                    y++;
                }
            }
            catch (Exception e){
                Toast.makeText(PickAndSend.this, "Zły obszar zdjęcia lub edytuj tekst", Toast.LENGTH_LONG).show();
                this.finish();
            }

        }

        //Calendar
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        cdate = "";

        //Buttons
        addNextProcuctButton = (Button) findViewById(R.id.addNextProductButton);
        send = (Button) findViewById(R.id.button_sendToDB);

        //Layout
        layoutList = findViewById(R.id.layout_list);
        inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Categories
        categoryArray = getResources().getStringArray(R.array.categories);

        //Set current date
        date = (EditText)findViewById(R.id.editTextDate);
        dateF = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        sdate = dateF.format(Calendar.getInstance().getTime());

        date.setText(sdate);


        //Define spinner
        spinnerProduct = findViewById(R.id.spinnerProduct);
        spinnerPrice = findViewById(R.id.spinnerPrice);

        //Define adapter
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, textArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);


        addNextProcuctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addView();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToDB();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(date);
            }
        });


    }

    private void pickDate(final EditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(PickAndSend.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                if(dayOfMonth < 10 && month < 10) {
                    cdate = "0" + dayOfMonth + "-0" + month + "-" + year;
                }
                else if(dayOfMonth < 10 && month >= 10) {
                    cdate = "0" + dayOfMonth + "-" + month + "-" + year;
                }
                else if(month < 10 && dayOfMonth >= 10) {
                    cdate = dayOfMonth + "-0" + month + "-" + year;
                }
                else {
                    cdate = dayOfMonth + "-" + month + "-" + year;
                }
                editText.setText(cdate);
            }
        },year,month,day);
        datePickerDialog.show();
    }

    private void addView() {
        final View productView = inflater.inflate(R.layout.row_add_product, null, false);

        //Spinners
        Spinner spinnerProduct = (Spinner) productView.findViewById(R.id.spinnerProduct);
        Spinner spinnerPrice = (Spinner) productView.findViewById(R.id.spinnerPrice);
        Spinner spinnerCategory = (Spinner) productView.findViewById(R.id.spinnerCategory);

        //OCR'ed products adapter
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, productsArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //OCR'ed prices adapter
        priceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pricesArray);
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Categories adapter
        adapterCategory = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryArray);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if(adapter == null) {
            Toast.makeText(this, "Error, adapter is null", Toast.LENGTH_LONG).show();
        }
        else if(spinnerPrice == null || spinnerProduct == null) {
            Toast.makeText(this, "Error, spinner is null", Toast.LENGTH_SHORT).show();
        }
        else if(textArray == null || textArray.length == 0) {
            Toast.makeText(this, "Error, text array is null", Toast.LENGTH_SHORT).show();
        }
        else if(categoryArray == null || categoryArray.length == 0) {
            Toast.makeText(this, "Error, categories array is null", Toast.LENGTH_SHORT).show();
        }
        else {
            spinnerProduct.setAdapter(adapter);
            spinnerPrice.setAdapter(priceAdapter);
            spinnerCategory.setAdapter(adapterCategory);
        }

        ImageButton imageClose = (ImageButton) productView.findViewById(R.id.imageRemove);

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(productView);
            }
        });

        layoutList.addView(productView);

        //Random quantity = 1
        price = productView.findViewById(R.id.quantity);
        price.setText("1");
    }

    private void removeView(View view) {
        layoutList.removeView(view);
    }

    private void sendToDB(){

        if(!checkConnection()) {
            Toast.makeText(this, "Brak dostępu do internetu", Toast.LENGTH_SHORT).show();
        }

        //Get amount of products
        int counter = layoutList.getChildCount();

        //Spinners
        Spinner spinnerPrice;
        Spinner spinnerProduct;
        Spinner spinnerCategory;

        //Quantity EditText
        EditText textData;

        //Strings
        String price;
        String product;
        String category;
        String quantity;

        //Permission
        boolean permission = true;

        //Get date
        EditText date = (EditText) findViewById(R.id.editTextDate);
        String sDate;
        sDate = date.getText().toString();

        //Get shop
        EditText shop = (EditText) findViewById(R.id.editTextShop);
        String sShop;
        sShop = shop.getText().toString();

        //Summary
        double dSummary = 0;
        double dPrice = 0;
        double dQuantity = 0;
        String sSummary = "";
        NumberFormat numberFormat;

        //Round total summary
        DecimalFormat df = new DecimalFormat("#.00");
        df.setRoundingMode(RoundingMode.CEILING);

        //Get reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //Check shop
        if(sShop.isEmpty()) {
            Toast.makeText(this, "Podaj sklep", Toast.LENGTH_SHORT).show();
            permission = false;
        }
        if(sDate.isEmpty()) {
            Toast.makeText(this, "Podaj date", Toast.LENGTH_SHORT).show();
            permission = false;
        }

        //Get summary during first iteration - needed for Database root name
        dSummary = 0;
        for(int j = layoutList.getChildCount()-1 ; j >= 0;  --j) {
            //Get views
            View vSummarJ = layoutList.getChildAt(j);
            textData = (EditText) vSummarJ.findViewById(R.id.quantity);
            spinnerPrice = (Spinner) vSummarJ.findViewById(R.id.spinnerPrice);
            //Get data
            quantity = textData.getText().toString();
            price = spinnerPrice.getSelectedItem().toString();
            //Switch comma to dot
            price = price.replaceAll(",", ".");
            //Parse string to double
            dPrice = Double.parseDouble(price);
            dQuantity = Double.parseDouble(quantity);
            //Calculate summary
            dSummary = dSummary + ( dPrice * dQuantity );
        }
        //Double to string
        sSummary = String.format("%.2f", dSummary);
        //Replace dot with comma (Firebase does not accept dots in root names)
        sSummary = sSummary.replace(".",",");

        if(permission) {
            for(int i = layoutList.getChildCount()-1 ; i >= 0; --i) {
                View v = layoutList.getChildAt(i);
                textData = (EditText) v.findViewById(R.id.quantity);
                spinnerPrice = (Spinner) v.findViewById(R.id.spinnerPrice);
                spinnerProduct = (Spinner) v.findViewById(R.id.spinnerProduct);
                spinnerCategory = (Spinner) v.findViewById(R.id.spinnerCategory);
                quantity = textData.getText().toString();
                price = spinnerPrice.getSelectedItem().toString();
                product = spinnerProduct.getSelectedItem().toString();
                category = spinnerCategory.getSelectedItem().toString();

                //Remove dots from names
                product = product.replace(".","-");

                //Replace comma with dot
                price = price.replaceAll(",", ".");

                //Toast.makeText(this, sSummary, Toast.LENGTH_LONG).show();
                //sSummary = "333";

                // Write a message to the database - "Paragony"
                DatabaseReference myRefReceiptProductPrice = database.getReference("Użytkownicy").child(username).child("Paragony").child(sDate).child(sShop).child(sSummary).child(product).child("Cena");
                DatabaseReference myRefReceiptProductQuantity = database.getReference("Użytkownicy").child(username).child("Paragony").child(sDate).child(sShop).child(sSummary).child(product).child("Ilość");
                DatabaseReference myRefReceiptProductCategory = database.getReference("Użytkownicy").child(username).child("Paragony").child(sDate).child(sShop).child(sSummary).child(product).child("Kategoria");

                myRefReceiptProductPrice.setValue(price);
                myRefReceiptProductQuantity.setValue(quantity);
                myRefReceiptProductCategory.setValue(category);

                Toast.makeText(this, "Wysłano!", Toast.LENGTH_LONG).show();
                
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Username", username);
                intent.putExtra("Permission", true);
                startActivity(intent);
            }
        }

    }

    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}