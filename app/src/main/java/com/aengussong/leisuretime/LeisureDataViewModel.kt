package com.aengussong.leisuretime

import androidx.lifecycle.ViewModel
import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import org.koin.core.KoinComponent
import org.koin.core.inject

class LeisureDataViewModel : ViewModel(), KoinComponent {

    private val repo: LeisureRepository by inject()

    fun addLeisure(leisure: LeisureEntity) {
        repo.addLeisure(leisure)
    }

    fun getLeisure(name: String): LeisureEntity {
        return repo.getLeisure(name)
    }
}