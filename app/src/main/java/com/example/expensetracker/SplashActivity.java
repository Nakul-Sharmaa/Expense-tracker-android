package com.example.expensetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SplashActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "user_prefs";
    private static final String SALARY_KEY = "user_salary";
    private static final String SALARY_DATE_KEY = "salary_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            if (isSalaryAddedForCurrentMonth()) {

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            } else {

                Intent intent = new Intent(SplashActivity.this, SalaryInputActivity.class);
                startActivity(intent);
            }
            finish();
        }, 2000); // 2000 milliseconds = 2 seconds
    }

    private boolean isSalaryAddedForCurrentMonth() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        double salary = sharedPreferences.getFloat(SALARY_KEY, -1);
        String savedDate = sharedPreferences.getString(SALARY_DATE_KEY, "");


        String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault())
                .format(Calendar.getInstance().getTime());


        return salary != -1 && currentMonth.equals(savedDate);
    }
}
