package com.example.ocr_mlkit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Buttons of main activity
    private ImageButton button_selectImage;
    private ImageButton button_products;
    private ImageButton button_receipts;
    private ImageButton button_stats;
    private ImageButton button_info;

    DatabaseReference reff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkConnection()) {
            Toast.makeText(this, "Brak dostępu do internetu", Toast.LENGTH_SHORT).show();
        }

        //Gallery Button listener

        button_selectImage = (ImageButton) findViewById(R.id.button_gallery);
        button_selectImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Opening Gallery", Toast.LENGTH_SHORT).show();
                selectImageActivity();
            }
        });

        //Products Button listener

        button_products = (ImageButton) findViewById(R.id.button_products);
        button_products.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Opening Products", Toast.LENGTH_SHORT).show();
                seeProductsActivity();
            }
        });

        //Receipts Button listener

        button_receipts = (ImageButton) findViewById(R.id.button_receipts);
        button_receipts.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Opening Receipts", Toast.LENGTH_SHORT).show();
                seeReceiptsActivity();
            }
        });

        //Stats Button listener

        button_stats = (ImageButton) findViewById(R.id.button_stats);
        button_stats.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Opening Stats", Toast.LENGTH_SHORT).show();
                seeStatisticsActivity();
            }
        });

        //Info Button listener

        button_info = (ImageButton) findViewById(R.id.button_info);
        button_info.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Opening info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void selectImageActivity(){
        Intent intent = new Intent(this, EditImage.class);
        startActivity(intent);
    }

    public void seeProductsActivity() {
        Intent intent = new Intent(this, Products.class);
        startActivity(intent);
    }

    public void seeReceiptsActivity() {
        Intent intent = new Intent(this, Receipts.class);
        startActivity(intent);
    }

    public void seeStatisticsActivity() {
        Intent intent = new Intent(this, Statistics.class);
        startActivity(intent);
    }

    /*public void test() {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String data = "02-02-2020";
        String product = "Kapusta 1kg";
        DatabaseReference myRefPrice = database.getReference("Paragony").child(data).child(product).child("Cena");
        DatabaseReference myRefQuantity = database.getReference("Paragony").child(data).child(product).child("Ilość");

        myRefPrice.setValue("3,99");
        myRefQuantity.setValue("3");
    }

     */

    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
