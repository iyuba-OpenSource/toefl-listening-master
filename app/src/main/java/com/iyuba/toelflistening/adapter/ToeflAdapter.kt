package com.iyuba.toelflistening.adapter

import androidx.core.util.Consumer
import com.iyuba.toelflistening.bean.ToeflItem
import com.iyuba.toelflistening.databinding.ToeflItemBinding

/**
苏州爱语吧科技有限公司
 */
class ToeflAdapter : BaseAdapter<ToeflItem, ToeflItemBinding>() {
    lateinit var actionListener:Consumer<ToeflItem>

    override fun ToeflItemBinding.onBindViewHolder(bean: ToeflItem, position: Int) {
        item = bean
        root.setOnClickListener {
            actionListener.accept(bean)
        }
    }
}