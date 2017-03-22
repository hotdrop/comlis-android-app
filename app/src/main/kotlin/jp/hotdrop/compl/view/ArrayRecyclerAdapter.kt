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

    fun removeItem(index: Int) {
        list.removeAt(index)
    }

    fun addAll(items: Collection<T>) {
        list.addAll(items)
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
}