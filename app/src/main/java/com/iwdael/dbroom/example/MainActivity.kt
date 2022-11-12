package com.iwdael.dbroom.example

import  android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.iwdael.dbroom.DbRoom
import com.iwdael.dbroom.example.databinding.ActivityMainBinding
import com.iwdael.dbroom.example.databinding.ActivityMainBindingImpl
import com.iwdael.dbroom.example.entity.Music
import com.iwdael.dbroom.example.entity.MusicObservable

class MainActivity : AppCompatActivity() {
    companion object {
        const val DB_KEY = "DB_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBindingImpl>(this, R.layout.activity_main)
        DbRoom.init(this)
        Thread {
            DbRoom.music().deleteAll()
            DbRoom.music().insert(Music().apply { setName("Android") })
            Thread.sleep(10000)
            val music0 = DbRoom.music().findAll().first()
            val music1 = DbRoom.music().findAll().first()
            val music2 = DbRoom.music().findAll().first()
            Log.v("dzq","name:${music1.getName()},id:${music1.getId()}")
            binding.entity1 = music1
            binding.entity2 = music2
            Log.v("dzq","e0:${((music0.dbObservable) as MusicObservable).nameEntityVersion}")
            Log.v("dzq","e1:${((music1.dbObservable) as MusicObservable).nameEntityVersion}")
            Log.v("dzq","e2:${((music2.dbObservable) as MusicObservable).nameEntityVersion}")
            Log.v("dzq","r0:${((music0.dbObservable) as MusicObservable).nameEntityVersion}")
            Log.v("dzq","r1:${((music1.dbObservable) as MusicObservable).nameEntityVersion}")
            Log.v("dzq","r2:${((music2.dbObservable) as MusicObservable).nameEntityVersion}")
            Thread.sleep(10000)
            music0.setName("IOS")
            Log.v("dzq","name:${music1.getName()}")

            Log.v("dzq","e0:${((music0.dbObservable) as MusicObservable).nameEntityVersion}")
            Log.v("dzq","e1:${((music1.dbObservable) as MusicObservable).nameEntityVersion}")
            Log.v("dzq","e2:${((music2.dbObservable) as MusicObservable).nameEntityVersion}")
            Log.v("dzq","r0:${((music0.dbObservable) as MusicObservable).nameEntityVersion}")
            Log.v("dzq","r1:${((music1.dbObservable) as MusicObservable).nameEntityVersion}")
            Log.v("dzq","r2:${((music2.dbObservable) as MusicObservable).nameEntityVersion}")

        }.start()
//        val music  = Music().apply { setName("IOS") }
//        binding.entity1 =  music
//        music.setId(1)
//        music.setName("Android")
    }
}