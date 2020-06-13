package com.aengussong.leisuretime.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.aengussong.leisuretime.LeisureDataViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class LeisureDataActivity : AppCompatActivity() {

    protected val viewModel: LeisureDataViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.errorLiveData.observe(this, Observer { throwable ->
            Toast.makeText(this, throwable.localizedMessage, Toast.LENGTH_SHORT).show()
        })
    }
}