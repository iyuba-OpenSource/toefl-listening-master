package com.iyuba.toelflistening.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
苏州爱语吧科技有限公司
 */
abstract class BaseAdapter<T, VB : ViewDataBinding> : RecyclerView.Adapter<BaseAdapter.BindViewHolder<VB>>() {
    protected val  data = mutableListOf<T>()

    class BindViewHolder<M : ViewDataBinding>(val bind: M) : RecyclerView.ViewHolder(bind.root)

    override fun onBindViewHolder(holder: BindViewHolder<VB>, position: Int) {
        with(holder.bind){
            onBindViewHolder(data[position],position)
            executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindViewHolder<VB> {
        return with(getViewBinding<VB>(LayoutInflater.from(parent.context),parent,1)){
            BindViewHolder(this)
        }
    }

    override fun getItemCount(): Int =data.size

    fun changeData(newData: List<T>) {
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = data.size

            override fun getNewListSize(): Int = newData.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                areItemsTheSame(data[oldItemPosition], newData[newItemPosition])

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                areItemContentsTheSame(data[oldItemPosition], newData[newItemPosition],oldItemPosition, newItemPosition)

        })
        data.clear()
        data.addAll(newData)
        result.dispatchUpdatesTo(this)
    }

    abstract fun VB.onBindViewHolder(bean: T, position: Int)

    private fun <VB:ViewBinding> Any.getViewBinding(inflater: LayoutInflater, container: ViewGroup?, position:Int = 0):VB{
        val vbClass =  (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
        val inflate = vbClass[position].getDeclaredMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
        return inflate.invoke(null, inflater, container, false) as VB
    }
    protected open fun areItemContentsTheSame(oldItem: T, newItem: T, oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItem == newItem
    }

    protected open fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }


}