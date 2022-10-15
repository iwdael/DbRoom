package com.iwdael.dbroom.example

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iwdael.dbroom.DB
import com.iwdael.dbroom.RoomObserver
import com.iwdael.dbroom.annotations.Delete
import com.iwdael.dbroom.annotations.Find
import com.iwdael.dbroom.annotations.Update
import com.iwdael.dbroom.annotations.UseDataBinding
import com.iwdael.dbroom.annotations.UseRoomNotifier
import kotlin.Int
import kotlin.String
import kotlin.Unit

@UseRoomNotifier
@Entity
@UseDataBinding
class Post : RoomObserver(), Observable {
  @PrimaryKey
  @Bindable
  private var id: Int? = null

  @Update(
    value = ["updateInfo"],
    where = ["updateInfo"]
  )
  @Bindable
  private var name: String? = null

  @Find(value = ["findAddress"])
  @Delete(value = ["deleteAddress"])
  @Update(value = ["updateInfo"])
  @Bindable
  private var address: String? = null

  public fun setId(id: Int?): Unit {
    this.id = id
  }

  fun setName(name: String?) {
    this.name = name
    notifyPropertyChanged(DB.name)
  }

  fun setAddress(address: String?): Unit {
    this.address = address
    notifyPropertyChanged(DB.address)
  }

  public fun getId(): Int? = this.id

  public fun getName(): String? = this.name

  public fun getAddress(): String? = this.address
}
