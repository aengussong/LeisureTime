package com.aengussong.leisuretime.model

import com.aengussong.leisuretime.R
import tellh.com.recyclertreeview_lib.LayoutItemType
import java.util.*

data class Leisure(
    val id: Long,
    val name: String,
    val counter: Long,
    val updated: Date
) : LayoutItemType {
    override fun getLayoutId() = R.layout.item_leisure
}