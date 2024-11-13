package com.example.expensetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SalaryInputActivity extends AppCompatActivity {
    private EditText salaryInput;
    private static final String PREFS_NAME = "user_prefs";
    private static final String SALARY_KEY = "user_salary";
    private static final String SALARY_DATE_KEY = "salary_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary_input);

        salaryInput = findViewById(R.id.edit_text_salary);
        Button submitButton = findViewById(R.id.button_submit_salary);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String salaryStr = salaryInput.getText().toString();
                if (!salaryStr.isEmpty()) {
                    double salary = Double.parseDouble(salaryStr);


                    SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat(SALARY_KEY, (float) salary);


                    String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault())
                            .format(Calendar.getInstance().getTime());
                    editor.putString(SALARY_DATE_KEY, currentMonth);
                    editor.apply();


                    Intent intent = new Intent(SalaryInputActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SalaryInputActivity.this, "Please enter a valid salary", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
