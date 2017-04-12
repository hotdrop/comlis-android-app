package jp.hotdrop.compl.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import java.util.*


abstract class ArrayRecyclerAdapter<T, VH: RecyclerView.ViewHolder>(private val context: Context)
    : RecyclerView.Adapter<VH>() {

    protected val list: MutableList<T> = mutableListOf()

    override fun getItemCount(): Int {
        return list.size
    }

    fun getItem(index: Int): T {
        return list[index]
    }

    fun addItem(item: T) {
        list.add(item)
    }

    fun addAll(items: List<T>) {
        list.addAll(items)
    }

    fun clear() {
        list.clear()
    }

    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(list, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    /**
     * flexbox-layoutで上下左右のドラッグができるリストはこっちのonItemMovedメソッド使う
     * 上下の場合は常に互いのアイテムをswapさせれば問題なかったが、flexbox-layoutを使って上下左右のドラッグを可能にすると
     * アイテムを飛び越えられるので隣接するアイテムのswapだけでは対応できない。
     * なので上下左右のアイテム移動用にonItemMoveとonListUpdateを用意した。
     */
    fun onItemMovedForFlexBox(fromPosition: Int, toPosition: Int): Boolean {
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    fun onListUpdateForFlexBox(fromPosition: Int, toPosition: Int) {
        val moveItem = list[fromPosition]
        list.removeAt(fromPosition)
        if(toPosition > list.count()) list.add(moveItem) else list.add(toPosition, moveItem)
    }
}