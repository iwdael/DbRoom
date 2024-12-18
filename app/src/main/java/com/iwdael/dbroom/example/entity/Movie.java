
package com.iwdael.dbroom.example.entity;

import androidx.databinding.Bindable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Movie  {
  @PrimaryKey
  private Long id;

  private String name;

  private String author;

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
