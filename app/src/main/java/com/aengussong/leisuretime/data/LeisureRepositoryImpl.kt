package com.aengussong.leisuretime.data

import com.aengussong.leisuretime.data.local.dao.LeisureDao
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject

class LeisureRepositoryImpl : LeisureRepository, KoinComponent {

    private val localProvider: LeisureDao by inject()

    override fun addLeisure(leisure: LeisureEntity) {
        localProvider.addLeisure(leisure)
    }

    override fun getLeisure(name: String): Single<LeisureEntity> {
        return localProvider.getLeisure(name)
    }
}