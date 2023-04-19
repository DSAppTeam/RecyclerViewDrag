package com.ds.drag.demo

import android.graphics.Canvas
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/7/20
 * desc   : 文件夹内的item拖拽回调
 * version: 1.0
 */
class FolderItemDragCallback : ItemTouchHelper.Callback() {

    companion object {
        private val TAG = "FolderItemDragCallback"
    }


    var itemLocationListener: ((viewHolder: RecyclerView.ViewHolder?, left: Float, top: Float, activity: Boolean) -> Unit)? = null

    private var location: IntArray? = null


    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val flag = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        return makeMovementFlags(flag, 0)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {

        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        Log.d("onSwiped", "onSwiped:  "+direction)
    }


    /**
     * 监听拖拽item位置变化
     */
    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val top = viewHolder.itemView.top
        val left = viewHolder.itemView.left
        if (isCurrentlyActive) {
            viewHolder.itemView.alpha = 0.5f
        } else {
            viewHolder.itemView.alpha = 1f
        }
        val parentLocation = getLocation(recyclerView)
        val x = parentLocation[0] + left + dX
        val y = parentLocation[1] + top + dY
        itemLocationListener?.invoke(viewHolder, x, y, isCurrentlyActive)
        Log.d(TAG, "onChildDrawOver: top $x, left $y")
        Log.d(TAG, "onChildDrawOver: actionState："+actionState)
    }


    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.alpha = 1f
    }


    private fun getLocation(recyclerView: RecyclerView): IntArray {
        if (location == null) {
            location = IntArray(2)
            recyclerView.getLocationInWindow(location)
        }
        return location!!
    }


}