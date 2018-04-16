package com.iyao.recyclerviewhelper.adapter

import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View

/*********************Adapter***************************/
inline fun <reified R : Any> RecyclerView.Adapter<*>.takeIsInstance(): R?  {
    var adapter : RecyclerView.Adapter<*>? = this
    while (adapter !is R && adapter is AbsAdapterWrapper<*>) {
        adapter = adapter.getWrappedAdapter()
    }
    return adapter as? R?
}
tailrec fun RecyclerView.Adapter<*>.getWrappedPosition(wrappedAdapter: RecyclerView.Adapter<*>, outerPosition: Int): Int {
    return when {
        wrappedAdapter == this -> outerPosition
        this is AdapterWrapper -> getWrappedAdapter().getWrappedPosition(wrappedAdapter, getWrappedPosition(outerPosition))
        else -> -1
    }
}

/**********************ViewHolder************************/
inline fun <reified V : View> RecyclerView.ViewHolder.childView(@IdRes id: Int): V? {
    return itemView.findViewById(id)
}


open class CacheViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val children : SparseArray<View> = SparseArray()
}


inline fun <reified V : View> CacheViewHolder.childView(@IdRes id: Int): V? {
    return children[id, itemView.findViewById<V>(id)?.also{
        children.put(id, it)
    }] as V?
}

typealias CachedAutoRefreshAdapter<E> = AutoRefreshAdapter<CacheViewHolder, E>
typealias CachedStatusWrapper = StatusWrapper<CacheViewHolder>
typealias CachedHeaderAndFooterWrapper = HeaderAndFooterWrapper<CacheViewHolder>
typealias CachedMultipleChoiceWrapper = MultipleChoiceWrapper<CacheViewHolder>
