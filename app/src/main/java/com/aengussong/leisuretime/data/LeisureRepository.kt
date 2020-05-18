package com.aengussong.leisuretime.data

import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import io.reactivex.Completable
import io.reactivex.Single

interface LeisureRepository {
    fun addLeisure(leisure: LeisureEntity): Completable
    fun getLeisure(id: Long): Single<LeisureEntity>
    fun updateLeisure(id: Long, newName: String): Completable
    fun deleteLeisure(id: Long): Completable
}