package com.ds.drag.demo.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ds.drag.core.FolderData
import com.ds.drag.core.IDragData
import com.ds.drag.core.PreviewData
import com.ds.drag.core.SimpleData
import com.ds.drag.core.callback.DragTouchCallback
import com.ds.drag.core.callback.DragTouchCallback.Companion.defaultFolderId
import com.ds.drag.demo.*
import com.ds.drag.demo.handler.FolderHandlerImpl
import com.ds.drag.demo.handler.FolderInnerHandlerImpl
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.activity_folder.*
import kotlinx.android.synthetic.main.activity_folder2.blur_view
import kotlinx.android.synthetic.main.activity_folder2.blur_view2


/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/7/20
 * desc   : 合并生成文件夹逻辑
 * version: 1.0
 */
class FolderActivity2 : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    // recyclerView 在 Window 中的坐标
    private val location: IntArray by lazy {
        val location = IntArray(2)
        recyclerView.getLocationInWindow(location)
        return@lazy location
    }
    private val folderLocation: IntArray by lazy {
        val location = IntArray(2)
        recycler_view_folder.getLocationInWindow(location)
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
        setContentView(R.layout.activity_folder2)
        initSimpleList()
        initFolderList()
        blur_view.visibility=View.GONE
    }


    /**
     * 初始化左侧列表
     */
    private fun initSimpleList() {
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this,4,  LinearLayoutManager.VERTICAL,false)
        listAdapter.setData(getTestList())
        listAdapter.itemClickListener = { item ->
//            if(isExpandFolder){
//                isExpandFolder(false)
//                blur_view.visibility=View.GONE
//            }else{
                if(item is FolderData){
                    selectedItem = item
                    val dataList = (item as? FolderData)?.list
                    showFolderList(dataList)
                }
          //  }
        }
        blur_view.setOnClickListener {
           // isExpandFolder(false)
            blur_view.visibility=View.GONE }
        recyclerView.adapter = listAdapter
        val itemTouchCallback = DragTouchCallback(listAdapter, vertical = true, horizontal = true)
        val dragHandler = FolderHandlerImpl(recyclerView, listAdapter)
        // 合并操作回调
        dragHandler.mergedListener = { item ->
            if (item == selectedItem) {
                val dataList = (item as? FolderData)?.list
                //showFolderList(dataList)
            }
        }

        itemTouchCallback.setDragHandler(dragHandler)
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(recyclerView)
    }

    /**
     * 初始化文件夹RecyclerView
     */
    private fun initFolderList() {
        recycler_view_folder.layoutManager = GridLayoutManager(this,3,  LinearLayoutManager.VERTICAL,false)
        recycler_view_folder.adapter = folderAdapter
        // 拖拽位置监听，实现将文件夹的item拖回左侧列表
        val itemTouchCallback = FolderItemDragCallback(folderAdapter)
        itemTouchCallback.itemLocationListener = { viewHolder, left, top, activity ->
            Log.d("FolderActivity", "initFolderList: left $left, top $top")
            notifyPreviewViewHolder(viewHolder, left, top, activity)
        }
        val dragHandler = FolderInnerHandlerImpl(recycler_view_folder, folderAdapter,listAdapter)
        itemTouchCallback.setDragHandler(dragHandler)
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(recycler_view_folder)
    }
    /**
     * 用于显示文件夹内的list
     */
    private fun showFolderList(list: List<IDragData>?) {
        blur_view.visibility=View.VISIBLE
        if (list != null) {
            folderAdapter.setData(list)
        } else {
            folderAdapter.setData(emptyList())
        }
        val radius = 5f
        val decorView = window.decorView
        // ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        // ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        val rootView = decorView.findViewById<View>(android.R.id.content) as ViewGroup
        val windowBackground = decorView.background
        blur_view.setupWith(rootView, RenderScriptBlur(this)) // or RenderEffectBlur
            .setFrameClearDrawable(windowBackground) // Optional
            .setBlurAutoUpdate(true)

            .setBlurRadius(radius)

        blur_view2.setupWith(rootView, RenderScriptBlur(this)) // or RenderEffectBlur
            .setFrameClearDrawable(windowBackground) // Optional
            .setBlurAutoUpdate(true)
            .setBlurRadius(25f)

        isExpandFolder(true)
//        val dialogLayer = AnyLayer.dialog(this)
//        dialogLayer.contentView(R.layout.dialog_sign_up_for_login_tips)
//            .gravity(Gravity.CENTER)
//            .doBindData {
//                val recycler_view_folder = findViewById<RecyclerView>(R.id.recycler_view_folder)
//                recycler_view_folder?.layoutManager = LinearLayoutManager(this.activity)
//                recycler_view_folder?.adapter = folderAdapter
//                // 拖拽位置监听，实现将文件夹的item拖回左侧列表
//                val itemTouchCallback = FolderItemDragCallback()
//                itemTouchCallback.itemLocationListener = { viewHolder, left, top, activity ->
//                    Log.d("FolderActivity", "initFolderList: left $left, top $top")
//                    notifyPreviewViewHolder(viewHolder, left, top, activity)
//                }
//                ItemTouchHelper(itemTouchCallback).attachToRecyclerView(recycler_view_folder)
//
//            }
//            .show()
    }

    override fun onBackPressed() {
        blur_view?.visibility = View.GONE
       // super.onBackPressed()
    }
    /**
     * 根据Y轴方向的位置，找到RecyclerView对应的位置
     */
    private fun findBestPosition(left: Int,top: Int, recyclerView: RecyclerView): Int {
        val adapterList = listAdapter.mList
        if (adapterList.isEmpty()) {
            return 0
        }
        val linearLayoutManager = (recyclerView.layoutManager as LinearLayoutManager?) ?: return 0
        val firstPosition = linearLayoutManager.findFirstVisibleItemPosition()
        val lastPosition = linearLayoutManager.findLastVisibleItemPosition()
        for (i in firstPosition until lastPosition) { //遍历所有可见的view,
            Log.d("firstPosition:lastPosition", "$firstPosition:$lastPosition")
            val childView = linearLayoutManager.findViewByPosition(i) ?: return 0
            Log.d("findBestPosition", "childView.top: ${childView.top}, top: $top, childView.left: ${childView.left},  left: $left")
            //逐一比对,在满足条件的view后面插入
            if (childView.bottom <= top || childView.left <= left){
                val nextView = linearLayoutManager.findViewByPosition(i + 1)
                if (nextView == null || (nextView.bottom >= top && nextView.left >= left)) {
                    Log.d("findBestPosition", i.toString())
                    return i
                }
            }
        }
        return 0
    }
    //当前拖拽的文件夹postion
    var dragoutFolderPid=-1;
    var isExpandFolder=false;
    /**
     * 根据拖拽位置，显示、隐藏预览项
     * @param viewHolder 被拖拽的ViewHolder
     * @param left viewHolder相对当前window的left
     * @param top  viewHolder相对当前window的top
     * @param activity 是否被拖动，释放拖动时 activity == false
     */
    private fun notifyPreviewViewHolder(viewHolder: RecyclerView.ViewHolder?, left: Float, top: Float, activity: Boolean) {

        Log.d("notifyPreviewViewHolder2", "left:"+left)
        Log.d("notifyPreviewViewHolder2", "top:"+dragoutFolderPid)
        if (activity) {
            val targetLeft = recyclerView.left.toFloat()
            val targetRight = recyclerView.right.toFloat()
            val targetWith = recyclerView.width.toFloat() //文件夹内的图标将要移出的目标
            val targetHeight = recyclerView.height.toFloat() //文件夹内的图标将要移出的目标
            val xStart = targetLeft - targetWith / 4
            val xEnd = targetRight - targetWith / 4
            var itemwidth= viewHolder!!.itemView.width.toFloat()
            var itemHeight= viewHolder!!.itemView.height.toFloat()
            if(dragoutFolderPid!=-1){
                if(left in xStart..xEnd){
                    val x = left - location[0]
                    val y = top - location[1]

                    val position = findBestPosition(x.toInt(),y.toInt(), recyclerView)
                 //   Log.d("notifyPreviewViewHolderfindBestPosition", "position:"+position)

                    updatePreviewPosition(position, viewHolder)
                }else{
                    updatePreviewPosition(-1, viewHolder) //文件夹列表内
                }
            }else{
              //  Log.d("notifyPreviewViewHolderfindBestPosition", "top:"+(blur_view2.get ))
              //  Log.d("notifyPreviewViewHolderfindBestPosition", "viewtop:"+(top ))
                val x = folderLocation[0]
                val y = folderLocation[1]

                if (left in 0f..(x - (itemwidth /3*1))
                    ||
                    left  in (x+recycler_view_folder.width- (itemwidth /3*2))..targetWith
                    ||
                    top in 0f..(y - (itemHeight /3*1))
                    ||
                    top  in (y +recycler_view_folder.height - (itemHeight/3*2))..targetHeight
                ){ //拖到外部文件
                    val x = left - location[0]
                    val y = top - location[1]
                    val position = findBestPosition(x.toInt(),y.toInt(), recyclerView)
                  //  Log.d("notifyPreviewViewHolderfindBestPosition", "position:"+position)
                    updatePreviewPosition(position, viewHolder)
                    dragoutFolderPid= viewHolder?.layoutPosition!!;
                    isExpandFolder(false);
                }else{
                    updatePreviewPosition(-1, viewHolder) //文件夹列表内
                }
            }

        } else {//拖拽结束
            if(dragoutFolderPid!=-1){
                blur_view?.visibility=View.GONE
            }
            dragoutFolderPid=-1;
            replacePreview(viewHolder)
        }
        //isDragoutFolder=activity;
    }
    fun isExpandFolder(isExpandFolder:Boolean){
        recycler_view_folder?.visibility=View.VISIBLE
        this.isExpandFolder=isExpandFolder;
        recycler_view_folder?.isEnabled=isExpandFolder
        recycler_view_folder?.background = isExpandFolder.let {
            if (isExpandFolder) {
                ColorDrawable(Color.WHITE).apply {
                alpha = 180
                }
            } else {
                ColorDrawable(Color.WHITE).apply {
                    alpha = 0
                }
            }
        }
        folderAdapter.isSHow=isExpandFolder;
        folderAdapter.mList.forEachIndexed { index, iDragData ->
            Log.d("dragoutFolderPid", "dragoutFolderPid:"+dragoutFolderPid)
            if(index!=dragoutFolderPid){
                folderAdapter.notifyItemChanged(index)
            }else{
            }
        }
        if (isExpandFolder){
            blur_view.setBlurEnabled(true)
            blur_view2.setBlurEnabled(true)
        }else{
            blur_view.setBlurEnabled(false)
            blur_view2.setBlurEnabled(false)
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
//            dataList.removeAll {
//                val folderData = it as? FolderData
//                folderData?.list?.isEmpty() ?: false
//            }
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
        val size=20
        for (index in 0..size) {
            if (index==size) {
               var l= FolderData();
                l.setFolderId(defaultFolderId)
                list.add(l)
            }else{
                list.add(SimpleData(index))
            }
        }

        return list
    }


}