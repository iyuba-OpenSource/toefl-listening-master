package com.iyuba.toelflistening.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.util.Consumer
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.bean.GroupRankItem
import com.iyuba.toelflistening.databinding.RankStudyItemLayoutBinding

/**
苏州爱语吧科技有限公司
@Date:  2023/1/16
@Author:  han rong cheng
 */
class StudyRankAdapter: PagingDataAdapter<GroupRankItem, StudyRankAdapter.RankHolder>(object : DiffUtil.ItemCallback<GroupRankItem>() {
    override fun areItemsTheSame(oldItem: GroupRankItem, newItem: GroupRankItem): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: GroupRankItem, newItem: GroupRankItem): Boolean {
        return oldItem == newItem
    }
}) {
    lateinit var itemListener:Consumer<GroupRankItem>
    override fun onBindViewHolder(holder: StudyRankAdapter.RankHolder, position: Int) {
        getItem(position)?.let {
            holder.bind.apply {
                item=it
                val pair=if (it.ranking<=3){
                    Pair(Color.WHITE,R.drawable.rank_border)
                }else{
                    Pair(Color.BLACK,R.drawable.rank_border_white)
                }
                rankStudyIndex.apply {
                    this.setTextColor(pair.first)
                    this.setBackgroundResource(pair.second)
                }
                root.setOnClickListener {_->
                    itemListener.accept(it)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyRankAdapter.RankHolder {
        val inflater = LayoutInflater.from(parent.context)
        val bind = DataBindingUtil.inflate<RankStudyItemLayoutBinding>(inflater, R.layout.rank_study_item_layout, parent, false)
        return RankHolder(bind)
    }

    inner class RankHolder(val bind: RankStudyItemLayoutBinding) : RecyclerView.ViewHolder(bind.root)
}