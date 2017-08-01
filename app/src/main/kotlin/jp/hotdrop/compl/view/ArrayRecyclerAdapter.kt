package jp.hotdrop.compl.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import java.util.*


abstract class ArrayRecyclerAdapter<T, VH: RecyclerView.ViewHolder>(private val context: Context)
    : RecyclerView.Adapter<VH>() {

    protected var list: MutableList<T> = mutableListOf()

    constructor(context: Context, list: MutableList<T>): this(context) {
        this.list = list
    }

    override fun getItemCount() = list.size

    fun getItem(index: Int) = list[index]

    fun getItemPosition(item: T): Int? {
        val idx = list.indexOf(item)
        return if(idx == -1) null else idx
    }

    fun addItem(item: T) {
        list.add(item)
    }

    fun addItemToFirst(item: T) {
        list.add(0, item)
    }

    fun addAll(items: List<T>) {
        list.addAll(items)
    }

    fun removeItem(index: Int) {
        list.removeAt(index)
    }

    fun clear() {
        list.clear()
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(list, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }


    /**
     * flexbox-layoutで上下左右のドラッグができるリストはこっちのonItemMovedメソッド使う
     * 上下の場合は常に互いのアイテムをswapさせれば問題なかったが、flexbox-layoutを使って上下左右のドラッグを可能にすると
     * アイテムを飛び越えられるので隣接するアイテムのswapだけでは対応できない。
     * なので上下左右のアイテム移動用にonItemMoveとonListUpdateを用意した。
     */
    fun onItemMovedForFlexBox(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition)
    }

    fun onListUpdateForFlexBox(fromPosition: Int, toPosition: Int) {
        val moveItem = list[fromPosition]
        list.removeAt(fromPosition)
        if(toPosition > list.count()) list.add(moveItem) else list.add(toPosition, moveItem)
    }
}