package jp.hotdrop.compl.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import java.util.*

abstract class ArrayRecyclerAdapter<T, VH: RecyclerView.ViewHolder>(context: Context)
    : RecyclerView.Adapter<VH>(), Iterable<T> {

    private var list: MutableList<T> = mutableListOf()
    private val context = context

    fun getContext(): Context {
        return context
    }

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

    fun clear() {
        list.clear()
    }

    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(list, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun iterator(): Iterator<T> {
        return list.iterator()
    }
}