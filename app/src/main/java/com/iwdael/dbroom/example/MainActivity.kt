package com.iwdael.dbroom.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.iwdael.dbroom.DbRoom
import com.iwdael.dbroom.example.databinding.ActivityMainBindingImpl
import com.iwdael.dbroom.example.entity.Music

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
            DbRoom.music().insert(Music().apply {
                name = "Android"
            })
            val m1 = DbRoom.music().findAll().first()
            val m2 = DbRoom.music().findAll().first()
            val m3 = DbRoom.music().findAll().first()
            binding.entity1 = m1
            binding.entity2 = m2
            Thread.sleep(5000)
            m3.name = "IOS"
            Thread.sleep(5000)
            m3.name = "AC"
        }.start()
    }
}