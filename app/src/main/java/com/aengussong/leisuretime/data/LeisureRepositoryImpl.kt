package com.aengussong.leisuretime.data

import com.aengussong.leisuretime.data.local.dao.LeisureDao
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class LeisureRepositoryImpl : LeisureRepository, KoinComponent {

    private val localProvider: LeisureDao by inject()

    override suspend fun addLeisure(leisure: LeisureEntity) = withContext(Dispatchers.IO){
        localProvider.addLeisure(leisure)
    }

    override fun getLeisure(id: Long): Single<LeisureEntity> {
        return localProvider.getLeisure(id)
    }

    override fun updateLeisure(id: Long, newName: String): Completable {
        return localProvider.updateLeisure(id, newName)
    }

    override fun deleteLeisure(id: Long): Completable {
        return localProvider.deleteLeisure(id)
    }
}