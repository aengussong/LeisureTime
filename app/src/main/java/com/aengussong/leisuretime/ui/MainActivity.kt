package com.aengussong.leisuretime.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aengussong.leisuretime.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener{openAddActivity()}
    }

    override fun onResume() {
        super.onResume()
        fillRecycler()
    }

    private fun openAddActivity(){
        val intent = Intent(this, AddActivity::class.java)
        startActivity(intent)
    }

    private fun fillRecycler(){
        
    }
}
