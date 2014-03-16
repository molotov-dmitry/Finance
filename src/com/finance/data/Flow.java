package com.finance.data;

import java.util.Date;

public class Flow {
    private long id;
    private Date date;
    private long account;
    private long amount;
    private String name;

    public Flow(long id, Date date, long account, long amount, String name) {
        this.id = id;
        this.date = date;
        this.account = account;
        this.amount = amount;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getAccount() {
        return account;
    }

    public void setAccount(long account) {
        this.account = account;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private long roubles() {
        return amount / 100;
    }

    private long kopeck() {
        return Math.abs(amount) % 100;
    }

    public String getAmount_s() {
        if (amount < 0)
            return "-" + String.valueOf(Math.abs(roubles())) + "." + String.format("%02d", kopeck());
        else
            return String.valueOf(Math.abs(roubles())) + "." + String.format("%02d", kopeck());
    }

    public String getAmount_c() {
        return getAmount_s() + " руб.";
    }
}
