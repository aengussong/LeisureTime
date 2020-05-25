package com.aengussong.leisuretime.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import com.aengussong.leisuretime.model.Leisure
import com.aengussong.leisuretime.usecase.mapper.Mapper

class GetLeisureUseCase(private val repo: LeisureRepository) : Mapper() {

    fun execute(): LiveData<List<Leisure>> {
        return Transformations.map(repo.getLeisures()) { list: List<LeisureEntity> ->
            list.map { it.toEntity() }
        }
    }
}