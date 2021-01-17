package com.example.ocr_mlkit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class ShowProductsOnReceipt extends AppCompatActivity {

    //TextView
    private TextView textView;
    private TextView infoTV;

    //Database
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

    //Receive data Strings
    private String date;
    private String shop;
    private String totalSum;
    private String receiptInfo;

    //Show data from database string
    //private String dataFromDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_products_on_receipt);

        textView = findViewById(R.id.textViewSingleReceiptProducts);
        infoTV = findViewById(R.id.textViewSingleReceiptInfo);

        Intent intent = getIntent();
        date = intent.getStringExtra("ReceiptDate");
        shop = intent.getStringExtra("ReceiptShop");
        totalSum = intent.getStringExtra("ReceiptTotalSum");
        receiptInfo = intent.getStringExtra("Info");

        infoTV.setText(receiptInfo);

        test();

    }

    private void test() {
        ref = database.getReference().child("Paragony").child(date).child(shop).child(totalSum);

        ref.addValueEventListener(new ValueEventListener() {
            String dataFromDB = "";
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    dataFromDB = dataFromDB + "Produkt: " + ds.getKey() + "\n";
                    for(DataSnapshot ds2 : ds.getChildren()) {
                        dataFromDB = dataFromDB + ds2.getKey() + ": " + ds2.getValue().toString() + "\n";
                    }
                    dataFromDB = dataFromDB + "\n\n";
                }
               textView.setText(dataFromDB);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowProductsOnReceipt.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });


    }
}