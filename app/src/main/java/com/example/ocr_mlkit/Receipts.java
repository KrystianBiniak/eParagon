package com.example.ocr_mlkit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Receipts extends AppCompatActivity {

    //Period editText
    private EditText periodSince;
    private EditText periodTill;

    //String
    private String date;
    private String receiptInfo;

    //Date
    private Date sPeriodSince;
    private Date sPeriodTill;
    private Date firebaseMember;
    private SimpleDateFormat sdf;

    //Calendar
    Calendar calendar;
    private int year;
    private int month;
    private int day;

    //Layout
    //Layout
    private LayoutInflater inflater;
    private LinearLayout layoutReceiptList;

    //ImageButton
    ImageButton imageButtonConfirm;

    //Database
    private DatabaseReference ref;

    //Test counter
    private int counter = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts);

        if(!checkConnection()) {
            Toast.makeText(this, "Brak dostępu do internetu", Toast.LENGTH_SHORT).show();
        }

        //Calendar
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        //Period editText
        periodSince = findViewById(R.id.editTextStatisticsPeriodSince);
        periodTill = findViewById(R.id.editTextStatisticsPeriodTill);

        //ImageButton
        imageButtonConfirm = findViewById(R.id.imageButtonStatisticsConfirm);

        //Layout
        layoutReceiptList = findViewById(R.id.layoutReceiptList);
        inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Date
        sdf = new SimpleDateFormat("dd-MM-yyyy");
        //Set current date
        periodSince.setText(sdf.format(Calendar.getInstance().getTime()));
        periodTill.setText(sdf.format(Calendar.getInstance().getTime()));

        periodSince.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                pickDate(periodSince);
            }
        });

        periodTill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(periodTill);
            }
        });

        imageButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showReceipts();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void pickDate(final EditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(Receipts.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                if(dayOfMonth < 10 && month < 10) {
                    date = "0" + dayOfMonth + "-0" + month + "-" + year;
                }
                else if(dayOfMonth < 10 && month >= 10) {
                    date = "0" + dayOfMonth + "-" + month + "-" + year;
                }
                else if(month < 10 && dayOfMonth >= 10) {
                    date = dayOfMonth + "-0" + month + "-" + year;
                }
                else {
                    date = dayOfMonth + "-" + month + "-" + year;
                }
                editText.setText(date);
            }
        },year,month,day);
        datePickerDialog.show();
    }

    private void showReceipts() throws ParseException {

        if(!checkConnection()) {
            Toast.makeText(this, "Brak dostępu do internetu", Toast.LENGTH_SHORT).show();
        }

        //final View receiptView = inflater.inflate(R.layout.row_show_receipt, layoutReceiptList, false);

        sPeriodSince = sdf.parse(periodSince.getText().toString());
        sPeriodTill = sdf.parse(periodTill.getText().toString());

        ref = FirebaseDatabase.getInstance().getReference().child("Paragony");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                layoutReceiptList.removeAllViews();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        if(!(ds.getKey().isEmpty())) {
                            firebaseMember = sdf.parse(ds.getKey());
                            //Toast.makeText(Receipts.this, periodSince.getText().toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(Receipts.this, "Exception", Toast.LENGTH_SHORT).show();
                    }
                    //Test toast
                    //Toast.makeText(Receipts.this, firebaseMember.toString() , Toast.LENGTH_SHORT);
                    counter += 1;
                    if((firebaseMember.after(sPeriodSince) && firebaseMember.before(sPeriodTill))
                            || firebaseMember.equals(sPeriodSince)
                            || firebaseMember.equals(sPeriodTill)
                            || (firebaseMember.equals(sPeriodSince) && sPeriodSince.equals(sPeriodTill))) {
                        //Add new view
                        final String sReceiptDate = ds.getKey();
                        for(DataSnapshot ds2 : ds.getChildren()) {
                            final String sReceiptShop = ds2.getKey();
                            for (DataSnapshot ds3 : ds2.getChildren()) {
                                final View receiptView = inflater.inflate(R.layout.row_show_receipt, null, false);
                                TextView editTexttest = (TextView) receiptView.findViewById(R.id.textViewShowReceiptCategory);

                                final String sReceiptTotalSum = ds3.getKey();
                                receiptInfo = sReceiptDate + " | " + sReceiptShop + " | " + sReceiptTotalSum + " zł";
                                editTexttest.setText(receiptInfo);
                                receiptView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        seeReceipt(sReceiptDate, sReceiptShop, sReceiptTotalSum, receiptInfo);
                                    }
                                });
                                layoutReceiptList.addView(receiptView);
                            }
                        }
                        //Test toast
                        //Toast.makeText(Receipts.this, counter, Toast.LENGTH_SHORT).show();
                        //counter += 1;
                    }
                    else {
                        //Toast.makeText(Receipts.this, "Date compare error", Toast.LENGTH_SHORT).show();
                    }
                }
                if(layoutReceiptList.getChildCount() == 0) {
                    Toast.makeText(Receipts.this, "Brak paragonów w wybranym okresie", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Receipts.this, "Operation failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void seeReceipt(String date, String shop, String totalSum, String receiptInfo) {
        Intent intent = new Intent(this, ShowProductsOnReceipt.class);
        intent.putExtra("ReceiptDate", date);
        intent.putExtra("ReceiptShop", shop);
        intent.putExtra("ReceiptTotalSum", totalSum);
        intent.putExtra("Info", receiptInfo);
        startActivity(intent);
    }

    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}