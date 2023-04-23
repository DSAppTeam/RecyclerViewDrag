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
import kotlinx.android.synthetic.main.activity_folder.recycler_view_folder

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/7/11
 * desc   : 合并成文件夹
 * version: 1.0
 */
class FolderInnerHandlerImpl(private val recyclerView: RecyclerView,private val adapter: SimpleAdapter, private val listAdapter: SimpleAdapter) : IDragHandler {

    companion object {
        const val TAG = "DragHandlerImpl"
    }

    var mergedListener: ((data: IDragData) -> Unit)? = null

    override fun swapPosition(fromPosition: Int, toPosition: Int): Boolean {
        return recyclerView?.isEnabled
    }

    override fun onBeforeSwap(fromPosition: Int, toPosition: Int) {

    }

    override fun onAfterSwap(fromPosition: Int, toPosition: Int) {
        recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)
        //todo 文件夹展开内的图标换位后更新文件夹预览页面
        var list= (listAdapter.getDragData().get(listAdapter.getDragData().size-1) as FolderData).list
        list.clear()
        list.addAll(adapter.getDragData() as  List<SimpleData>)
        listAdapter.notifyItemChanged(listAdapter.getDragData().size-1);
    }

    override fun onMergeData(fromPosition: Int, toPosition: Int) {
        TODO("Not yet implemented")
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {
        TODO("Not yet implemented")
    }

    override fun onStopDrag(performMerge: Boolean) {
        TODO("Not yet implemented")
    }





}