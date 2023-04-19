package com.ds.drag.demo.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ds.drag.core.IDragData
import com.ds.drag.core.SimpleData
import com.ds.drag.core.callback.DragTouchCallback
import com.ds.drag.demo.R
import com.ds.drag.demo.SimpleAdapter
import com.ds.drag.demo.handler.MultiplyHandlerImpl

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/7/20
 * desc   : 合并执行乘法逻辑
 * version: 1.0
 */
class MultiplyActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: SimpleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_handler)

        supportActionBar?.title = "乘法逻辑"

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = SimpleAdapter(this)
        mAdapter.setData(getTestList())
        recyclerView.adapter = mAdapter

        val itemTouchCallback = DragTouchCallback(mAdapter, horizontal = true, vertical = true)
        itemTouchCallback.setDragHandler(MultiplyHandlerImpl(recyclerView, mAdapter))
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(recyclerView)
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