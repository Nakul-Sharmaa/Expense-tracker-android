package com.example.expensetracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExpenseAdapter expenseAdapter;
    private ArrayList<Expense> expenseList;
    private SharedPreferences sharedPreferences;
    private double userSalary;
    private static final String PREFS_NAME = "user_prefs";
    private static final String SALARY_KEY = "user_salary";


    private static final String CHANNEL_ID = "expense_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView currentDateTextView = findViewById(R.id.text_current_date);
        String currentDate = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
                .format(Calendar.getInstance().getTime());
        currentDateTextView.setText("Date: " + currentDate);


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userSalary = sharedPreferences.getFloat(SALARY_KEY, 0);


        expenseList = loadExpenses();
        expenseAdapter = new ExpenseAdapter(expenseList, MainActivity.this, this::showExpenseDetails);
        recyclerView.setAdapter(expenseAdapter);

        // Button to add an expense
        Button addButton = findViewById(R.id.button_add_expense);
        addButton.setOnClickListener(v -> showAddExpenseDialog());

        // Button to view past expenses
        Button viewPastExpensesButton = findViewById(R.id.button_view_past_expenses);
        viewPastExpensesButton.setOnClickListener(v -> navigateToPastExpensesActivity());

        // Button to view the chart
        Button viewChartButton = findViewById(R.id.button_view_chart);
        viewChartButton.setOnClickListener(v -> navigateToBarChartActivity());

        // Check if expenses exceed salary and show notification if needed
        checkExpensesExceedSalary();

        // Create notification channel for Android O and above
        createNotificationChannel();
    }

    // Method to load the list of expenses from SharedPreferences
    private ArrayList<Expense> loadExpenses() {
        ArrayList<Expense> expenses = new ArrayList<>();
        String expensesJson = sharedPreferences.getString("expenses", null);
        if (expensesJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Expense>>() {}.getType();
            expenses = gson.fromJson(expensesJson, type);
        }
        return expenses;
    }

    // Method to save the expenses to SharedPreferences
    private void saveExpenses() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String expensesJson = gson.toJson(expenseList);
        editor.putString("expenses", expensesJson);
        editor.apply();
    }

    // Method to show the "Add Expense" dialog
    private void showAddExpenseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Expense");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_expense, null);
        builder.setView(view);

        EditText nameInput = view.findViewById(R.id.edit_text_name);
        EditText amountInput = view.findViewById(R.id.edit_text_amount);
        RadioGroup categoryGroup = view.findViewById(R.id.radio_group_category);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameInput.getText().toString();
            double amount;
            try {
                amount = Double.parseDouble(amountInput.getText().toString());
            } catch (NumberFormatException e) {
                amountInput.setError("Enter a valid number");
                return;
            }

            // Get selected category from RadioGroup
            int selectedId = categoryGroup.getCheckedRadioButtonId();

            if (selectedId == -1) {
                // No category selected
                Toast.makeText(MainActivity.this, "Please select a category", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the selected RadioButton
            RadioButton selectedRadioButton = view.findViewById(selectedId);
            if (selectedRadioButton != null) {
                String category = selectedRadioButton.getText().toString();

                if (!name.isEmpty() && !category.isEmpty()) {
                    expenseList.add(new Expense(name, amount, category));
                    expenseAdapter.notifyDataSetChanged();
                    saveExpenses();
                    checkExpensesExceedSalary();  // Recheck the expenses after adding a new one
                } else {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            } else {
                // This case should not happen, but if for any reason the RadioButton is null, show an error.
                Toast.makeText(MainActivity.this, "Category selection failed. Try again.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Method to navigate to PastExpensesActivity
    private void navigateToPastExpensesActivity() {
        Intent intent = new Intent(MainActivity.this, PastExpensesActivity.class);
        startActivity(intent);
    }

    // Method to navigate to BarChartActivity
    private void navigateToBarChartActivity() {
        double totalExpense = 0;
        for (Expense expense : expenseList) {
            totalExpense += expense.getAmount();
        }

        Intent intent = new Intent(MainActivity.this, BarChartActivity.class);
        intent.putExtra("salary", userSalary);
        intent.putExtra("total_expense", totalExpense);
        startActivity(intent);
    }


    private void showExpenseDetails(Expense expense) {
        Intent intent = new Intent(this, ExpenseDetailActivity.class);
        intent.putExtra("expense_name", expense.getName());
        intent.putExtra("expense_amount", expense.getAmount());
        intent.putExtra("expense_category", expense.getCategory());
        startActivity(intent);
    }


    private void checkExpensesExceedSalary() {
        double totalExpense = 0;
        for (Expense expense : expenseList) {
            totalExpense += expense.getAmount();
        }

        if (totalExpense > userSalary) {
            sendHeadsUpNotification();
        }
    }

    private void sendHeadsUpNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)  // System warning icon
                .setContentTitle("Warning")
                .setContentText("Expenses have exceeded your salary!")
                .setPriority(NotificationCompat.PRIORITY_MAX)  // Max priority for full-screen heads-up notification
                .setAutoCancel(true)
                .setFullScreenIntent(pendingIntent, true)  // Full-screen intent for heads-up display
                .setDefaults(Notification.DEFAULT_ALL) // Trigger sound, vibration, and lights
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)  // Show on lock screen
                .setCategory(NotificationCompat.CATEGORY_ALARM)  // Treat as an alarm
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) // Notification sound
                .setTimeoutAfter(10000); // Auto-dismiss after 10 seconds

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());  // Display notification with ID 1
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Expense Notifications";
            String description = "Channel for expense-related warnings";
            int importance = NotificationManager.IMPORTANCE_HIGH; // High importance for heads-up notifications
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
