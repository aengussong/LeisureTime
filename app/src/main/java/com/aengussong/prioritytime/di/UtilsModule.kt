package com.aengussong.prioritytime.di

import com.aengussong.prioritytime.worker.WorkerHelper
import org.koin.dsl.module

val utilsModule = module {
    factory { WorkerHelper(get()) }
}