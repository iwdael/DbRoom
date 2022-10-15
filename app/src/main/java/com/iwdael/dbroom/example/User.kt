package com.iwdael.dbroom.example

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iwdael.dbroom.DB
import com.iwdael.dbroom.RoomObserver
import com.iwdael.dbroom.annotations.UseDataBinding
import com.iwdael.dbroom.annotations.UseRoomNotifier
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Unit

@UseRoomNotifier(generate = false)
@Entity
@UseDataBinding
public class User : RoomObserver(), Observable {
  @PrimaryKey
  @Bindable
  private var id: Int? = null

  @Bindable
  private var name: String? = null

  @Bindable
  private var address: String? = null

  @Bindable
  private var isSelected: Boolean? = null

  public fun setId(id: Int?): Unit {
    this.id = id
  }

  public fun setName(name: String?): Unit {
    this.name = name
    notifyPropertyChanged(DB.name)
  }

  public fun setAddress(address: String?): Unit {
    this.address = address
    notifyPropertyChanged(DB.address)
  }

  public fun setSelected(isSelected: Boolean?): Unit {
    this.isSelected = isSelected
    notifyPropertyChanged(DB.isSelected)
  }

  public fun getId(): Int? = this.id

  public fun getName(): String? = this.name

  public fun getAddress(): String? = this.address

  public fun isSelected(): Boolean? = this.isSelected
}
