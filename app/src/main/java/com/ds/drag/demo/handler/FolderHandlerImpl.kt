package com.ds.drag.demo.handler

import android.annotation.SuppressLint
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.ds.drag.core.FolderData
import com.ds.drag.core.IDragData
import com.ds.drag.core.SimpleData
import com.ds.drag.core.callback.IDragHandler
import com.ds.drag.demo.*
import com.ds.drag.demo.SimpleAdapter

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/7/11
 * desc   : 合并成文件夹
 * version: 1.0
 */
class FolderHandlerImpl(private val recyclerView: RecyclerView, private val adapter: SimpleAdapter) : IDragHandler {

    companion object {
        const val TAG = "DragHandlerImpl"
    }

    var mergedListener: ((data: IDragData) -> Unit)? = null

    override fun swapPosition(fromPosition: Int, toPosition: Int): Boolean {
        return true
    }

    override fun onBeforeSwap(fromPosition: Int, toPosition: Int) {

    }

    override fun onAfterSwap(fromPosition: Int, toPosition: Int) {
        recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)
        //修改后的数据回传给原列表
        adapter.mList.forEachIndexed { index, iDragData ->
            if(iDragData is SimpleData){
                Log.d(TAG, "onAfterSwap: $index -> ${(iDragData as SimpleData).value}")
            }
        }

    }

    override fun onMergeData(fromPosition: Int, toPosition: Int) {
        val list: MutableList<IDragData> = adapter.mList
        val fromData = list.getOrNull(fromPosition)
        val toData = list.getOrNull(toPosition)
        if (fromData == null || toData == null) {
            notifyDataSetChanged()
            return
        }
        if (toData is FolderData) {         // 场景1：目标位置是文件夹
            // 目标位置是文件夹，直接添加
            list.remove(fromData)
            // 添加文件夹标识
            val folderId = toData.getFolderId()
            fromData.setFolderId(folderId)
            toData.list.add(fromData)
            notifyDataSetChanged()
            mergedListener?.invoke(toData)
        } else {                            // 场景2：目标位置是文件夹
            // 目标位置不是文件，创建一个文件夹对象
            // 当前时间戳作为文件夹id
            val folderId = System.currentTimeMillis().toString()
            val folderTab = FolderData()
            folderTab.setFolderId(folderId)
            fromData.setFolderId(folderId)
            toData.setFolderId(folderId)
            folderTab.list.add(toData)
            folderTab.list.add(fromData)
            // 添加文件夹数据，将选中的item移除
            list.add(toPosition, folderTab)
            list.remove(toData)
            list.remove(fromData)
            notifyDataSetChanged()
            mergedListener?.invoke(folderTab)
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