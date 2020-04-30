package com.aengussong.leisuretime.data

import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import io.reactivex.Completable
import io.reactivex.Single

interface LeisureRepository {
    fun addLeisure(leisure: LeisureEntity): Completable
    fun getLeisure(name: String): Single<LeisureEntity>
    fun updateLeisure(oldName: String, newName: String): Completable
}