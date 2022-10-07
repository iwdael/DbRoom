package com.iwdael.dbroom.example;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

public class T1 {
    @PrimaryKey
    private String name = null;
    private String uAddress = null;
    private String Age = null;
    private boolean isSelected = false;
    private boolean uEnabled = false;
    private boolean Disabled = false;
    private boolean iisOk = false;
    private Boolean isSelected1 = null;
    private Boolean uEnabled1 = null;
    private Boolean Disabled1 = null;
    private Boolean iisOk1 = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getuAddress() {
        return uAddress;
    }

    public void setuAddress(String uAddress) {
        this.uAddress = uAddress;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isuEnabled() {
        return uEnabled;
    }

    public void setuEnabled(boolean uEnabled) {
        this.uEnabled = uEnabled;
    }

    public boolean isDisabled() {
        return Disabled;
    }

    public void setDisabled(boolean disabled) {
        Disabled = disabled;
    }

    public boolean isIisOk() {
        return iisOk;
    }

    public void setIisOk(boolean iisOk) {
        this.iisOk = iisOk;
    }

    public Boolean getSelected1() {
        return isSelected1;
    }

    public void setSelected1(Boolean selected1) {
        isSelected1 = selected1;
    }

    public Boolean getuEnabled1() {
        return uEnabled1;
    }

    public void setuEnabled1(Boolean uEnabled1) {
        this.uEnabled1 = uEnabled1;
    }

    public Boolean getDisabled1() {
        return Disabled1;
    }

    public void setDisabled1(Boolean disabled1) {
        Disabled1 = disabled1;
    }

    public Boolean getIisOk1() {
        return iisOk1;
    }

    public void setIisOk1(Boolean iisOk1) {
        this.iisOk1 = iisOk1;
    }
}
