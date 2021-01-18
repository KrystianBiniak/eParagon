package com.example.ocr_mlkit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class Products extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

    //Test
    private Button button;
    private TextView textViewProducts;

    //Spinner
    private Spinner spinnerProduct;

    //Adapter
    private ArrayAdapter adapterCategory;

    //String array
    private String[] categoryArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        if(!checkConnection()) {
            Toast.makeText(this, "Brak dostępu do internetu", Toast.LENGTH_SHORT).show();
        }

        textViewProducts = findViewById(R.id.textViewTest);
        button = findViewById(R.id.buttonTest);

        //Spinner
        spinnerProduct = findViewById(R.id.spinnerProductShow);

        //Get categories
        categoryArray = getResources().getStringArray(R.array.categories);

        //Categories adapter
        adapterCategory = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryArray);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Set spinner
        spinnerProduct.setAdapter(adapterCategory);

        textViewProducts.setMovementMethod(new ScrollingMovementMethod());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Products.this, "Pobieranie", Toast.LENGTH_SHORT).show();
                test();
            }
        });
    }

    private void test() {

        if(!checkConnection()) {
            Toast.makeText(this, "Brak dostępu do internetu", Toast.LENGTH_SHORT).show();
        }

        final String selectedCategory = spinnerProduct.getSelectedItem().toString();
        ref = database.getReference().child("Paragony");
        final ArrayList<String> list = new ArrayList<String>();
        final ArrayList<String> listShow = new ArrayList<String>();
        String s1 = "List";
        list.add(s1);

        //textViewProducts.setText(ref.getKey());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String key = "";
                String listItem = "";
                boolean permission = true;
                for(DataSnapshot ds : snapshot.getChildren()) {
                    for(DataSnapshot ds2 : ds.getChildren()) {
                        for(DataSnapshot ds3 : ds2.getChildren()) {
                            //Toast.makeText(Products.this, ds3.getKey(), Toast.LENGTH_SHORT).show();
                            for(DataSnapshot ds4 : ds3.getChildren()) {
                                if(ds4.child("Kategoria").getValue().equals(selectedCategory)) {
                                    //Check if product is already on list
                                    for(int i = 0; i < list.size() ; ++i) {
                                        key = ds4.getKey().toString();
                                        listItem = list.get(i).toString();
                                        if( key.equals(listItem) ){
                                            permission = false;
                                        }
                                    }
                                    if(permission == true) {
                                        //Toast.makeText(Products.this, "|" + listItem + "|", Toast.LENGTH_SHORT).show();
                                        listShow.add( ds4.getKey() + "\nCena: " + ds4.child("Cena").getValue().toString() + "\nSklep: " + ds2.getKey() + "\n\n" );
                                        list.add(ds4.getKey());
                                    }
                                    permission = true;
                                }
                            }
                        }
                    }
                }
                int length = listShow.size();
                //String sLength = String.valueOf(length);
                if(length > 0) {
                    StringBuilder sb = new StringBuilder();

                    for(String s: listShow) {
                        sb.append(s);
                        sb.append("\n");
                    }

                    textViewProducts.setText(sb.toString());
                }
                else {
                    textViewProducts.setText("Brak produktów w wybranej kategorii");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        });
    }

    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}