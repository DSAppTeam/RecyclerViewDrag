package com.ds.drag.demo.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ds.drag.core.callback.DragTouchCallback
import com.ds.drag.demo.IDragData
import com.ds.drag.demo.R
import com.ds.drag.demo.SimpleAdapter
import com.ds.drag.demo.SimpleData
import com.ds.drag.demo.handler.AdditionHandlerImpl

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/7/20
 * desc   : 合并执行加法逻辑
 * version: 1.0
 */
class AdditionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: SimpleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_handler)
        supportActionBar?.title = "加法逻辑"

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = SimpleAdapter(this)
        mAdapter.setData(getTestList())
        recyclerView.adapter = mAdapter

        val itemTouchCallback = DragTouchCallback(mAdapter)
        itemTouchCallback.setDragHandler(AdditionHandlerImpl(recyclerView, mAdapter))
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