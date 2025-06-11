package com.iyuba.toelflistening.adapter

import androidx.core.util.Consumer
import com.iyuba.toelflistening.bean.BuyCurrency
import com.iyuba.toelflistening.databinding.BuyCurrencyItemBinding

/**
苏州爱语吧科技有限公司
@Date:  2022/9/30
@Author:  han rong cheng
 */
class ShowCurrencyAdapter:BaseAdapter<BuyCurrency, BuyCurrencyItemBinding>() {
    lateinit var buyListener: Consumer<Int>
    override fun BuyCurrencyItemBinding.onBindViewHolder(bean: BuyCurrency, position: Int) {
        item=bean
        buyNow.setOnClickListener {
            if (::buyListener.isInitialized){
                buyListener.accept(position)
            }
        }
    }
}