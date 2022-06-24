package com.iwdael.dbroom.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.iwdael.dbroom.DbRoom

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DbRoom.init(this)

        Thread {
            DbRoom.user()
                .insert(User(1, "jack", "china"))
            Log.v("dzq-1", DbRoom.user().all().joinToString(separator = ","))
            DbRoom.user()
                .update(User(1, "jack"))
            Log.v("dzq-2", DbRoom.user().all().joinToString(separator = ","))
        }.start()
    }
}