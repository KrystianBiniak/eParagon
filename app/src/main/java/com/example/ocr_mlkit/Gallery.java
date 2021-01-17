package com.example.ocr_mlkit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Gallery extends AppCompatActivity {

    //Gallery
    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        InputStream inputStream = null;
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if(data == null) {
                //Display an error
                Toast.makeText(Gallery.this, "Error", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Toast.makeText(Gallery.this, "Picture selected", Toast.LENGTH_SHORT).show();
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();
            openScanActivity(bitmapdata);
        }
    }

    public void openScanActivity(byte[] bitmapdata){
        Intent intent = new Intent(this, EditImage.class);
        intent.putExtra("Selected image", bitmapdata);
        startActivity(intent);
    }
}