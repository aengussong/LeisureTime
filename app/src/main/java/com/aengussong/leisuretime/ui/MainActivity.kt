package com.aengussong.leisuretime.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aengussong.leisuretime.R
import com.aengussong.leisuretime.adapter.LeisureAdapter
import com.aengussong.leisuretime.app.LeisureApp
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val adapter = LeisureAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener { openAddActivity() }

        recycler_view.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recycler_view.hasFixedSize()
        recycler_view.adapter = adapter

        adapter.onClickListener = View.OnClickListener { v ->
            val position = recycler_view.getChildAdapterPosition(v)
            val leisureEntity = adapter.list[position]
            Observable.fromCallable {
                LeisureApp.db.leisureDao().updateLeisure(leisureEntity.name, leisureEntity.counter + 1)
            }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe()
        }
    }

    override fun onResume() {
        super.onResume()
        fillRecycler()
    }

    private fun openAddActivity() {
        val intent = Intent(this, AddActivity::class.java)
        startActivity(intent)
    }

    private fun fillRecycler() {
        LeisureApp.db.leisureDao().getLeisures().observe(this, Observer {
            adapter.updateList(it)
        })
    }
}
