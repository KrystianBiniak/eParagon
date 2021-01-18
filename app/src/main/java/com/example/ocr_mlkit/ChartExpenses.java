package com.example.ocr_mlkit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;

public class ChartExpenses extends AppCompatActivity {

    //Colors
    private int[] colors;

    //Bar chart
    private BarChart barChart;
    private BarDataSet barDataSet;
    private BarData barData;

    //Pie chart
    private PieChart pieChart;
    private PieDataSet pieDataSet;
    private PieData pieData;

    //ArrayList for bar chart
    ArrayList<BarEntry> visitorsBarChart;

    //ArrayList for pie chart
    ArrayList<PieEntry> visitorsPieChart;

    //Receiveed data
    private String[] categories;
    private float[] expenses;
    private String period;

    //Period
    private TextView periodTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_expenses);

        if(!checkConnection()) {
            Toast.makeText(this, "Brak dostępu do internetu", Toast.LENGTH_SHORT).show();
        }

        categories = getIntent().getStringArrayExtra("Categories");
        expenses = getIntent().getFloatArrayExtra("Expenses");
        period = getIntent().getStringExtra("Period");

        //Set period textview
        periodTV = findViewById(R.id.textViewChartEShowPeriod);
        periodTV.setText(period);


        //Colors
        colors = new int[5];
        colors[0] = Color.parseColor("#b3c6ff");
        colors[1] = Color.parseColor("#668cff");
        colors[2] = Color.parseColor("#1a53ff");
        colors[3] = Color.parseColor("#0033cc");
        colors[4] = Color.parseColor("#001a66");

        //Charts
        barChart = findViewById(R.id.BarChartExpenses);
        pieChart = findViewById(R.id.PieChartExpenses);

        //Array for bar chart
        visitorsBarChart = new ArrayList<>();

        visitorsBarChart.add(new BarEntry(1, expenses[0]));
        visitorsBarChart.add(new BarEntry(2, expenses[1]));
        visitorsBarChart.add(new BarEntry(3, expenses[2]));
        visitorsBarChart.add(new BarEntry(4, expenses[3]));
        visitorsBarChart.add(new BarEntry(5, expenses[4]));

        //Array for pie chart
        visitorsPieChart = new ArrayList<>();

        visitorsPieChart.add(new PieEntry(expenses[0], categories[0]));
        visitorsPieChart.add(new PieEntry(expenses[1], categories[1]));
        visitorsPieChart.add(new PieEntry(expenses[2], categories[2]));
        visitorsPieChart.add(new PieEntry(expenses[3], categories[3]));
        visitorsPieChart.add(new PieEntry(expenses[4], categories[4]));


        //Bar chart set up
        barDataSet = new BarDataSet(visitorsBarChart, "Kwota [zł]");
        barDataSet.setColors(ColorTemplate.createColors(colors));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        //barChart.getDescription().setText("Bar Chart Example");
        barChart.animateY(1500);

        //Pie chart set up
        pieDataSet = new PieDataSet(visitorsPieChart, "Kwota [zł]");
        pieDataSet.setColors(ColorTemplate.createColors(colors));
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(16f);

        pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Kwota [zł]");
        pieChart.animate();
    }

    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}