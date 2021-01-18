package com.example.ocr_mlkit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditImage extends AppCompatActivity {

    //View for image
    private ImageView imageEditView;

    //Received image
    private BitmapDrawable receivedImageBitmapDrawable;
    private Bitmap receivedImageBitmap;
    Uri uri;

    //Buttons
    private ImageButton button_refresh;
    private ImageButton button_browse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        if(!checkConnection()) {
            Toast.makeText(this, "Brak dostępu do internetu", Toast.LENGTH_SHORT).show();
        }

        //Buttons
        button_browse = findViewById(R.id.button_browse);
        button_refresh = findViewById(R.id.button_reset);

        //ImageView
        imageEditView = findViewById(R.id.imageEditView);

        //Listeners
        button_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChooseFile();
            }
        });

        //Image
        receivedImageBitmapDrawable = null;
        receivedImageBitmap = null;

        button_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //imageEditView.setImageBitmap(null);
                if(receivedImageBitmap == null) {
                    Toast.makeText(EditImage.this, "Brak zdjęcia", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        sendImageActivity(receivedImageBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Get and set image
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        uri = result.getUri();
        imageEditView.setImageURI(uri);

        //Retrieve bitmap
        receivedImageBitmapDrawable = (BitmapDrawable) imageEditView.getDrawable();
        receivedImageBitmap = receivedImageBitmapDrawable.getBitmap();
    }


    public void onChooseFile() {
        CropImage.activity()
                .setMultiTouchEnabled(true)
                .start(EditImage.this);
    }

    public void sendImageActivity(Bitmap bitmap) throws IOException {
        //ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        //byte[] bitmapdata = stream.toByteArray();

        //Create file
        File file = new File(getApplicationContext().getCacheDir(), "imageOCR");

        //Bitmap to byte array
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        byte[] bitmapdata = outStream.toByteArray();

        //Write bytes to file
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bitmapdata);
        fileOutputStream.flush();
        fileOutputStream.close();


        Intent intent = new Intent(this, OCR.class);
        //intent.putExtra("Selected image", bitmapdata);
        intent.putExtra("FileImage", file);
        startActivity(intent);
    }

    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
