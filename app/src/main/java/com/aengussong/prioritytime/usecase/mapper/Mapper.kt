package com.aengussong.prioritytime.usecase.mapper

import com.aengussong.prioritytime.data.local.entity.TaskEntity
import com.aengussong.prioritytime.model.Task

open class Mapper {

    protected fun TaskEntity.toTask() = Task(id, name, counter, updated)
}