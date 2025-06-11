package com.iyuba.toelflistening.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.bean.StrangenessWordItem
import com.iyuba.toelflistening.databinding.StrangenessWordItemBinding
import com.iyuba.toelflistening.utils.StrangeListener


/**
苏州爱语吧科技有限公司
 */
class DataRecycleViewAdapter : PagingDataAdapter<StrangenessWordItem, DataRecycleViewAdapter.DataViewHolder>(object:DiffUtil.ItemCallback<StrangenessWordItem>(){
    override fun areItemsTheSame(oldItem: StrangenessWordItem, newItem: StrangenessWordItem): Boolean {
        return oldItem.createDate==newItem.createDate
    }

    override fun areContentsTheSame(oldItem: StrangenessWordItem, newItem: StrangenessWordItem): Boolean {
        return oldItem==newItem
    }
}) {
    lateinit var listener: StrangeListener
    override fun onBindViewHolder(holder: DataRecycleViewAdapter.DataViewHolder, position: Int) {
        val item=getItem(position)
        holder.bind.item=item
        item?.let {
            holder.bind.videoWord.setOnClickListener { listener.playVideo(item.Audio) }
            holder.bind.root.setOnClickListener {  listener.wordDetailed(item.Word)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataRecycleViewAdapter.DataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val bind=DataBindingUtil.inflate<StrangenessWordItemBinding>(inflater, R.layout.strangeness_word_item,parent,false)
        return DataViewHolder(bind)
    }
    inner class DataViewHolder(val bind:StrangenessWordItemBinding) : RecyclerView.ViewHolder(bind.root)
}