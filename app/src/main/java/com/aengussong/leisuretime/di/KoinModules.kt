package com.aengussong.leisuretime.di

import androidx.room.Room
import com.aengussong.leisuretime.LeisureDataViewModel
import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.data.LeisureRepositoryImpl
import com.aengussong.leisuretime.data.local.LeisureDb
import com.aengussong.leisuretime.usecase.AddLeisureUseCase
import com.aengussong.leisuretime.usecase.GetLeisureUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dbModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            LeisureDb::class.java,
            "leisure_db"
        )
            .createFromAsset("db/leisure")
            .build()
    }
    single { get<LeisureDb>().leisureDao() }
}

val dataModule = module {
    single<LeisureRepository> { LeisureRepositoryImpl() }
    single { AddLeisureUseCase(get()) }
    single { GetLeisureUseCase(get()) }
}

val viewModelModule = module {
    viewModel { LeisureDataViewModel(get(), get()) }
}
