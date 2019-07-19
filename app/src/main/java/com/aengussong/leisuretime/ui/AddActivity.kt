package com.aengussong.leisuretime.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aengussong.leisuretime.R
import com.aengussong.leisuretime.app.LeisureApp
import com.aengussong.leisuretime.data.entity.LeisureEntity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add.*
import java.util.*

class AddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        add_btn.setOnClickListener {
            val input = input_name.text.toString()
            if (input.isNotEmpty()) {
                Observable.fromCallable { LeisureApp.db.leisureDao().insert(LeisureEntity(input, 0, Date())) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe()
                finish()
            }
        }
    }
}
