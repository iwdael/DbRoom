// @author  : iwdael
// @mail    : iwdael@outlook.com
// @project : https://github.com/iwdael/dbroom
package com.iwdael.dbroom.example.entity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.iwdael.dbroom.annotations.UseDataBinding;
import com.iwdael.dbroom.annotations.UseRoom;
import java.lang.Long;
import java.lang.String;

@UseDataBinding
@UseRoom
@Entity
@TypeConverters
public class Movie extends BaseObservable {
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
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public void setDuration(String duration) {
    this.duration = duration;
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
