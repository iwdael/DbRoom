// @author  : iwdael
// @mail    : iwdael@outlook.com
// @project : https://github.com/iwdael/dbroom
package com.iwdael.dbroom.example.entity

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iwdael.dbroom.annotations.Find
import com.iwdael.dbroom.annotations.UseDataBinding
import com.iwdael.dbroom.annotations.UseNotifier
import com.iwdael.dbroom.annotations.UseRoom
import kotlin.Long
import kotlin.String

@Entity
@UseDataBinding
@UseRoom
@UseNotifier
open class Music : BaseObservable() {
  @PrimaryKey
  open var id: Long? = null

  @Bindable
  @Find("findByName")
  open var name: String? = null

  @Bindable
  open var artists: String? = null

  @Bindable
  open var duration: String? = null

  @Bindable
  open var lyrics: String? = null
}
