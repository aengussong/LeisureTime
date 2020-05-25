package com.aengussong.leisuretime.data

import androidx.lifecycle.LiveData
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import io.reactivex.Completable
import io.reactivex.Single

interface LeisureRepository {
    suspend fun addLeisure(leisure: LeisureEntity)
    suspend fun getLowestCounter(ancestry:String):Long
    suspend fun getAncestry(id:Long):String
    fun getLeisures(): LiveData<List<LeisureEntity>>
}