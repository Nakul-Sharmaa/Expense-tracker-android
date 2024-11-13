package com.example.expensetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import java.util.List;

public class PastExpensesAdapter extends ArrayAdapter<String> {

    public PastExpensesAdapter(Context context, List<String> pastExpenses) {
        super(context, 0, pastExpenses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.past_expense_item, parent, false);
        }
        String record = getItem(position);
        TextView recordTextView = convertView.findViewById(R.id.record_text_view);
        recordTextView.setText(record);

        return convertView;
    }
}
