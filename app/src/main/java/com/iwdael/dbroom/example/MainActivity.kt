package com.iwdael.dbroom.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.iwdael.dbroom.DbRoom
import com.iwdael.dbroom.example.room.findX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class MainActivity : AppCompatActivity() {
    companion object {
        const val DB_KEY = "DB_KEY"
    }

    override suspend fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DbRoom.init(this)

        DbRoom.user().findX()
            .map {
                it.asFlow()
            }
            .flowOn(Dispatchers.IO)
            .collect {

            }
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