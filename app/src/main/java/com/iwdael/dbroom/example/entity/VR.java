
package com.iwdael.dbroom.example.entity;

import androidx.databinding.Bindable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.iwdael.dbroom.annotations.UseDataBinding;
import com.iwdael.dbroom.annotations.UseRoom;
import com.iwdael.dbroom.core.Notifier;
import com.iwdael.dbroom.example.TypeConvert;

@TypeConverters({TypeConvert.class})
@UseDataBinding
@UseRoom
@Entity
public class VR implements Notifier {
  @PrimaryKey
  @Bindable
  private Long id;

  @Bindable
  private String name;

  @TypeConverters(TypeConvert.class)
  @Bindable
  private T t;

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

  public T getT() {
    return t;
  }

  public void setT(T t) {
    this.t = t;
  }
}
