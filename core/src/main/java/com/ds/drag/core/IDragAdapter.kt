package com.ds.drag.core

import androidx.recyclerview.widget.RecyclerView

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/7/11
 * desc   : Adapter接口，接入拖拽功能需要实现该接口
 * version: 1.0
 */
interface IDragAdapter {

    fun getDragData(): List<Any>

    fun getDragItem(viewHolder: RecyclerView.ViewHolder?): IDragItem?


}
