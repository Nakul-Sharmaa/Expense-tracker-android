package com.example.expensetracker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.util.ArrayList;

public class BarChartActivity extends AppCompatActivity {

    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);

        barChart = findViewById(R.id.barChart);

        double salary = getIntent().getDoubleExtra("salary", 0);
        double totalExpense = getIntent().getDoubleExtra("total_expense", 0);

        // Calculate savings
        double savings = salary - totalExpense;

        // Set the bar chart data
        setBarChartData(savings, totalExpense);
    }

    private void setBarChartData(double savings, double totalExpense) {
        // Create data entries for the bar chart
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, (float) savings)); // Savings
        entries.add(new BarEntry(1f, (float) totalExpense)); // Total Expenses

        // Set up the dataset
        BarDataSet barDataSet = new BarDataSet(entries, "Savings vs Expenses");
        barDataSet.setColors(getResources().getColor(R.color.black), getResources().getColor(R.color.white));

        // Set up the BarData
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.invalidate(); // Refresh the chart

        // Add labels to the bars
        String[] labels = {"Savings", "Expenses"};
        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return labels[(int) value];
            }
        });

        barChart.getDescription().setText("Comparison of Savings and Expenses");
        barChart.getDescription().setEnabled(true);
    }
}
