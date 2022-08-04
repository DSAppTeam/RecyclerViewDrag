package com.ds.drag.demo

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_folder_item_layout.view.*

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/8/2
 * desc   :
 * version: 1.0
 */
class FolderAdapter(context: Context) : RecyclerView.Adapter<FolderItemVH>() {

    private val layoutInflater = LayoutInflater.from(context)
    private val mList: MutableList<IDragData> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<IDragData>) {
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
        holder.itemView.tv_content.text = (data as? SimpleData)?.value.toString()


    }

    override fun getItemCount(): Int {
        return minOf(mList.size, 4)
    }
}


class FolderItemVH(itemView: View) : RecyclerView.ViewHolder(itemView) {


}

