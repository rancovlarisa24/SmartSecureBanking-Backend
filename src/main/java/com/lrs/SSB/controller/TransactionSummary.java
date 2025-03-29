package com.lrs.SSB.controller;

public class TransactionSummary {
    private String name;
    private long count;

    public TransactionSummary(String name, long count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public long getCount() {
        return count;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
