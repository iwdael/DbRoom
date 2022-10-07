package com.iwdael.dbroom.example;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.iwdael.dbroom.DB;
import com.iwdael.dbroom.RoomObserver;
import com.iwdael.dbroom.annotation.Insert;

@Entity
public class Human extends RoomObserver {
    @PrimaryKey
    private Long id;
    @Insert(value = {"insertInfo"})
    private String name;
    @Insert(value = {"insertInfo"})
    private String address;

    private boolean isSelected;
    private Boolean isEnabled;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(DB.address);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

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


}
