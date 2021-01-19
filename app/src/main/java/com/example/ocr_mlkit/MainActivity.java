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
    private Button button_login;
    private Button button_reg;
    private Button button_logout;

    //Userinfo
    private TextView tvUsername;
    private String username;

    //User permission
    public boolean permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkConnection()) {
            Toast.makeText(this, "Brak dostępu do internetu", Toast.LENGTH_SHORT).show();
        }

        //Register, loggin & info
        button_login = (Button) findViewById(R.id.buttonMain_UserInfo);
        button_reg = (Button) findViewById(R.id.buttonMain_UserRegister);
        button_logout = (Button) findViewById(R.id.buttonMain_UserLogout);
        tvUsername = (TextView) findViewById(R.id.textViewMain_Username);

        //Permission, path & info
        permission = getIntent().getBooleanExtra("Permission", false);
        username = getIntent().getStringExtra("Username");
        if(permission) {
            button_reg.setVisibility(View.INVISIBLE);
            button_login.setVisibility(View.INVISIBLE);
            button_logout.setVisibility(View.VISIBLE);
            tvUsername.setText("Zalogowany jako: \n" + username);
            tvUsername.setVisibility(View.VISIBLE);
        }
        else {
            button_reg.setVisibility(View.VISIBLE);
            button_login.setVisibility(View.VISIBLE);
            button_logout.setVisibility(View.INVISIBLE);
            tvUsername.setText("username");
            tvUsername.setVisibility(View.INVISIBLE);
        }

        //Gallery Button listener

        button_selectImage = (ImageButton) findViewById(R.id.button_gallery);
        button_selectImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Opening Gallery", Toast.LENGTH_SHORT).show();
                if(permission) selectImageActivity();
                else Toast.makeText(MainActivity.this, "Zaloguj się", Toast.LENGTH_SHORT).show();
            }
        });

        //Products Button listener

        button_products = (ImageButton) findViewById(R.id.button_products);
        button_products.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Opening Products", Toast.LENGTH_SHORT).show();
                if(permission) seeProductsActivity();
                else Toast.makeText(MainActivity.this, "Zaloguj się", Toast.LENGTH_SHORT).show();
            }
        });

        //Receipts Button listener

        button_receipts = (ImageButton) findViewById(R.id.button_receipts);
        button_receipts.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Opening Receipts", Toast.LENGTH_SHORT).show();
                if(permission) seeReceiptsActivity();
                else Toast.makeText(MainActivity.this, "Zaloguj się", Toast.LENGTH_SHORT).show();
            }
        });

        //Stats Button listener

        button_stats = (ImageButton) findViewById(R.id.button_stats);
        button_stats.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Opening Stats", Toast.LENGTH_SHORT).show();
                if(permission) seeStatisticsActivity();
                else Toast.makeText(MainActivity.this, "Zaloguj się", Toast.LENGTH_SHORT).show();
            }
        });

        //Info Button listener

        button_info = (ImageButton) findViewById(R.id.button_info);
        button_info.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Opening info", Toast.LENGTH_SHORT).show();
            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivity();
            }
        });

        button_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerActivity();
            }
        });

        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra("Permission", false);
                startActivity(intent);
            }
        });
    }

    public void loginActivity() {
        Intent intent = new Intent(this, LoginToDB.class);
        startActivity(intent);
    }

    public void registerActivity() {
        Intent intent = new Intent(this, RegisterToDB.class);
        startActivity(intent);
    }

    public void selectImageActivity(){
        Intent intent = new Intent(this, EditImage.class);
        intent.putExtra("Username", username);
        startActivity(intent);
    }

    public void seeProductsActivity() {
        Intent intent = new Intent(this, Products.class);
        intent.putExtra("Username", username);
        startActivity(intent);
    }

    public void seeReceiptsActivity() {
        Intent intent = new Intent(this, Receipts.class);
        intent.putExtra("Username", username);
        startActivity(intent);
    }

    public void seeStatisticsActivity() {
        Intent intent = new Intent(this, Statistics.class);
        intent.putExtra("Username", username);
        startActivity(intent);
    }

    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
