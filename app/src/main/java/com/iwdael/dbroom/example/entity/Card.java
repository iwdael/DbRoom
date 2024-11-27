
package com.iwdael.dbroom.example.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.iwdael.dbroom.example.util.TypeConvert;

@TypeConverters({TypeConvert.class})
@Entity
public class Card {
  @PrimaryKey
  private Long id;
  private String name;
  private Title t;

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }


  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public Title getT() {
    return t;
  }

  public void setT(Title t) {
    this.t = t;
  }
}
