package com.example.expensetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import android.content.SharedPreferences;

public class PastExpensesActivity extends AppCompatActivity {

    private ListView pastExpensesListView;
    private SharedPreferences sharedPreferences;
    private static final String PAST_EXPENSES_PREFS = "past_expenses_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_expenses);

        pastExpensesListView = findViewById(R.id.past_expenses_list_view);
        sharedPreferences = getSharedPreferences(PAST_EXPENSES_PREFS, MODE_PRIVATE);

        loadPastExpenses();
    }

    private void loadPastExpenses() {
        String pastExpensesJson = sharedPreferences.getString("past_expenses", null);
        if (pastExpensesJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, ArrayList<Expense>>>() {}.getType();
            Map<String, ArrayList<Expense>> pastExpensesMap = gson.fromJson(pastExpensesJson, type);

            ArrayList<String> displayList = new ArrayList<>();
            for (Map.Entry<String, ArrayList<Expense>> entry : pastExpensesMap.entrySet()) {
                StringBuilder record = new StringBuilder("Date: " + entry.getKey() + "\n");
                for (Expense expense : entry.getValue()) {
                    record.append("- ").append(expense.getName())
                            .append(": $").append(expense.getAmount())
                            .append(" (").append(expense.getCategory()).append(")\n");
                }
                displayList.add(record.toString());
            }

            PastExpensesAdapter adapter = new PastExpensesAdapter(this, displayList);
            pastExpensesListView.setAdapter(adapter);
        } else {
            TextView emptyView = findViewById(R.id.empty_view);
            emptyView.setVisibility(View.VISIBLE);
        }
    }
}
