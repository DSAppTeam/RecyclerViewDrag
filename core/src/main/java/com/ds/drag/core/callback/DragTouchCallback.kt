package com.ds.drag.core.callback

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.RecyclerView
import com.ds.drag.core.FolderData
import com.ds.drag.core.IDragAdapter
import com.ds.drag.core.IDragData
import com.ds.drag.core.IDragItem
import kotlin.math.abs

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/4/14
 * desc   : RecyclerView 拖拽实现，支持Item 合并操作
 * 原理：
 * 1：chooseDropTarget 回调当偏移量处于(-winnerScore，winnerScore)之间时，认为可以触发合并操作，此时记录合并的选中项（mergeSelected）和目标项（mergeTarget）。
 * 2：onSelectedChanged 方法中判断出拖拽停止的操作，如果 mergeSelected 和 mergeTarget 的值不为null，则触发合并操作。
 *
 * @param dragAdapter IDragAdapter
 * @param horizontal 是否支持垂直方向
 * @param vertical 是否支持水平方向
 *
 * version: 1.0
 */
@Suppress("DEPRECATION")
class DragTouchCallback(
    private val dragAdapter: IDragAdapter,
    private val vertical: Boolean = true,
    private val horizontal: Boolean = true
) : ItemTouchHelper.Callback() {

    companion object {
        const val defaultFolderId = "闲置功能"
        const val TAG = "DragTouchCallback"
    }
    // 合并逻辑的处理
    private var mDragHandler: IDragHandler? = null

    // 被选中的合并
    private var mergeSelected: RecyclerView.ViewHolder? = null
    private var mergeTarget: RecyclerView.ViewHolder? = null

    fun setDragHandler(handler: IDragHandler) {
        this.mDragHandler = handler
    }

    override fun isLongPressDragEnabled(): Boolean = true

    override fun isItemViewSwipeEnabled(): Boolean = false


    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragItem = dragAdapter.getDragItem(viewHolder)
        val canDrag = dragItem?.canDrag(viewHolder) ?: false
        var dragFlags = 0
        if (canDrag) {
            if (vertical) {
                dragFlags = dragFlags.or(ItemTouchHelper.UP or ItemTouchHelper.DOWN)
            }
            if (horizontal) {
                dragFlags = dragFlags.or(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            }
            if(vertical && horizontal){
                dragFlags = dragFlags.or(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            }
        }
        val swipeFlags = 0
//        if((recyclerView.adapter as IDragAdapter).getDragData().get(viewHolder.adapterPosition) is FolderData){
//            return makeMovementFlags(0, 0)
//        }else{
            return makeMovementFlags(dragFlags, swipeFlags)
//        }
    }

    override fun getAnimationDuration(recyclerView: RecyclerView, animationType: Int, animateDx: Float, animateDy: Float): Long {
        if (mergeTarget != null && mergeSelected != null) {
            return 0L
        }
        return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy)
    }

    private val winnerSetX = mutableSetOf<RecyclerView.ViewHolder>()
    private val winnerSetY = mutableSetOf<RecyclerView.ViewHolder>()

    /**
     * 暂时只处理 Y 轴方向的逻辑
     */
    override fun chooseDropTarget(selected: RecyclerView.ViewHolder, dropTargets: MutableList<RecyclerView.ViewHolder>, curX: Int, curY: Int): RecyclerView.ViewHolder? {
        val right = curX + selected.itemView.width
        val bottom = curY + selected.itemView.height
        var winner: RecyclerView.ViewHolder? = null
        // winnerScore 一个阈值，当超过阈值触发item位置变化，处于 （-winnerScore, winnerScore）区间触发合并成文件夹
        var winnerScoreY = (selected.itemView.height * 0.3).toInt()
        var winnerScoreX = (selected.itemView.width * 0.3).toInt()
        val dx = curX - selected.itemView.left
        val dy = curY - selected.itemView.top
        val targetsSize = dropTargets.size
        for (i in 0 until targetsSize) {
            val target = dropTargets[i]
            // 处理了Y方向的逻辑
            if (dy < 0) {
                val diff = target.itemView.top - curY
                if (diff in -winnerScoreY..winnerScoreY) {
                    Log.d(TAG, "chooseDropTarget: y 满足条件， target = ${target.adapterPosition}")
                    winnerSetY.add(target)
                } else {
                    winnerSetY.remove(target)
                    if (diff > 0 && target.itemView.top < selected.itemView.top) {
                        val score = abs(diff)
                        if (score > winnerScoreY) {
                            winnerScoreY = score
                            winner = target
                        }
                    }
                }
            }
            if (dy > 0) {
                val diff = target.itemView.bottom - bottom
                if (diff in -winnerScoreY..winnerScoreY) {
                    Log.d(TAG, "chooseDropTarget: y 满足条件， target = ${target.adapterPosition}")
                    winnerSetY.add(target)
                } else {
                    winnerSetY.remove(target)
                    if (diff < 0 && target.itemView.bottom > selected.itemView.bottom) {
                        val score = abs(diff)
                        if (score > winnerScoreY) {
                            winnerScoreY = score
                            winner = target
                        }
                    }
                }
            }

            if (dx > 0) {
                val diff: Int = target.itemView.right - right
                if (diff in -winnerScoreX..winnerScoreX) {
                    Log.d(TAG, "chooseDropTarget: x 满足条件， target = ${target.adapterPosition}")
                    winnerSetX.add(target)
                } else {
                    winnerSetX.remove(target)
                    if (diff < 0 && target.itemView.right > selected.itemView.right) {
                        val score = abs(diff)
                        if (score > winnerScoreX) {
                            winnerScoreX = score
                            winner = target
                        }
                    }
                }
            }

            if (dx < 0) {
                val diff = target.itemView.left - curX
                if (diff in -winnerScoreX..winnerScoreX) {
                    Log.d(TAG, "chooseDropTarget: x 满足条件， target = ${target.adapterPosition}")
                    winnerSetX.add(target)
                } else {
                    winnerSetX.remove(target)
                    if (diff > 0 && target.itemView.left < selected.itemView.left) {
                        val score = abs(diff)
                        if (score > winnerScoreX) {
                            winnerScoreX = score
                            winner = target
                        }
                    }
                }
            }
        }
        findMergeTarget(selected)
        return winner
    }

    /**
     * 找到满足合并条件的ViewHolder
     */
    private fun findMergeTarget(selected: RecyclerView.ViewHolder) {
        val target = when {
            // 同时满足x、y轴法相的ViewHolder
            horizontal && vertical -> winnerSetX.find { winnerSetY.contains(it) }
            // 满足y轴法相的ViewHolder
            vertical -> winnerSetY.firstOrNull()
            // 满足x轴法相的ViewHolder
            horizontal -> winnerSetX.firstOrNull()
            // default
            else -> null
        }
        Log.d(TAG, "findMergeTarget: position = ${target?.adapterPosition}")
        if (target != null) {
            /**
             * 1. 判断当前拖入的图标类型，如果是文件夹类型，则不触发合并
             *  {@link #com.ds.drag.core.callback.DragTouchCallback.defaultFolderId}
             */
            //判断当前拖入的图标类型，如果是文件夹类型，则不触发合并
            var targetItem=   dragAdapter.getDragData().get(target.adapterPosition);
            if(targetItem is FolderData){ //拖入目标是文件夹.目前只能拖入默认创建的文件夹
              //  if( (target as FolderData ).getFolderId().equals(defaultFolderId)){ //
                    onStashMergeHolder(selected, target)
              //  }
            }

        } else {
            onClearMergeHolder()
        }
        winnerSetX.clear()
        winnerSetY.clear()
    }
     fun  判断文件夹ID(): String? {
        dragAdapter.getDragData().forEach {
            if (it is FolderData) {
                return it.getFolderId();
            }
        }
         return null
    }

    /**
     * 找到可以触发合并的ViewHolder，暂存对应的ViewHolder信息；
     * 用于在onSelectedChanged()方法中接收拖拽时，判断是否触发合并文件夹
     *
     * @param selected 被选中的item
     * @param target 目标item
     */
    private fun onStashMergeHolder(selected: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) {
        val fromItem = dragAdapter.getDragItem(selected)
        val toItem = dragAdapter.getDragItem(target)
        val canMerge = fromItem?.canMerge(selected) ?: false
        val canFold = toItem?.acceptMerge(target) ?: false
        // 判断是否支持合并成文件夹
        if (canMerge && canFold) {
            if (mergeTarget != target) {
                showMergePreview(target, true)  // 显示合并预览状态
                showMergePreview(mergeTarget, false) // 不显示合并预览状态
                mergeTarget = target
                Log.d(TAG, "onStashMergeHolder: margeTarget = ${target.adapterPosition}")
            }
            if (mergeSelected != selected) {
                mergeSelected = selected
                Log.d(TAG, "onStashMergeHolder: margeSelected = ${selected.adapterPosition}")
            }
        }
    }


    /**
     * 清空需要触发触发合并的ViewHolder
     */
    private fun onClearMergeHolder() {
        if (mergeTarget != null) {
            showMergePreview(mergeTarget, false)  // 不显示合并预览状态
            mergeTarget = null
        }
        if (mergeSelected != null) {
            mergeSelected = null
        }
    }

    /**
     * 展示合并状态
     */
    private fun showMergePreview(target: RecyclerView.ViewHolder?, show: Boolean) {
        val dragItem = dragAdapter.getDragItem(target)
        dragItem?.showMergePreview(target, show)
    }

    /**
     * 拖拽动作结束时，判断是否需要触发合并操作
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        Log.d(TAG, "onSelectedChanged: viewHolder = ${viewHolder?.adapterPosition ?: -1} | actionState = $actionState")
        // 开始拖拽
        if (viewHolder != null && actionState == ACTION_STATE_DRAG) {
            mDragHandler?.onStartDrag(viewHolder)
        }
        // 结束拖拽
        if (viewHolder == null && actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            // 合并操作和拖拽操作设计成互斥
            val performMerge = mergeTarget != null && mergeSelected != null
            if (performMerge) {
                performMergeAction()
            }
            onClearMergeHolder()
            mDragHandler?.onStopDrag(performMerge)
        }
    }
    //第一个合并的文件夹id
    var firstFolderId:String?=null
    /**
     * 执行合并操作，并触发回调
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun performMergeAction() {
        if (mergeTarget != null && mergeSelected != null) {
            val fromPosition = mergeSelected?.adapterPosition ?: -1
            val toPosition = mergeTarget?.adapterPosition ?: -1
            if (fromPosition < 0 || toPosition < 0) {
                return
            }
            mDragHandler?.onMergeData(fromPosition, toPosition)
            Log.d(TAG, "onSelectedChanged: 合并 ${mergeTarget?.adapterPosition} and ${mergeSelected?.adapterPosition}")
        }
    }


    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        //todo 如果目标是文件夹则不交换位置
        val swap = mDragHandler?.swapPosition(fromPosition, toPosition)?: false  && dragAdapter.getDragData().get(toPosition) !is FolderData
        if (swap) { // 交换位置
            mDragHandler?.onBeforeSwap(fromPosition, toPosition)
            val list = dragAdapter.getDragData()
            if (list is MutableList) {
                val item = list.removeAt(fromPosition)
                list.add(toPosition, item)
            }
            mDragHandler?.onAfterSwap(fromPosition, toPosition)
        }
        return swap
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val dragItem = dragAdapter.getDragItem(viewHolder)
        dragItem?.showDragState(viewHolder, isCurrentlyActive)
    }


    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        val dragItem = dragAdapter.getDragItem(viewHolder)
        dragItem?.showDragState(viewHolder, false)
    }


}