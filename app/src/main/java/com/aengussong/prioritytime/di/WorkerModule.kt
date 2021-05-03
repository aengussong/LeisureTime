package com.aengussong.prioritytime.di

import com.aengussong.prioritytime.worker.EraseDataWorker
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val workerModule = module {
    worker { EraseDataWorker(get(), get(), get()) }
}