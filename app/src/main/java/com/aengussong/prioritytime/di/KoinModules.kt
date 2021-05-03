package com.aengussong.prioritytime.di

import androidx.room.Room
import com.aengussong.prioritytime.TaskDataViewModel
import com.aengussong.prioritytime.data.TaskRepository
import com.aengussong.prioritytime.data.TaskRepositoryImpl
import com.aengussong.prioritytime.data.local.MIGRATION_1_2
import com.aengussong.prioritytime.data.local.SharedPrefs
import com.aengussong.prioritytime.data.local.TasksDb
import com.aengussong.prioritytime.usecase.*
import com.aengussong.prioritytime.usecase.periodicErase.GetPeriodicEraseUseCase
import com.aengussong.prioritytime.usecase.periodicErase.StartPeriodicEraseUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dbModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            TasksDb::class.java,
            "leisure_db"
        ).addMigrations(MIGRATION_1_2).build()
    }
    single { get<TasksDb>().taskDao() }
}

val dataModule = module {
    single<TaskRepository> { TaskRepositoryImpl(get(), get()) }
    single { AddTaskUseCase(get()) }
    single { GetTaskUseCase(get()) }
    single { IncrementTaskUseCase(get()) }
    single { RenameTaskUseCase(get()) }
    single { RemoveTaskUseCase(get()) }
    single { DropCountersUseCase(get()) }
    single { DecrementUseCase(get()) }
    single { SortOrderUseCase(get()) }
    single { SharedPrefs(get()) }
    factory { StartPeriodicEraseUseCase(get(), get()) }
    factory {GetPeriodicEraseUseCase(get())}
}

val viewModelModule = module {
    viewModel { TaskDataViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
