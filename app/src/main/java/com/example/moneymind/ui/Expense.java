package com.example.moneymind.ui;

public class Expense {
    public String title;
    public String amount;
    public String date;
    public int iconResId;

    public Expense(String title, String amount, String date, int iconResId) {
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.iconResId = iconResId;
    }
}