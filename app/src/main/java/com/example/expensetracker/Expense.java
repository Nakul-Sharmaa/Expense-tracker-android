package com.example.expensetracker;

public class Expense {
    private String name;
    private double amount;
    private String category;

    public Expense(String name, double amount, String category) {
        this.name = name;
        this.amount = amount;
        this.category = category;
    }

    public String getName() { return name; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }

    public void setName(String name) { this.name = name; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
}
