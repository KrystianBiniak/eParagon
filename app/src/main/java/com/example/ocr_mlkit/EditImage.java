package com.example.ocr_mlkit;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
                    sendImageActivity(receivedImageBitmap);
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

    public void sendImageActivity(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();

        Intent intent = new Intent(this, OCR.class);
        intent.putExtra("Selected image", bitmapdata);
        startActivity(intent);
    }

}
