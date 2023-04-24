package com.ds.drag.demo.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_folder2.deskBlurView
import kotlinx.android.synthetic.main.activity_folder2.deskFolderBlurView
import java.util.concurrent.CopyOnWriteArrayList


/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/7/20
 * desc   : 合并生成文件夹逻辑
 * version: 1.0
 */
class FolderActivity2 : AppCompatActivity() {

    private lateinit var grvDesk: RecyclerView
    // recyclerView 在 Window 中的坐标
    private val location: IntArray by lazy {
        val location = IntArray(2)
        grvDesk.getLocationInWindow(location)
        return@lazy location
    }
    private val folderLocation: IntArray by lazy {
        val location = IntArray(2)
        grvDeskFolder.getLocationInWindow(location)
        return@lazy location
    }
    private var previewPosition = -1

    // 左侧列表
    private val deskAdapter by lazy { SimpleAdapter(this) }
    // 子列表
    private val deskFolderAdapter by lazy { SimpleAdapter(this, true) }
    private var selectedItem: IDragData? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder2)
        initSimpleList()
        initFolderList()
        deskBlurView.visibility=View.GONE
    }

    var spancount=4;
    /**
     * 初始化左侧列表
     */
    private fun initSimpleList() {
        grvDesk = findViewById(R.id.grvDesk)
        grvDesk.layoutManager = GridLayoutManager(this,spancount,  LinearLayoutManager.VERTICAL,false)
        deskAdapter.setData(getTestList())
        deskAdapter.itemClickListener = { item ->
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
        deskBlurView.setOnClickListener {
           // isExpandFolder(false)
            deskBlurView.visibility=View.GONE }
        grvDesk.adapter = deskAdapter
        val itemTouchCallback = DragTouchCallback(deskAdapter, vertical = true, horizontal = true)
        val dragHandler = FolderHandlerImpl(grvDesk, deskAdapter)
        // 合并操作回调
        dragHandler.mergedListener = { item ->
            if (item == selectedItem) {
                val dataList = (item as? FolderData)?.list
                //showFolderList(dataList)
            }
        }

        itemTouchCallback.setDragHandler(dragHandler)
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(grvDesk)
    }

    /**
     * 初始化文件夹RecyclerView
     */
    private fun initFolderList() {
        grvDeskFolder.layoutManager = GridLayoutManager(this,3,  LinearLayoutManager.VERTICAL,false)
        grvDeskFolder.adapter = deskFolderAdapter
        // 拖拽位置监听，实现将文件夹的item拖回左侧列表
        val itemTouchCallback = FolderItemDragCallback(deskFolderAdapter)
        itemTouchCallback.itemLocationListener = { viewHolder, left, top, activity ->
            Log.d("FolderActivity", "initFolderList: left $left, top $top")
            notifyPreviewViewHolder(viewHolder, left, top, activity)
        }
        val dragHandler = FolderInnerHandlerImpl(grvDeskFolder, deskFolderAdapter,deskAdapter)
        itemTouchCallback.setDragHandler(dragHandler)
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(grvDeskFolder)
    }
    /**
     * 用于显示文件夹内的list
     */
    private fun showFolderList(list: List<IDragData>?) {
        deskBlurView.visibility=View.VISIBLE
        if (list != null) {
            deskFolderAdapter.setData(list)
        } else {
            deskFolderAdapter.setData(emptyList())
        }
        val radius = 5f
        val decorView = window.decorView
        // ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        // ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        val rootView = decorView.findViewById<View>(android.R.id.content) as ViewGroup
        val windowBackground = decorView.background
        deskBlurView.setupWith(rootView, RenderScriptBlur(this)) // or RenderEffectBlur
            .setFrameClearDrawable(windowBackground) // Optional
            .setBlurAutoUpdate(true)
            .setBlurRadius(radius)

        deskFolderBlurView.setupWith(rootView, RenderScriptBlur(this)) // or RenderEffectBlur
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
        deskBlurView?.visibility = View.GONE
       // super.onBackPressed()
    }
    /**
     * 根据 X、Y轴方向的位置，找到RecyclerView对应的位置
     */
    private fun findBestPosition(left: Int,top: Int, recyclerView: RecyclerView): Int {
        val adapterList = deskAdapter.mList
        if (adapterList.isEmpty()) {
            return 0
        }
        val linearLayoutManager = (recyclerView.layoutManager as LinearLayoutManager?) ?: return 0
        val firstPosition = linearLayoutManager.findFirstVisibleItemPosition()
        val lastPosition = linearLayoutManager.findLastVisibleItemPosition()
        for (i in firstPosition until lastPosition) { //遍历所有可见的view,
           val childView = linearLayoutManager.findViewByPosition(i) ?: return 0
            //逐一比对,在满足条件的view后面插入
            if (childView?.bottom!! <= top || childView?.left!! <= left){
                var nextViewPos=i + 1
                val nextView = linearLayoutManager.findViewByPosition(nextViewPos)
                if(nextView == null ){
                    return i
                }else {
                    if (nextViewPos % spancount == 0) { //grid布局的话,每行的最后一个要单独处理
                        if(nextView.top >= top &&  nextView?.left==0 ){
                            return i
                        }
                    }else{
                        if (nextView.bottom >= top && (nextView.left >= left)) {
                            return i
                        }
                    }
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

        if (activity) {
            val targetLeft = grvDesk.left.toFloat()
            val targetRight = grvDesk.right.toFloat()
            val targetTop = grvDesk.top.toFloat()
            val targetBottom = grvDesk.bottom.toFloat()
            val targetWith = grvDesk.width.toFloat() //文件夹内的图标将要移出的目标
            val targetHeight = grvDesk.height.toFloat() //文件夹内的图标将要移出的目标
            val xStart = targetLeft - targetWith / 4
            val xEnd = targetRight - targetWith / 4

            val yStart = targetTop - targetHeight / 4
            val yEnd = targetBottom - targetWith / 4
            var itemwidth= viewHolder!!.itemView.width.toFloat()
            var itemHeight= viewHolder!!.itemView.height.toFloat()
            if(dragoutFolderPid!=-1){ //从文件夹拖出来以后
                Log.d("notifyPreviewViewHolder", "文件夹内的图标在文件夹 外 拖动")
                if(left in xStart..xEnd+itemwidth || top in yStart..yEnd+itemHeight){
                    //打印left  xStart xEnd 的log
                    Log.d("notifyPreviewViewHolder", "left:"+left)
                    val x = left - location[0]
                    val y = top - location[1]
                    val position = findBestPosition(x.toInt(),y.toInt(),
                        grvDesk
                    )
                 //   Log.d("notifyPreviewViewHolderfindBestPosition", "position:"+position)

                    updatePreviewPosition(position, viewHolder)
                }else{
                    Log.d("notifyPreviewViewHolder", xEnd.toString())
                    updatePreviewPosition(-1, viewHolder) //文件夹列表内
                }
            }else{ //在文件夹中拖动
                Log.d("notifyPreviewViewHolder", "文件夹内的图标在文件夹 内 拖动")
              //  Log.d("notifyPreviewViewHolderfindBestPosition", "top:"+(blur_view2.get ))
              //  Log.d("notifyPreviewViewHolderfindBestPosition", "viewtop:"+(top ))
                val x = folderLocation[0]
                val y = folderLocation[1]

                if (left in 0f..(x - (itemwidth /3*1))
                    ||
                    left  in (x+grvDeskFolder.width- (itemwidth /3*2))..targetWith
                    ||
                    top in 0f..(y - (itemHeight /3*1))
                    ||
                    top  in (y +grvDeskFolder.height - (itemHeight/3*2))..targetHeight
                ){ //拖到外部文件
                    val x = left - location[0]
                    val y = top - location[1]
                    val position = findBestPosition(x.toInt(),y.toInt(),
                        grvDesk
                    )
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
                deskBlurView?.visibility=View.GONE
            }
            dragoutFolderPid=-1;
            replacePreview(viewHolder)
        }
        //isDragoutFolder=activity;
    }
    fun isExpandFolder(isExpandFolder:Boolean){
        grvDeskFolder?.visibility=View.VISIBLE
        this.isExpandFolder=isExpandFolder;
        grvDeskFolder?.isEnabled=isExpandFolder
        grvDeskFolder?.background = isExpandFolder.let {
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
        deskFolderAdapter.isSHow=isExpandFolder;
        deskFolderAdapter.mList.forEachIndexed { index, iDragData ->
            Log.d("dragoutFolderPid", "dragoutFolderPid:"+dragoutFolderPid)
            if(index!=dragoutFolderPid){
                deskFolderAdapter.notifyItemChanged(index)
            }else{
            }
        }
        if (isExpandFolder){
            deskBlurView.setBlurEnabled(true)
            deskFolderBlurView.setBlurEnabled(true)
        }else{
            deskBlurView.setBlurEnabled(false)
            deskFolderBlurView.setBlurEnabled(false)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun replacePreview(viewHolder: RecyclerView.ViewHolder?) {
        if (previewPosition >= 0) {
            val fromPosition = viewHolder?.adapterPosition ?: return
            val folderList = deskFolderAdapter.getDragData()
            val data = folderList.removeAt(fromPosition)
            deskFolderAdapter.notifyDataSetChanged()

            val dataList = deskAdapter.getDragData()
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
            deskAdapter.notifyDataSetChanged()
            previewPosition = -1
        }
    }


    private val previewData = PreviewData()

    /**
     * 更新预览ViewHolder的位置
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun updatePreviewPosition(position: Int, viewHolder: RecyclerView.ViewHolder) {
        if (previewPosition != position) {
            previewPosition = position
            Log.d("Dodge", "updatePreviewPosition: position = $position")
            if (position >= 0) {
                val fromPosition = viewHolder.adapterPosition
                val simpleData = deskFolderAdapter.getDragData().getOrNull(fromPosition) as? SimpleData
                previewData.realData = simpleData
                val dataList = deskAdapter.getDragData()
                dataList.remove(previewData)
                dataList.add(position, previewData)
                deskAdapter.notifyDataSetChanged()
            } else {
                val dataList = deskAdapter.getDragData()
                val oldList = dataList.toList()
                dataList.remove(previewData)
                val result = DiffUtil.calculateDiff(DiffCallback(oldList, dataList))
                result.dispatchUpdatesTo(deskAdapter)
            }
        }
    }

    private fun getTestList(): MutableList<IDragData> {
        val list = mutableListOf<IDragData>()

        var simpleData=SimpleData(1)
        simpleData.iconResId=R.drawable.gzhb
        simpleData.titleName="工作汇报"
        simpleData.unreadcount=0
        list.add(simpleData)

         simpleData=SimpleData(2)
        simpleData.iconResId=R.drawable.gztz
        simpleData.titleName="工作通知"
        simpleData.unreadcount=1
        list.add(simpleData)

         simpleData=SimpleData(3)
        simpleData.iconResId=R.drawable.spsx
        simpleData.titleName="审批事项"
        simpleData.unreadcount=5
        list.add(simpleData)

         simpleData=SimpleData(4)
        simpleData.iconResId=R.drawable.kqdk
        simpleData.titleName="考勤打卡"
        simpleData.unreadcount=12
        list.add(simpleData)

         simpleData=SimpleData(5)
        simpleData.iconResId=R.drawable.rwmb
        simpleData.titleName="任务目标"
        simpleData.unreadcount=13
        list.add(simpleData)

         simpleData=SimpleData(6)
        simpleData.iconResId=R.drawable.wdyp
        simpleData.titleName="我的云盘"
        simpleData.unreadcount=12
        list.add(simpleData)

         simpleData=SimpleData(7)
        simpleData.iconResId=R.drawable.txl
        simpleData.titleName="通讯录"
        simpleData.unreadcount=11
        list.add(simpleData)

         simpleData=SimpleData(8)
        simpleData.iconResId=R.drawable.zdlc
        simpleData.titleName="制度流程"
        simpleData.unreadcount=1
        list.add(simpleData)

         simpleData=SimpleData(9)
        simpleData.iconResId=R.drawable.glbm
        simpleData.titleName="关联部门"
        simpleData.unreadcount=2
        list.add(simpleData)



        var l= FolderData();
        l.setFolderId(defaultFolderId)
        l.list.addAll(convertData())
        list.add(l)

        return list
    }

    fun convertData(): CopyOnWriteArrayList<SimpleData>  {
        val da =CopyOnWriteArrayList<SimpleData>()
        var simpleData=SimpleData(10)
        simpleData.iconResId=R.drawable.glyg
        simpleData.titleName="关联员工"
        simpleData.unreadcount=23
        da.add(simpleData)

        simpleData=SimpleData(11)
        simpleData.iconResId=R.drawable.bmlxd
        simpleData.titleName="部门联系单"
        simpleData.unreadcount=0
        da.add(simpleData)

        simpleData=SimpleData(12)
        simpleData.iconResId=R.drawable.qtsx
        simpleData.titleName="其他事项"
        simpleData.unreadcount=11
        da.add(simpleData)
        return da
    }


}