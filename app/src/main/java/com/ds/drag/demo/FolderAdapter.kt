package com.ds.drag.demo

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ds.drag.core.IDragData
import com.ds.drag.core.SimpleData
import kotlinx.android.synthetic.main.item_folder_item_layout.view.*
import kotlinx.android.synthetic.main.item_simple_data_layout.view.image

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/8/2
 * desc   :
 * version: 1.0
 */
class FolderAdapter(context: Context) : RecyclerView.Adapter<FolderItemVH>() {

    private val layoutInflater = LayoutInflater.from(context)
    private val mList: MutableList<SimpleData> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<SimpleData>) {
        mList.clear()
        mList.addAll(data)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderItemVH {
        val view = layoutInflater.inflate(R.layout.item_folder_item_layout, parent, false)
        return FolderItemVH(view)
    }

    override fun onBindViewHolder(holder: FolderItemVH, position: Int) {
        val data = mList[position]
        holder.itemView.image.setImageResource(data.iconResId!!)
       holder.itemView.tv_content.visibility = View.GONE


    }

    override fun getItemCount(): Int {
        return minOf(mList.size, 100)
    }
}


class FolderItemVH(itemView: View) : RecyclerView.ViewHolder(itemView) {


}

