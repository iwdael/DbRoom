package com.iwdael.dbroom.example

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.iwdael.dbroom.example.DbRoom
import com.iwdael.dbroom.example.databinding.ActivityMainBindingImpl
import com.iwdael.dbroom.example.entity.AirTechColumn
import com.iwdael.dbroom.example.entity.AirTechSQL
import com.iwdael.dbroom.example.entity.Music

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBindingImpl>(this, R.layout.activity_main)
        DbRoom.init(this)
        val finder = AirTechSQL
            .newFinder()
            .fields(AirTechColumn.double_,AirTechColumn.long_,AirTechColumn.float_)
            .where(AirTechColumn.byte_)
            .equal(1)
            .build()
        val updater = AirTechSQL.newUpdater()
            .appending(AirTechColumn.byte_, 1)
            .appended(AirTechColumn.short_, 1)
            .where(AirTechColumn.double_)
            .equal(0.0)
            .build()
        val inserter = AirTechSQL.newInserter()
            .appending(AirTechColumn.byte_, 1)
            .appending(AirTechColumn.double_, 1.0)
            .build()
        val deleter = AirTechSQL.newDeleter()
            .fields(AirTechColumn.double_,AirTechColumn.long_,AirTechColumn.float_)
            .where(AirTechColumn.byte_)
            .equal(1)
            .and()
            .where(AirTechColumn.int_)
            .unequal(10)
            .build()
        Log.v("DbRoom", inserter.selection)
        Log.v("DbRoom", updater.selection)
        Log.v("DbRoom", finder.selection)
        Log.v("DbRoom", deleter.selection)
        Thread {
            DbRoom.music().deleteAll()
            DbRoom.music().insert(Music().apply {
                name = "Android"
            })
            val m1 = DbRoom.music().findAll2().first()
            val m2 = DbRoom.music().findAll2().first()
            val m3 = DbRoom.music().findAll2().first()
            binding.entity1 = m1
            binding.entity2 = m2
            Thread.sleep(5000)
            m3.name = "IOS"
            Thread.sleep(5000)
            m3.name = "AC"
        }.start()
    }
}