package com.example.expensetracker;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ExpenseDetailActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private ArrayList<Expense> expenseList;
    private int expensePosition;
    private static final String PREFS_NAME = "user_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        expenseList = loadExpenses();

        String expenseName = getIntent().getStringExtra("expense_name");
        double expenseAmount = getIntent().getDoubleExtra("expense_amount", 0);
        String expenseCategory = getIntent().getStringExtra("expense_category");

        EditText nameEditText = findViewById(R.id.edit_text_expense_name);
        EditText amountEditText = findViewById(R.id.edit_text_expense_amount);
        RadioGroup categoryGroup = findViewById(R.id.radio_group_category);

        nameEditText.setText(expenseName);
        amountEditText.setText(String.valueOf(expenseAmount));

        for (int i = 0; i < categoryGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) categoryGroup.getChildAt(i);
            if (radioButton.getText().toString().equals(expenseCategory)) {
                radioButton.setChecked(true);
            }
        }

        expensePosition = findExpensePosition(expenseName, expenseAmount, expenseCategory);

        Button updateButton = findViewById(R.id.button_update_expense);
        updateButton.setOnClickListener(v -> updateExpense(nameEditText, amountEditText, categoryGroup));

        Button deleteButton = findViewById(R.id.button_delete_expense);
        deleteButton.setOnClickListener(v -> deleteExpense());
    }

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

    private int findExpensePosition(String name, double amount, String category) {
        for (int i = 0; i < expenseList.size(); i++) {
            Expense expense = expenseList.get(i);
            if (expense.getName().equals(name) && expense.getAmount() == amount && expense.getCategory().equals(category)) {
                return i;
            }
        }
        return -1;
    }


    private void updateExpense(EditText nameEditText, EditText amountEditText, RadioGroup categoryGroup) {
        String newName = nameEditText.getText().toString();
        double newAmount;
        try {
            newAmount = Double.parseDouble(amountEditText.getText().toString());
        } catch (NumberFormatException e) {
            amountEditText.setError("Enter a valid number");
            return;
        }

        int selectedId = categoryGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);
        String newCategory = selectedRadioButton.getText().toString();

        if (!newName.isEmpty() && !newCategory.isEmpty()) {

            Expense updatedExpense = new Expense(newName, newAmount, newCategory);
            expenseList.set(expensePosition, updatedExpense);
            saveExpenses();
            Toast.makeText(this, "Expense updated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to delete the expense
    private void deleteExpense() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Remove the expense from the list
                    expenseList.remove(expensePosition);
                    saveExpenses(); // Save the updated list after deletion
                    Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show();
                    finish(); // Close this activity
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Method to save the expenses to SharedPreferences
    private void saveExpenses() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String expensesJson = gson.toJson(expenseList);
        editor.putString("expenses", expensesJson);
        editor.apply();
    }
}
