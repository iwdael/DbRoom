package com.iwdael.dbroom.example;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.iwdael.dbroom.DB;
import com.iwdael.dbroom.RoomObserver;
import com.iwdael.dbroom.annotations.UseDataBinding;
import com.iwdael.dbroom.annotations.UseRoomNotifier;
import java.lang.Long;
import java.lang.String;

@UseRoomNotifier(
    generate = false
)
@UseDataBinding
@Entity
public class T1 extends RoomObserver implements Observable {
  @ColumnInfo(
      name = "c_name"
  )
  @PrimaryKey
  @Bindable
  Long id_;

  @Bindable
  String name_;

  @Bindable
  String address_;

  @Bindable
  String age_;

  public void setId_(Long id_) {
    this.id_ = id_;
  }

  public void setName_(String name_) {
    this.name_ = name_;
    notifyPropertyChanged(DB.name_);
  }

  public void setAddress_(String address_) {
    this.address_ = address_;
    notifyPropertyChanged(DB.address_);
  }

  public void setAge_(String age_) {
    this.age_ = age_;
    notifyPropertyChanged(DB.age_);
  }

  public Long getId_() {
    return this.id_;
  }

  public String getName_() {
    return this.name_;
  }

  public String getAddress_() {
    return this.address_;
  }

  public String getAge_() {
    return this.age_;
  }
}
