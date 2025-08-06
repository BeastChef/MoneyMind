package com.example.moneymind.model;

public class User {
    private String email;
    private String name;
    private double balance;

    // Конструктор по умолчанию (для Firestore)
    public User() {}

    // Конструктор с параметрами
    public User(String email, String name, double balance) {
        this.email = email;
        this.name = name;
        this.balance = balance;
    }

    // Геттеры и сеттеры для каждого поля
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
