// @author  : iwdael
// @mail    : iwdael@outlook.com
// @project : https://github.com/iwdael/dbroom
package com.iwdael.dbroom.example.entity

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iwdael.dbroom.annotations.UseDataBinding
import com.iwdael.dbroom.annotations.UseRoomNotifier
import kotlin.Long
import kotlin.String

@Entity
@UseDataBinding
@UseRoomNotifier
open class Music : BaseObservable() {
  @PrimaryKey
  @Bindable
  open var id: Long? = null

  @Bindable
  open var name: String? = null

  @Bindable
  open var artists: String? = null

  @Bindable
  open var duration: String? = null

  @Bindable
  open var lyrics: String? = null
}
