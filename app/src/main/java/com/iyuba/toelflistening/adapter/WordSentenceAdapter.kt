package com.iyuba.toelflistening.adapter

import com.iyuba.toelflistening.bean.PickWordItem
import com.iyuba.toelflistening.databinding.ShowWordSentenceItemBinding

/**
苏州爱语吧科技有限公司
 */
class WordSentenceAdapter:BaseAdapter<PickWordItem,ShowWordSentenceItemBinding>() {
    override fun ShowWordSentenceItemBinding.onBindViewHolder(bean: PickWordItem, position: Int) {
        this.item=bean
    }
}