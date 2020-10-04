package com.aengussong.prioritytime.usecase.mapper

import com.aengussong.prioritytime.data.local.entity.LeisureEntity
import com.aengussong.prioritytime.model.Leisure

open class Mapper {

    protected fun LeisureEntity.toLeisure() = Leisure(id, name, counter, updated)
}