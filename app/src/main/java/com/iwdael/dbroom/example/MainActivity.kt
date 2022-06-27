package com.iwdael.dbroom.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.iwdael.dbroom.DbRoom

class MainActivity : AppCompatActivity() {
    companion object {
        const val DB_KEY = "DB_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DbRoom.init(this)

        Thread {
            try {
                Log.v("dzq", DbRoom.obtain(DB_KEY, String::class.java) + "-")
                Log.v("dzq", DbRoom.obtain(DB_KEY, "12312312"))
                DbRoom.store(DB_KEY, "Android")
                Log.v("dzq", DbRoom.obtain(DB_KEY, String::class.java))
//                DbRoom.store(DB_KEY, null)
//                Log.v("dzq", DbRoom.obtain(DB_KEY, String::class.java) + "-")
            } catch (e: Exception) {
                Log.v("dzq", e.stackTraceToString())
            }
        }.start()
    }
}