package com.example.ocr_mlkit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class Camera extends AppCompatActivity {

    private ImageView cameraView;
    private Button cameraButton;
    private Button sendButton;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraView = findViewById(R.id.cameraView);
        cameraButton = findViewById(R.id.cameraButton);
        sendButton = findViewById(R.id.sendButton);

        //Permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.CAMERA
                    },100);
        }

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            //Get image
            bitmap = (Bitmap) data.getExtras().get("data");
            //Set image
            cameraView.setImageBitmap(bitmap);

            //Set button
            sendButton.setVisibility(View.VISIBLE);
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Send photo to OCR
                    Toast.makeText(Camera.this, "Photo send", Toast.LENGTH_SHORT).show();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bitmapdata = stream.toByteArray();
                    sendImageActivity(bitmapdata);
                }
            });
        }
    }

    public void sendImageActivity(byte[] bitmapdata){
        Intent intent = new Intent(this, OCR.class);
        intent.putExtra("Selected image", bitmapdata);
        startActivity(intent);
    }



}