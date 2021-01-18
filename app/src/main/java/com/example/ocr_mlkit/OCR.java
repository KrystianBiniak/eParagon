package com.example.ocr_mlkit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.FileUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;

public class OCR extends AppCompatActivity {


    //OCR RECOGNIZER

    //Creating button
    private Button mTextButton;
    //private Button editTextButton;
    private Button sendTextButton;

    /*
    //Creating place to show text
    private TextView mTextView;
    private TextView mTextView2;
    private TextView mTextView3;
     */

    //Creating place to show lines and edit text
    private EditText editOCRText;
    private TextView mTextViewLines;

    //Creating string for displaying text
    private String sTextElements;
    private String sTextLines;
    private String sTextBlocks;

    //Creating string for single blocks, lines, elements
    private String sTextSingleElement;
    private String sTextSingleLine;
    private String sTextSingleBlock;

    //Creating string for lines counter
    private String sLinesCounter;

    //Creating String Array and string to send text
    private String[] textArray;
    private String text;

    //Counter
    private int counterLines = 0;
    private int counterElements = 0;

    //Received image
    private Bitmap receivedImageBitmap;
    private byte[] receivedImage;

    //ImageView
    private ImageView imageView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_c_r);

        if(!checkConnection()) {
            Toast.makeText(this, "Brak dostępu do internetu", Toast.LENGTH_SHORT).show();
        }

        //Button settings

        mTextButton = findViewById(R.id.mTextButton);
        sendTextButton = findViewById(R.id.sendTextButton);
        //editTextButton = findViewById(R.id.editTextButton);

        mTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runTextRecognition();
            }
        });

        sendTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendText();
            }
        });

        /*
        //TextView set up
        mTextView = (TextView) findViewById(R.id.mTextView);
        mTextView3 = (TextView) findViewById(R.id.mTextView3);
        mTextView2 = (TextView) findViewById(R.id.mTextView2);
         */

        //EditText set up
        editOCRText = (EditText) findViewById(R.id.mEditOCRText);

        //Lines counter set up
        mTextViewLines = (TextView) findViewById(R.id.mTextViewLines);

        sTextElements = "";
        sTextLines = "";
        sTextBlocks = "";
        sTextSingleBlock = "";
        sTextSingleElement = "";
        sTextSingleLine = "";
        sLinesCounter = "";
        text = "";


        //Receiving image
        //File receivedFile = getIntent().getByteArrayExtra("FileImage")
        //receivedImage = getIntent().getByteArrayExtra("Selected image");
        File file = new File(getApplicationContext().getCacheDir(), "imageOCR");
        int size = (int) file.length();
        receivedImage = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(receivedImage, 0, receivedImage.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //receivedImage = file.toString().getBytes();

        if(receivedImage == null) {
            receivedImageBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.testimage);
            Toast.makeText(OCR.this, "ERROR \n Test image loaded", Toast.LENGTH_SHORT).show();
        }
        else {
            receivedImageBitmap = BitmapFactory.decodeByteArray(receivedImage, 0, receivedImage.length);
        }

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(receivedImageBitmap);


    }


    //MLkit text recognizer

    private void runTextRecognition() {
        //Converting image to Bitmap and MLkit file
        InputImage image = InputImage.fromBitmap(receivedImageBitmap, 0);

        //Detector
        TextRecognizer detector = TextRecognition.getClient();


        detector.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text texts) {
                                mTextButton.setEnabled(true);
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Task failed with an exception
                                mTextButton.setEnabled(true);
                                e.printStackTrace();
                            }
                        });
    }

    private void processTextRecognitionResult(Text texts) {
        List<Text.TextBlock> blocks = texts.getTextBlocks();

        if(blocks.size() == 0) {
            Toast.makeText(this, "No text found", Toast.LENGTH_SHORT).show();
            return;
        }

        for(int i = 0; i < blocks.size(); ++i) {
            List<Text.Line> lines = blocks.get(i).getLines();


            //POSSIBLE BLOCKS RECOGNIZING
            sTextSingleBlock = blocks.get(i).getText();
            sTextBlocks = sTextBlocks + "\n Block " + i + ": " + sTextSingleBlock;


            for(int j = 0; j < lines.size(); ++j) {
                List<Text.Element> elements = lines.get(j).getElements();
                sTextSingleLine = lines.get(j).getText();
                sTextLines = sTextLines + sTextSingleLine + "\n";
                ++counterLines;
                sLinesCounter = sLinesCounter + counterLines + "\n";


                for(int k = 0; k < elements.size(); ++k) {
                    sTextSingleElement = elements.get(k).getText();

                    /*
                    POSSIBLE ELEMENTS RECOGNITION
                    if(ready == true) {
                        sTextElements = sTextElements + "\n Element " + counterElements + ": " + sTextSingleElement);
                    }
                    ++counterElements;
                    */

                }
            }
        }



        editOCRText.setText(sTextLines);
        mTextViewLines.setText(sLinesCounter);
        //mTextView.setText(sTextBlocks);
        //mTextView3.setText(sTextElements);

        //editTextButton.setVisibility(View.VISIBLE);
        sendTextButton.setVisibility(View.VISIBLE);




    }

    private void sendText() {
        text = editOCRText.getText().toString();
        textArray = text.split("\n");
        Intent intent = new Intent(this, PickAndSend.class);
        intent.putExtra("Text Array", textArray);
        startActivity(intent);
    }

    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}