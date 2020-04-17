package com.aengussong.leisuretime.data

import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import io.reactivex.Single

interface LeisureRepository {
    fun addLeisure(leisure: LeisureEntity)
    fun getLeisure(name: String): Single<LeisureEntity>
}