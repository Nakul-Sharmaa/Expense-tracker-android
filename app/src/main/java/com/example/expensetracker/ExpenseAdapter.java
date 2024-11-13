package com.example.expensetracker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private ArrayList<Expense> expenseList;
    private Context context;  // Context to launch new activities
    private OnExpenseClickListener onExpenseClickListener;

    // Constructor to pass the context and the listener
    public ExpenseAdapter(ArrayList<Expense> expenseList, Context context, OnExpenseClickListener listener) {
        this.expenseList = expenseList;
        this.context = context;
        this.onExpenseClickListener = listener;
    }

    public interface OnExpenseClickListener {
        void onExpenseClick(Expense expense);
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the expense_item layout
        View itemView = LayoutInflater.from(context).inflate(R.layout.expense_item, parent, false);
        return new ExpenseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.textViewName.setText(expense.getName());
        holder.textViewAmount.setText(String.valueOf(expense.getAmount()));
        holder.textViewCategory.setText(expense.getCategory());

        holder.itemView.setOnClickListener(v -> {
            // Open ExpenseDetailActivity with expense details
            Intent intent = new Intent(context, ExpenseDetailActivity.class);
            intent.putExtra("expense_name", expense.getName());
            intent.putExtra("expense_amount", expense.getAmount());
            intent.putExtra("expense_category", expense.getCategory());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private TextView textViewAmount;
        private TextView textViewCategory;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_expense_name);
            textViewAmount = itemView.findViewById(R.id.text_expense_amount);
            textViewCategory = itemView.findViewById(R.id.text_expense_category);
        }
    }
}
