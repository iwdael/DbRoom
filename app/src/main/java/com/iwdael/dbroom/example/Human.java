package com.iwdael.dbroom.example;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.iwdael.dbroom.annotation.Insert;

@Entity
public class Human {
    @PrimaryKey
    Long id;
    @Insert(value = {"insertInfo"})
    String name;
    @Insert(value = {"insertInfo"})
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
