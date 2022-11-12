package com.iwdael.dbroom.example.entity;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.iwdael.dbroom.DB;
import com.iwdael.dbroom.core.RoomObservable;
import com.iwdael.dbroom.annotations.UseDataBinding;
import com.iwdael.dbroom.annotations.UseRoomNotifier;
import java.lang.Long;
import java.lang.String;

@UseRoomNotifier
@Entity
@UseDataBinding
public class Movie extends RoomObservable  {
  @PrimaryKey
  @Bindable
  private Long id;

  @Bindable
  private String name;

  @Bindable
  private String author;

  @Bindable
  private String duration;

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
    notifyPropertyChanged(DB.name);
  }

  public void setAuthor(String author) {
    this.author = author;
    notifyPropertyChanged(DB.author);
  }

  public void setDuration(String duration) {
    this.duration = duration;
    notifyPropertyChanged(DB.duration);
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getAuthor() {
    return this.author;
  }

  public String getDuration() {
    return this.duration;
  }
}
