package com.ds.drag.demo.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ds.drag.core.FolderData
import com.ds.drag.core.IDragData
import com.ds.drag.core.PreviewData
import com.ds.drag.core.SimpleData
import com.ds.drag.core.callback.DragTouchCallback
import com.ds.drag.demo.*
import com.ds.drag.demo.handler.FolderHandlerImpl
import kotlinx.android.synthetic.main.activity_folder.*

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/7/20
 * desc   : 合并生成文件夹逻辑
 * version: 1.0
 */
class FolderActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    // recyclerView 在 Window 中的坐标
    private val location: IntArray by lazy {
        val location = IntArray(2)
        recyclerView.getLocationInWindow(location)
        return@lazy location
    }

    private var previewPosition = -1

    // 左侧列表
    private val listAdapter by lazy { SimpleAdapter(this) }
    // 子列表
    private val folderAdapter by lazy { SimpleAdapter(this, true) }
    private var selectedItem: IDragData? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)
        initSimpleList()
        initFolderList()
    }

    /**
     * 初始化左侧列表
     */
    private fun initSimpleList() {
        recyclerView = findViewById(R.id.grvDesk)
        recyclerView.layoutManager = LinearLayoutManager(this)
        listAdapter.setData(getTestList())
        listAdapter.itemClickListener = { item ->
            selectedItem = item
            val dataList = (item as? FolderData)?.list
            showFolderList(dataList)
        }
        recyclerView.adapter = listAdapter
        val itemTouchCallback = DragTouchCallback(listAdapter, vertical = true, horizontal = false)
        val dragHandler = FolderHandlerImpl(recyclerView, listAdapter)
        // 合并操作回调
        dragHandler.mergedListener = { item ->
            if (item == selectedItem) {
                val dataList = (item as? FolderData)?.list
                showFolderList(dataList)
            }
        }
        itemTouchCallback.setDragHandler(dragHandler)
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(recyclerView)
    }

    /**
     * 初始化文件夹RecyclerView
     */
    private fun initFolderList() {
        grvDeskFolder.layoutManager = LinearLayoutManager(this)
        grvDeskFolder.adapter = folderAdapter
        // 拖拽位置监听，实现将文件夹的item拖回左侧列表
        val itemTouchCallback = FolderItemDragCallback(folderAdapter)
        itemTouchCallback.itemLocationListener = { viewHolder, left, top, activity ->
            Log.d("FolderActivity", "initFolderList: left $left, top $top")
            notifyPreviewViewHolder(viewHolder, left, top, activity)
        }
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(grvDeskFolder)
    }

    /**
     * 用于显示文件夹内的list
     */
    private fun showFolderList(list: List<IDragData>?) {
        if (list != null) {
            folderAdapter.setData(list)
        } else {
            folderAdapter.setData(emptyList())
        }
    }

    /**
     * 根据Y轴方向的位置，找到RecyclerView对应的位置
     */
    private fun findBestPosition(top: Int, recyclerView: RecyclerView): Int {
        val adapterList = listAdapter.mList
        if (adapterList.isEmpty()) {
            return 0
        }

        val linearLayoutManager = (recyclerView.layoutManager as LinearLayoutManager?) ?: return 0
        val firstPosition = linearLayoutManager.findFirstVisibleItemPosition()
        val lastPosition = linearLayoutManager.findLastVisibleItemPosition()
        for (i in firstPosition until lastPosition) {
            val childView = linearLayoutManager.findViewByPosition(i) ?: return 0
            if (childView.top <= top) {
                val nextView = linearLayoutManager.findViewByPosition(i + 1)
                if (nextView == null || nextView.top >= top) {
                    return i
                }
            }
        }
        return 0
    }

    /**
     * 根据拖拽位置，显示、隐藏预览项
     * @param viewHolder 被拖拽的ViewHolder
     * @param left viewHolder相对当前window的left
     * @param top  viewHolder相对当前window的top
     * @param activity 是否被拖动，释放拖动时 activity == false
     */
    private fun notifyPreviewViewHolder(viewHolder: RecyclerView.ViewHolder?, left: Float, top: Float, activity: Boolean) {


        if (activity) {
            val targetLeft = recyclerView.left.toFloat()
            val targetRight = recyclerView.right.toFloat()
            val targetWith = recyclerView.width
            val xStart = targetLeft - targetWith / 4
            val xEnd = targetRight - targetWith / 4
            if (left in xStart..xEnd) {
                val y = top - location[1]
                val position = findBestPosition(y.toInt(), recyclerView)
                updatePreviewPosition(position, viewHolder)
            } else {
                updatePreviewPosition(-1, viewHolder)
            }
        } else {
            replacePreview(viewHolder)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun replacePreview(viewHolder: RecyclerView.ViewHolder?) {
        if (previewPosition >= 0) {
            val fromPosition = viewHolder?.adapterPosition ?: return
            val folderList = folderAdapter.getDragData()
            val data = folderList.removeAt(fromPosition)
            folderAdapter.notifyDataSetChanged()

            val dataList = listAdapter.getDragData()
            dataList.forEach {
                val folderData = it as? FolderData
                folderData?.list?.remove(data)
            }
            // 移除空的文件夹
            dataList.removeAll {
                val folderData = it as? FolderData
                folderData?.list?.isEmpty() ?: false
            }
            dataList.remove(previewData)
            dataList.add(previewPosition, data)
            listAdapter.notifyDataSetChanged()
            previewPosition = -1
        }
    }


    private val previewData = PreviewData()

    /**
     * 更新预览ViewHolder的位置
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun updatePreviewPosition(position: Int, viewHolder: RecyclerView.ViewHolder?) {
        if (previewPosition != position) {
            previewPosition = position
            Log.d("Dodge", "updatePreviewPosition: position = $position")
            if (position >= 0) {
                val fromPosition = viewHolder?.adapterPosition ?: return
                val simpleData = folderAdapter.getDragData().getOrNull(fromPosition) as? SimpleData
                previewData.realData = simpleData
                val dataList = listAdapter.getDragData()
                dataList.remove(previewData)
                dataList.add(position, previewData)
                listAdapter.notifyDataSetChanged()
            } else {
                val dataList = listAdapter.getDragData()
                val oldList = dataList.toList()
                dataList.remove(previewData)
                val result = DiffUtil.calculateDiff(DiffCallback(oldList, dataList))
                result.dispatchUpdatesTo(listAdapter)
            }
        }
    }

    private fun getTestList(): MutableList<IDragData> {
        val list = mutableListOf<IDragData>()
        for (index in 0..50) {
            val data = SimpleData(index)
            list.add(data)
        }
        return list
    }


}