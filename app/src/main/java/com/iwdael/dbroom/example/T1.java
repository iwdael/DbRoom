package com.iwdael.dbroom.example;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.iwdael.dbroom.DB;
import com.iwdael.dbroom.RoomObserver;
import com.iwdael.dbroom.annotation.UseRoomMap;

@UseRoomMap
@Entity
public class T1 extends RoomObserver {
    @PrimaryKey
    private Long id_;
    private String name_;
    private String address_;
    private String age_;


    public Long getId_() {
        return id_;
    }

    public void setId_(Long id_) {
        this.id_ = id_;
    }

    public String getName_() {
        return name_;
    }

    public void setName_(String name_) {
        this.name_ = name_;
        notifyPropertyChanged(DB.name_);
    }

    public String getAddress_() {
        return address_;
    }

    public void setAddress_(String address_) {
        this.address_ = address_;
        notifyPropertyChanged(DB.address_);
    }

    public String getAge_() {
        return age_;
    }

    public void setAge_(String age_) {
        this.age_ = age_;
        notifyPropertyChanged(DB.age_);
    }
}