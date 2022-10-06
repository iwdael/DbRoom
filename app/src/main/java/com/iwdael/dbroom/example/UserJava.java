package com.iwdael.dbroom.example;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserJava {
    @PrimaryKey
    Long id;

    String name;
    String address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
