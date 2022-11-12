// @author  : iwdael
// @mail    : iwdael@outlook.com
// @project : https://github.com/iwdael/dbroom
package com.iwdael.dbroom.example.entity

import androidx.databinding.Bindable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iwdael.dbroom.DB
import com.iwdael.dbroom.annotations.UseDataBinding
import com.iwdael.dbroom.annotations.UseRoomNotifier
import com.iwdael.dbroom.core.RoomObservable
import kotlin.Long
import kotlin.String
import kotlin.Unit

@Entity
@UseRoomNotifier
@UseDataBinding
class Music : RoomObservable() {
  @PrimaryKey
  @Bindable
  private var id: Long? = null

  @Bindable
  private var name: String? = null

  @Bindable
  private var artists: String? = null

  @Bindable
  private var duration: String? = null

  @Bindable
  private var lyrics: String? = null

  fun setId(id: Long?) {
    this.id = id
  }

  fun setName(name: String?) {
    this.name = name
    notifyPropertyChanged(DB.name)
  }

  fun setArtists(artists: String?) {
    this.artists = artists
    notifyPropertyChanged(DB.artists)
  }

  fun setDuration(duration: String?) {
    this.duration = duration
    notifyPropertyChanged(DB.duration)
  }

  fun setLyrics(lyrics: String?) {
    this.lyrics = lyrics
    notifyPropertyChanged(DB.lyrics)
  }

  fun getId(): Long? = this.id

  fun getName(): String? = this.name

  fun getArtists(): String? = this.artists

  fun getDuration(): String? = this.duration

  fun getLyrics(): String? = this.lyrics
}
