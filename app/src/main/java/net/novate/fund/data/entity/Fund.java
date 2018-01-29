package net.novate.fund.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by gavin on 18-1-26.
 */

@Entity
public class Fund implements Comparable<Fund> {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String code;

    @ColumnInfo
    private String date;

    @ColumnInfo
    private double price;

    @ColumnInfo
    private double value;

    @ColumnInfo
    private double rate;

    @ColumnInfo
    private boolean purchasable;

    @ColumnInfo
    private boolean redeemable;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public boolean isPurchasable() {
        return purchasable;
    }

    public void setPurchasable(boolean purchasable) {
        this.purchasable = purchasable;
    }

    public boolean isRedeemable() {
        return redeemable;
    }

    public void setRedeemable(boolean redeemable) {
        this.redeemable = redeemable;
    }

    @Override
    public String toString() {
        return code + " : " + date + "  " + price + "  " + value + "  " + rate;
    }

    public int getYear() {
        return getDigitalDate() / 10000;
    }

    public int getMonth() {
        return getDigitalDate() / 100 % 100;
    }

    public int getDay() {
        return getDigitalDate() % 100;
    }

    public int getDigitalDate() {
        return Integer.parseInt(date.replace("-", ""));
    }

    @Override
    public int compareTo(@NonNull Fund target) {
        return getDigitalDate() - target.getDigitalDate();
    }
}
