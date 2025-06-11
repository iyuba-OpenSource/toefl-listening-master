package com.iyuba.toelflistening.adapter

import com.iyuba.toelflistening.bean.SentenceItem
import com.iyuba.toelflistening.databinding.SentenceItemBinding
import com.iyuba.toelflistening.utils.OnWordClickListener
import com.iyuba.toelflistening.utils.SearchWordListener

/**
苏州爱语吧科技有限公司
 */
class SentenceAdapter : BaseAdapter<SentenceItem, SentenceItemBinding>() {
    lateinit var wordListen: SearchWordListener

    override fun SentenceItemBinding.onBindViewHolder(bean: SentenceItem, position: Int) {
        this.item = bean
        if (::wordListen.isInitialized) {
            selectText.apply {
                setOnWordClickListener(object : OnWordClickListener(){
                    override fun onNoDoubleClick(str: String) {
                        wordListen.searchListener(str, this@apply)
                    }
                })
            }
        }
    }
}