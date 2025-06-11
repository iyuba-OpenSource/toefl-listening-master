package com.iyuba.toelflistening.adapter

import androidx.core.util.Consumer
import com.iyuba.toelflistening.bean.SettingItem
import com.iyuba.toelflistening.databinding.SettingItemBinding


/**
苏州爱语吧科技有限公司
 */
class SettingAdapter :BaseAdapter<SettingItem, SettingItemBinding>() {
    lateinit var actionListener: Consumer<Int>
    override fun SettingItemBinding.onBindViewHolder(bean: SettingItem, position: Int) {
        this.item=bean
        this.root.setOnClickListener {
            actionListener.accept(position)
        }
    }
}