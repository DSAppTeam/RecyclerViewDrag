package com.ds.drag.demo.activity

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ds.drag.demo.IDragData
import com.ds.drag.demo.R
import com.ds.drag.demo.SimpleAdapter
import com.ds.drag.demo.SimpleData

/**
 * author : linzheng
 * e-mail : linzheng@corp.netease.com
 * time   : 2022/11/8
 * desc   :
 * version: 1.0
 */
class TestDragActivity : AppCompatActivity() {

    private val recyclerView: RecyclerView by lazy { findViewById(R.id.recycler_view) }
    private val mAdapter: SimpleAdapter by lazy { SimpleAdapter(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_handler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter.setData(getTestList())
        recyclerView.adapter = mAdapter

        ItemTouchHelper(TestDragCallback(mAdapter)).attachToRecyclerView(recyclerView)
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


class TestDragCallback(val adapter: SimpleAdapter) : ItemTouchHelper.Callback() {

    private val paint by lazy {
        val paint = Paint()
        paint.color = Color.GREEN
        return@lazy paint

    }


    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val flag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(flag, 0)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        val list = adapter.getDragData()
        val item = list.removeAt(fromPosition)
        list.add(toPosition, item)
        adapter.notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun isLongPressDragEnabled(): Boolean = true

    override fun isItemViewSwipeEnabled(): Boolean = false

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        Log.d("Dodge", "onChildDraw: isCurrentlyActive = $isCurrentlyActive , actionState = $actionState")
        if (isCurrentlyActive) {
            val top = viewHolder.itemView.top.toFloat()
            val left = viewHolder.itemView.left.toFloat()
            val right = viewHolder.itemView.right.toFloat()
            val bottom = viewHolder.itemView.bottom.toFloat()
            c.drawRect(RectF(left, top, right, bottom), paint)
        }
    }


    override fun onChildDrawOver(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        Log.d("Dodge", "onChildDrawOver: isCurrentlyActive = $isCurrentlyActive")
    }


}

