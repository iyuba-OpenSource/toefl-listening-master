package com.iyuba.toelflistening.activity.dollar

import androidx.core.util.Consumer
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.activity.BaseActivity
import com.iyuba.toelflistening.adapter.ShowCurrencyAdapter
import com.iyuba.toelflistening.bean.BuyCurrency
import com.iyuba.toelflistening.databinding.ActivityBuyCurrencyBinding
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.addDefaultDecoration
import com.iyuba.toelflistening.utils.startPayActivity

class BuyCurrencyActivity : BaseActivity<ActivityBuyCurrencyBinding>() , Consumer<Int> {
    private val list= mutableListOf<BuyCurrency>()
    override fun ActivityBuyCurrencyBinding.initBinding() {
        setTitleText("爱语币充值")
        response=GlobalHome.userInfo
        list.apply {
            add(BuyCurrency(19.9f, R.drawable.buy_200,210))
            add(BuyCurrency(59.9f, R.drawable.buy_600,650))
            add(BuyCurrency(99.9f, R.drawable.buy_1k,1100))
            add(BuyCurrency(599f, R.drawable.buy_6k,6600))
            add(BuyCurrency(999f, R.drawable.buy_1w,12000))
        }
        val showAdapter= with(ShowCurrencyAdapter()){
            buyListener=this@BuyCurrencyActivity
            changeData(list)
            this
        }
        butList.apply {
            adapter=showAdapter
            addDefaultDecoration()
        }
    }

    override fun accept(t: Int?) {
        t?.let {
            list[it].apply {
                startPayActivity(orderInfo,price.toString(),true)
            }
        }
    }

}