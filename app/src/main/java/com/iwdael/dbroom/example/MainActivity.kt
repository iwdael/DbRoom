package com.iwdael.dbroom.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.iwdael.dblite.DbRoom
import com.iwdael.dblite.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        DbLite.init(this)
        DbRoom.user()
    }
}