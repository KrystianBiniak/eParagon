package com.example.ocr_mlkit;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class Info extends AppCompatActivity {

    //Buttons
    private Button info;
    private Button infoImage;
    private Button infoText;

    //ImageView
    private ImageView imageViewInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //ImageView
        imageViewInfo = findViewById(R.id.imageViewInfo);

        //Buttons
        info = findViewById(R.id.buttonInfoInfo);
        infoImage = findViewById(R.id.buttonInfoEditPhoto);
        infoText = findViewById(R.id.buttonInfoEditText);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInfoInfo();
            }
        });

        infoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInfoImage();
            }
        });

        infoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInfoText();
            }
        });
    }

    public void setInfoInfo() {
        imageViewInfo.setImageResource(R.drawable.info_info);
    }

    public void setInfoImage() {
        imageViewInfo.setImageResource(R.drawable.example_edit_photo);
    }

    public void setInfoText() {
        imageViewInfo.setImageResource(R.drawable.example_edit_text);
    }

}