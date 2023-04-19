package com.ds.drag.demo.handler

import android.annotation.SuppressLint
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.ds.drag.core.IDragData
import com.ds.drag.core.SimpleData
import com.ds.drag.core.callback.IDragHandler
import com.ds.drag.demo.SimpleAdapter

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/7/11
 * desc   : 加法逻辑
 * version: 1.0
 */
class AdditionHandlerImpl(private val recyclerView: RecyclerView, private val adapter: SimpleAdapter) : IDragHandler {

    companion object {
        const val TAG = "DragHandlerImpl"
    }


    override fun swapPosition(fromPosition: Int, toPosition: Int): Boolean {
        return true
    }

    override fun onBeforeSwap(fromPosition: Int, toPosition: Int) {

    }

    override fun onAfterSwap(fromPosition: Int, toPosition: Int) {
        recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)
    }

    override fun onMergeData(fromPosition: Int, toPosition: Int) {
        val list: MutableList<IDragData> = adapter.mList
        val fromData = list.getOrNull(fromPosition)
        val toData = list.getOrNull(toPosition)
        if (fromData == null || toData == null) {
            notifyDataSetChanged()
            return
        }

        val value1 = (fromData as? SimpleData)?.value
        val value2 = (toData as? SimpleData)?.value
        if (value1 != null && value2 != null) {
            val newValue = value1 + value2
            val data = SimpleData(newValue)
            list.add(toPosition, data)
            list.remove(toData)
            list.remove(fromData)
            notifyDataSetChanged()
        } else {
            notifyDataSetChanged()
        }
        Log.d("MergeHandlerImpl", "onMergeData: 合并 $fromPosition and $toPosition")
    }

    /**
     * 开始拖拽
     */
    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {
        Log.d(TAG, "onStartDrag: ")
    }

    /**
     * 结束拖拽
     */
    override fun onStopDrag(performMerge: Boolean) {
        Log.d(TAG, "onStopDrag: 执行了合并操作 = $performMerge")
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun notifyDataSetChanged() {
        recyclerView.post {
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }


}