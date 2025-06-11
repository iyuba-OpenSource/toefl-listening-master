package com.iyuba.toelflistening.adapter

import androidx.core.util.Consumer
import com.iyuba.toelflistening.bean.QuestionItem
import com.iyuba.toelflistening.databinding.QuestionItemBinding

/**
苏州爱语吧科技有限公司
 */
class QuestionAdapter :BaseAdapter<QuestionItem, QuestionItemBinding>() {
    lateinit var actionListener: Consumer<Int>
    override fun QuestionItemBinding.onBindViewHolder(bean: QuestionItem, position: Int) {
        this.item=bean
        this.root.setOnClickListener {
            actionListener.accept(position)
        }
    }
}