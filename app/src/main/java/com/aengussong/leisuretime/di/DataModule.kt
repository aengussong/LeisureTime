package com.aengussong.leisuretime.di

import com.aengussong.leisuretime.LeisureDataViewModel
import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.data.LeisureRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {
    single<LeisureRepository> { LeisureRepositoryImpl() }
}

val viewModelModule = module {
    viewModel { LeisureDataViewModel() }
}
