package com.iwdael.dblite.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.iwdael.dblite.DbLite
import com.iwdael.dblite.R
import org.jetbrains.annotations.NotNull

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DbLite.init(this)
    }
}