package com.aengussong.prioritytime.di

import androidx.room.Room
import com.aengussong.prioritytime.LeisureDataViewModel
import com.aengussong.prioritytime.data.LeisureRepository
import com.aengussong.prioritytime.data.LeisureRepositoryImpl
import com.aengussong.prioritytime.data.local.LeisureDb
import com.aengussong.prioritytime.data.local.SharedPrefs
import com.aengussong.prioritytime.usecase.*
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dbModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            LeisureDb::class.java,
            "leisure_db"
        ).build()
    }
    single { get<LeisureDb>().leisureDao() }
}

val dataModule = module {
    single<LeisureRepository> { LeisureRepositoryImpl(get(), get()) }
    single { AddLeisureUseCase(get()) }
    single { GetLeisureUseCase(get()) }
    single { IncrementLeisureUseCase(get()) }
    single { RenameLeisureUseCase(get()) }
    single { RemoveLeisureUseCase(get()) }
    single { DropCountersUseCase(get()) }
    single { DecrementUseCase(get()) }
    single { SortOrderUseCase(get()) }
    single { SharedPrefs(get()) }
}

val viewModelModule = module {
    viewModel { LeisureDataViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
}
