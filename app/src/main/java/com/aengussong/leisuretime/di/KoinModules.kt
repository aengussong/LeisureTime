package com.aengussong.leisuretime.di

import androidx.room.Room
import com.aengussong.leisuretime.LeisureDataViewModel
import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.data.LeisureRepositoryImpl
import com.aengussong.leisuretime.data.local.LeisureDb
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

val dbModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            LeisureDb::class.java,
            "leisure_db"
        ).build()
    }
}

val dataModule = module {
    single<LeisureRepository> { LeisureRepositoryImpl() }
    single { get<LeisureDb>().leisureDao() }
}

val viewModelModule = module {
    viewModel { LeisureDataViewModel(get()) }
}
