package com.finance.data;

public class Account {
    private long id;
    private String name;
    private long balance;

    private long roubles() {
        return balance / 100;
    }

    private long kopeck() {
        return Math.abs(balance) % 100;
    }

    public Account(long id, String name, long balance) {
        this.setId(id);
        this.setName(name);
        this.setBalance(balance);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getBalance() {
        return this.balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public String getBalance_s() {
        if (balance < 0)
            return "-" + String.valueOf(Math.abs(roubles())) + "." + String.format("%02d", kopeck());
        else
            return String.valueOf(Math.abs(roubles())) + "." + String.format("%02d", kopeck());
    }

    public String getBalance_c() {
        return getBalance_s() + " руб.";
    }
}
