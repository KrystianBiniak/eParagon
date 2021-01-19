package com.example.ocr_mlkit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class Statistics extends AppCompatActivity {

    //Permission for access to charts
    private boolean permission;

    //ImageButtons for charts
    private ImageButton imageButtonChartExpenses;
    private ImageButton imageButtonChartQuantity;

    //ImageButton for getting data from DB
    private ImageButton imageButtonConfirm;

    //EditText & String for date
    private EditText dateSince;
    private EditText dateTill;
    private SimpleDateFormat sdf;
    private String date;

    //Date format
    private Date sPeriodSince;
    private Date sPeriodTill;
    private Date firebaseMember;

    //Calendar
    Calendar calendar;
    private int year;
    private int month;
    private int day;

    //Database
    private DatabaseReference ref;

    //Data collecting
    private double totalSum;
    private String totalSumSingle;
    private double quantity;
    private double price;
    private String sPrice;
    private String sQuantity;
    private double totalPrice;
    private double totalQuantity;
    private String cateogoryFromArray;
    private double totalPriceSingle;
    private double[] categoriesTotalSum;
    private double[] categoriesTotalQuantity;

    //TextView for data show
    private TextView textViewTotalSum;
    private TextView textViewCategorySum;

    //Category spinner & adapter
    private Spinner categorySpinner;
    private String categoryString;
    private ArrayAdapter<String> adapterCategory;
    private String[] categories;

    //Get indexes for expenses chart
    private int indexOfMax;
    private int j;
    private int[] arrayIndexMax;
    private double[] tempCategoriesTotalSum;
    private double[] tempCategoriesTotalQuantity;

    //Data which is send to expenses chart activity
    private String[] categoriesToChartByExpenses;
    private float[] expensesToChartByExpenses;

    //Data which is send to expenses chart activity
    private String[] categoriesToChartByQuantity;
    private float[] quantityToChartByQuantity;

    //Username
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        if(!checkConnection()) {
            Toast.makeText(this, "Brak dostępu do internetu", Toast.LENGTH_SHORT).show();
        }

        //Username
        username = getIntent().getStringExtra("Username");

        //Permission
        permission = false;

        //Category spinner
        categorySpinner = findViewById(R.id.spinnerStatisticsPickCategory);
        categoryString = "Random category";

        //Categories adapter
        categories = getResources().getStringArray(R.array.categories);
        adapterCategory = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Set up spinner
        categorySpinner.setAdapter(adapterCategory);

        //Data collecting
        totalSum = 0;
        quantity = 0;
        price = 0;
        totalPrice = 0;
        totalQuantity = 0;
        cateogoryFromArray= "";
        categoriesTotalSum = new double[11];
        categoriesTotalQuantity = new double[11];

        //Data show
        textViewTotalSum = findViewById(R.id.textViewStatisticsSumTotalShow);
        textViewCategorySum = findViewById(R.id.textViewStatisticsShowSumCategory);

        //Calendar
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        //Date
        sdf = new SimpleDateFormat("dd-MM-yyyy");
        dateSince = findViewById(R.id.editTextStatisticsPeriodSince);
        dateTill = findViewById(R.id.editTextStatisticsPeriodTill);

        dateSince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(dateSince);
            }
        });

        dateTill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(dateTill);
            }
        });

        //Set current date
        dateSince.setText(sdf.format(Calendar.getInstance().getTime()));
        dateTill.setText(sdf.format(Calendar.getInstance().getTime()));

        //ImageButtons
        imageButtonChartExpenses = findViewById(R.id.imageButtonStatisticsByExpenses);
        imageButtonChartQuantity = findViewById(R.id.imageButtonStatisticsByQuantity);
        imageButtonConfirm = findViewById(R.id.imageButtonStatisticsConfirm);

        imageButtonChartExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(permission) {
                    showChartExpenses();
                }
                else  {
                    Toast.makeText(Statistics.this, "Zatwierdź najpierw datę", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageButtonChartQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(permission) {
                    showChartQuantity();
                }
                else  {
                    Toast.makeText(Statistics.this, "Zatwierdź najpierw datę", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getData();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void showChartExpenses() {

        categoriesToChartByExpenses = new String[5];
        expensesToChartByExpenses = new float[5];
        indexOfMax = 0;
        j = 0;
        arrayIndexMax = new int[5];
        tempCategoriesTotalSum = new double[categoriesTotalSum.length];
        System.arraycopy(categoriesTotalSum,0, tempCategoriesTotalSum, 0, categoriesTotalSum.length);

        for(int i = 1; i < tempCategoriesTotalSum.length; ++i) {
            if( tempCategoriesTotalSum[i] > tempCategoriesTotalSum[indexOfMax]) {
                indexOfMax = i;
            }
            if(i == tempCategoriesTotalSum.length - 1 && j < 5) {
                tempCategoriesTotalSum[indexOfMax] = 0;
                arrayIndexMax[j] = indexOfMax;
                ++j;
                i = 1;
            }
        }

        for(int i = 0; i < arrayIndexMax.length; ++i ) {
            categoriesToChartByExpenses[i] = categories[arrayIndexMax[i]];
            expensesToChartByExpenses[i] = (float) categoriesTotalSum[arrayIndexMax[i]];
        }
        //Toast.makeText(Statistics.this, Arrays.toString(arrayIndexMax), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ChartExpenses.class);
        intent.putExtra("Categories", categoriesToChartByExpenses);
        intent.putExtra("Expenses", expensesToChartByExpenses);
        intent.putExtra("Period",
                "Okres: "
                + dateSince.getText().toString().replaceAll("-",".")
                + " - "
                + dateTill.getText().toString().replaceAll("-","."));
        startActivity(intent);
    }

    private void showChartQuantity() {
        categoriesToChartByQuantity = new String[5];
        quantityToChartByQuantity = new float[5];
        indexOfMax = 0;
        j = 0;
        arrayIndexMax = new int[5];
        tempCategoriesTotalQuantity = new double[categoriesTotalQuantity.length];
        System.arraycopy(categoriesTotalQuantity,0, tempCategoriesTotalQuantity, 0, categoriesTotalQuantity.length);

        for(int i = 1; i < tempCategoriesTotalQuantity.length; ++i) {
            if( tempCategoriesTotalQuantity[i] > tempCategoriesTotalQuantity[indexOfMax]) {
                indexOfMax = i;
            }
            if(i == tempCategoriesTotalQuantity.length - 1 && j < 5) {
                tempCategoriesTotalQuantity[indexOfMax] = 0;
                arrayIndexMax[j] = indexOfMax;
                ++j;
                i = 1;
            }
        }

        for(int i = 0; i < arrayIndexMax.length; ++i ) {
            categoriesToChartByQuantity[i] = categories[arrayIndexMax[i]];
            quantityToChartByQuantity[i] = (float) categoriesTotalQuantity[arrayIndexMax[i]];
        }
        //Toast.makeText(Statistics.this, Arrays.toString(arrayIndexMax), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ChartQuantity.class);
        intent.putExtra("Categories", categoriesToChartByQuantity);
        intent.putExtra("Quantity", quantityToChartByQuantity);
        intent.putExtra("Period",
                "Okres: "
                        + dateSince.getText().toString().replaceAll("-",".")
                        + " - "
                        + dateTill.getText().toString().replaceAll("-","."));
        startActivity(intent);
    }

    private void pickDate(final EditText dateSelector) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(Statistics.this, new DatePickerDialog.OnDateSetListener() {
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
                dateSelector.setText(date);
            }
        },year,month,day);
        datePickerDialog.show();
    }

    private void getData() throws ParseException {

        if(!checkConnection()) {
            Toast.makeText(this, "Brak dostępu do internetu", Toast.LENGTH_SHORT).show();
        }

        //Set access to charts
        permission = true;

        //Set start values
        Arrays.fill(categoriesTotalSum, 0);
        Arrays.fill(categoriesTotalQuantity, 0);
        totalSum = 0;
        quantity = 0;
        price = 0;
        totalPrice = 0;
        totalQuantity = 0;
        totalPriceSingle = 0;

        sPeriodSince = sdf.parse(dateSince.getText().toString());
        sPeriodTill = sdf.parse(dateTill.getText().toString());

        categoryString = categorySpinner.getSelectedItem().toString();

        ref = FirebaseDatabase.getInstance().getReference().child("Użytkownicy").child(username).child("Paragony");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        if (!(ds.getKey().isEmpty())) {
                            firebaseMember = sdf.parse(ds.getKey());
                            //Toast.makeText(Receipts.this, periodSince.getText().toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(Statistics.this, "Exception", Toast.LENGTH_SHORT).show();
                    }
                    if((firebaseMember.after(sPeriodSince) && firebaseMember.before(sPeriodTill))
                            || firebaseMember.equals(sPeriodSince)
                            || firebaseMember.equals(sPeriodTill)
                            || (firebaseMember.equals(sPeriodSince) && sPeriodSince.equals(sPeriodTill))) {
                        for(DataSnapshot ds2: ds.getChildren()) {
                            for(DataSnapshot ds3: ds2.getChildren()) {
                                totalSumSingle = ds3.getKey();
                                totalSumSingle = totalSumSingle.replace(",",".");
                                totalSum = totalSum + Double.parseDouble(totalSumSingle);
                                for(DataSnapshot ds4 : ds3.getChildren()) {
                                    for(int i = 0; i < categories.length; ++i) {
                                        //Sum up category total
                                        if(ds4.child("Kategoria").getValue().toString().equals(categories[i])) {
                                            sQuantity = ds4.child("Ilość").getValue().toString();
                                            quantity = Double.parseDouble(sQuantity);

                                            sPrice = ds4.child("Cena").getValue().toString();
                                            price = Double.parseDouble(sPrice);

                                            totalPrice =  (price * quantity);
                                            categoriesTotalSum[i] = categoriesTotalSum[i] + totalPrice;
                                            categoriesTotalQuantity[i] = categoriesTotalQuantity[i] + quantity;
                                            if(categories[i].equals(categoryString)) {
                                                totalPriceSingle = totalPriceSingle + totalPrice;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                textViewTotalSum.setText(String.format("%.2f", totalSum) + " zł");
                textViewCategorySum.setText(String.format("%.2f", totalPriceSingle) + " zł");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Statistics.this, "Date compare error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}