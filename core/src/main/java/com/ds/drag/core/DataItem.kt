package com.ds.drag.core

import java.util.concurrent.CopyOnWriteArrayList

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/7/11
 * desc   :
 * version: 1.0
 */
interface IDragData {

    /**
     * 是否支持拖拽，默认不支持拖拽
     */
    fun isCanDrag(): Boolean

    /**
     * 判断是否处于文件夹中
     */
    fun inFolder(): Boolean

    /**
     * 保存文件夹标识
     */
    fun setFolderId(folderId: String?)

    /**
     * 获取文件夹标识
     */
    fun getFolderId(): String?

}


/**
 * 简单数据结构
 */
class SimpleData(val id: Int) : IDragData {
    var unreadcount: Int = 0
    var iconResId: Int = 0
    var titleName: String = ""
    var tempFolderId: String? = null


    override fun inFolder(): Boolean {
        return false
    }

    override fun setFolderId(folderId: String?) {
        tempFolderId = folderId
    }

    override fun getFolderId(): String? {
        return tempFolderId
    }

    override fun isCanDrag(): Boolean {
        return true
    }


}


/**
 * 文件夹信息
 */
class FolderData : IDragData {

    // 文件夹id
    private var id: String? = null

    // 文件夹内的数据列表
    val list = CopyOnWriteArrayList<SimpleData>()

    override fun inFolder(): Boolean {
        return false
    }

    /**
     * 获取未读数据
     */
    fun getUnreadcount():Int{
        var count=0;
        if(list!=null){
            for (simpleData in list) {
                count+=simpleData.unreadcount
            }
        }
        return count;
    }
    override fun setFolderId(folderId: String?) {
        this.id = folderId
    }

    override fun getFolderId(): String? {
        return id
    }

    override fun isCanDrag(): Boolean {
        return false
    }

}


/**
 * 预览占位的ViewHolder
 */
class PreviewData : IDragData {

    var realData: SimpleData? = null
    override fun inFolder(): Boolean {
        return false
    }

    override fun setFolderId(folderId: String?) {
    }

    override fun getFolderId(): String? {
        return null
    }

    override fun isCanDrag(): Boolean {
        return false
    }

}
