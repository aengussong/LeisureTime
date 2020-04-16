package com.aengussong.leisuretime.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aengussong.leisuretime.R
import kotlinx.android.synthetic.main.activity_add.*

class AddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        add_btn.setOnClickListener {
            val input = input_name.text.toString()
            if (input.isNotEmpty()) {
//                Observable.fromCallable { LeisureApp.db.leisureDao().insert(LeisureEntity(input, 0, Date())) }
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribeOn(Schedulers.io())
//                    .subscribe()
                finish()
            }
        }
    }
}
