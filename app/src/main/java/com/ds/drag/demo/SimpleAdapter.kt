package com.ds.drag.demo

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ds.drag.core.IDragAdapter
import com.ds.drag.core.IDragItem
import kotlinx.android.synthetic.main.item_folder_data_layout.view.*
import kotlinx.android.synthetic.main.item_preview_data_layout.view.*

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/7/12
 * desc   :
 * version: 1.0
 */
class SimpleAdapter(private val context: Context, private val inFolder: Boolean = false) : RecyclerView.Adapter<BaseDataVH>(), IDragAdapter {


    val mList: MutableList<IDragData> = mutableListOf()
    var itemClickListener: ((item: IDragData) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<IDragData>) {
        mList.clear()
        mList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (mList[position]) {
            is SimpleData -> 0
            is FolderData -> 1
            is PreviewData -> 2
            else -> super.getItemViewType(position)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseDataVH {
        val layoutInflater = LayoutInflater.from(context)
        return when (viewType) {
            1 -> {
                val view = layoutInflater.inflate(R.layout.item_folder_data_layout, parent, false)
                FolderViewHolder(view)
            }
            2 -> {
                val view = layoutInflater.inflate(R.layout.item_preview_data_layout, parent, false)
                PreviewViewHolder(view)
            }
            else -> {
                val view = layoutInflater.inflate(R.layout.item_simple_data_layout, parent, false)
                SimpleViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseDataVH, position: Int) {

        if (inFolder) {

            holder.itemView.setBackgroundResource(R.drawable.se_folder_bg)
        }

        holder.bindData(mList[position])
        if (holder is FolderViewHolder) {
            holder.itemView.clk_mask.setOnClickListener {
                itemClickListener?.invoke(mList[position])
            }
        } else {
            holder.itemView.setOnClickListener {
                itemClickListener?.invoke(mList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getDragData(): MutableList<IDragData> {
        return mList
    }

    override fun getDragItem(viewHolder: RecyclerView.ViewHolder?): IDragItem? {
        return viewHolder as? IDragItem
    }
}


abstract class BaseDataVH(itemView: View) : RecyclerView.ViewHolder(itemView), IDragItem {


    abstract fun bindData(data: IDragData)


}

/**
 * 用于显示占位视图
 */
class PreviewViewHolder(itemView: View) : BaseDataVH(itemView) {
    override fun bindData(data: IDragData) {
        val simpleData = (data as? PreviewData)?.realData
        itemView.tv_content.text = simpleData?.value?.toString()
    }

    override fun canDrag(): Boolean {
        return false
    }

    override fun canMerge(): Boolean {
        return false
    }

    override fun acceptMerge(): Boolean {
        return true
    }

    override fun showMergePreview(holder: RecyclerView.ViewHolder?, show: Boolean) {
    }

    override fun showDragState(holder: RecyclerView.ViewHolder?, isCurrentlyActive: Boolean) {
        if (isCurrentlyActive) {
            itemView.alpha = 0.8F
        } else {
            itemView.alpha = 1.0F
        }
    }

}


class SimpleViewHolder(itemView: View) : BaseDataVH(itemView) {

    @SuppressLint("SetTextI18n")
    override fun bindData(data: IDragData) {
        val simpleData = data as SimpleData
        itemView.findViewById<TextView>(R.id.tv_content).text = "${simpleData.value}"
    }

    override fun canDrag(): Boolean {

        return true
    }

    override fun canMerge(): Boolean {
        return true
    }

    override fun acceptMerge(): Boolean {

        return true
    }

    override fun showMergePreview(holder: RecyclerView.ViewHolder?, show: Boolean) {
        itemView.isSelected = show
    }

    override fun showDragState(holder: RecyclerView.ViewHolder?, isCurrentlyActive: Boolean) {
        if (isCurrentlyActive) {
            itemView.alpha = 0.8F
        } else {
            itemView.alpha = 1.0F
        }
    }

}

/**
 * 文件夹ViewHolder
 */
class FolderViewHolder(itemView: View) : BaseDataVH(itemView) {


    override fun bindData(data: IDragData) {
        val folderData = data as FolderData
        val context = itemView.context
        val recyclerView = itemView.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val folderAdapter = FolderAdapter(context)
        folderAdapter.setData(folderData.list)
        recyclerView.adapter = folderAdapter

        showMergePreview(this, false)
    }

    override fun canDrag(): Boolean {

        return true
    }

    override fun canMerge(): Boolean {
        return false
    }

    override fun acceptMerge(): Boolean {

        return true
    }

    override fun showMergePreview(holder: RecyclerView.ViewHolder?, show: Boolean) {
        itemView.isSelected = show
    }

    override fun showDragState(holder: RecyclerView.ViewHolder?, isCurrentlyActive: Boolean) {
        if (isCurrentlyActive) {
            itemView.alpha = 0.8F
        } else {
            itemView.alpha = 1.0F
        }
    }

}

class DiffCallback(private val oldList: List<IDragData>, private val newList: List<IDragData>) : DiffUtil.Callback() {


    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList.getOrNull(oldItemPosition) == newList.getOrNull(newItemPosition)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList.getOrNull(oldItemPosition) == newList.getOrNull(newItemPosition)
    }


}


