package com.aengussong.leisuretime.di

import com.aengussong.leisuretime.LeisureDataViewModel
import com.aengussong.leisuretime.ui.MainActivity
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {

    scope<MainActivity> {
        viewModel { LeisureDataViewModel() }
    }
}
