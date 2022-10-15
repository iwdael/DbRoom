package com.iwdael.dbroom.example

import androidx.databinding.Bindable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iwdael.dbroom.annotations.UseDataBinding
import com.iwdael.dbroom.annotations.UseRoomNotifier

@UseRoomNotifier(generate = false)
@Entity
@UseDataBinding
class Pojo {
    @PrimaryKey
    var id: Long? = null
    var name: String? = null
    var address: String? = null


}
