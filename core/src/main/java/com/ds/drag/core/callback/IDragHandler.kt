package com.ds.drag.core.callback

import androidx.recyclerview.widget.RecyclerView

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/7/11
 * desc   : 拖拽处理接口
 * version: 1.0
 */
interface IDragHandler {

    /**
     * 是否可以交换位置
     */
    fun swapPosition(fromPosition: Int, toPosition: Int): Boolean

    /**
     * 交换位置前回调
     */
    fun onBeforeSwap(fromPosition: Int, toPosition: Int)

    /**
     * 交换位置后回调
     */
    fun onAfterSwap(fromPosition: Int, toPosition: Int)

    /**
     * 合并逻辑,返回合并后的文件夹id
     */
    fun onMergeData(fromPosition: Int, toPosition: Int)

    /**
     * 开始拖拽
     */
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder?)

    /**
     * 结束拖拽
     */
    fun onStopDrag(performMerge: Boolean)


}