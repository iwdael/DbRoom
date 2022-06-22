package com.iwdael.dbroom.example.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author : iwdael
 * @mail : iwdael@outlook.com
 * @project : https://github.com/iwdael/DbRoom
 */
@Entity(tableName = "AV")
public class AirTech {

    @PrimaryKey
    private Long key;
    private char char_;
    private short short_;
    private byte byte_;
    private boolean boolean_;
    private int int_;
    private long long_;
    private double double_;
    private float float_;

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public char getChar_() {
        return char_;
    }

    public void setChar_(char char_) {
        this.char_ = char_;
    }

    public short getShort_() {
        return short_;
    }

    public void setShort_(short short_) {
        this.short_ = short_;
    }

    public byte getByte_() {
        return byte_;
    }

    public void setByte_(byte byte_) {
        this.byte_ = byte_;
    }

    public boolean isBoolean_() {
        return boolean_;
    }

    public void setBoolean_(boolean boolean_) {
        this.boolean_ = boolean_;
    }

    public int getInt_() {
        return int_;
    }

    public void setInt_(int int_) {
        this.int_ = int_;
    }

    public long getLong_() {
        return long_;
    }

    public void setLong_(long long_) {
        this.long_ = long_;
    }

    public double getDouble_() {
        return double_;
    }

    public void setDouble_(double double_) {
        this.double_ = double_;
    }

    public float getFloat_() {
        return float_;
    }

    public void setFloat_(float float_) {
        this.float_ = float_;
    }
}
