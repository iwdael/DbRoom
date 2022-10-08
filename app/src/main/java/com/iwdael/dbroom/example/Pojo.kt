package com.iwdael.dbroom.example

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iwdael.dbroom.DB
import com.iwdael.dbroom.RoomObserver
import com.iwdael.dbroom.annotation.UseRoomMap

@UseRoomMap
@Entity
class Pojo : RoomObserver() {
    @PrimaryKey
    var id: Long? = null
    var name: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(DB.name)
        }
    var address: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(DB.address)
        }
}