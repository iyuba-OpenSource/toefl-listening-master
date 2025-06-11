package com.iyuba.toelflistening.activity

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import com.google.android.material.tabs.TabLayout
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.activity.dollar.BuyCurrencyActivity
import com.iyuba.toelflistening.bean.AllVipItem
import com.iyuba.toelflistening.bean.VipBean
import com.iyuba.toelflistening.databinding.ActivityMemberCentreBinding
import com.iyuba.toelflistening.utils.*


/**
 * 会员中心
 */
class MemberCentreActivity : BaseActivity<ActivityMemberCentreBinding>(), View.OnClickListener,
    TabLayout.OnTabSelectedListener {
    /**
     * 本应用VIP
     * */
    private val appVip = 0

    /**
     * 全站VIP
     * */
    private val allVip = 1

    /**
     * 黄金VIP
     * */
    private val goldVip = 2
    private val vipArray = arrayOf(appVip, allVip, goldVip)
    private lateinit var allVipLayout: LinearLayout
    private lateinit var currentSelect: RadioGroup


    override fun ActivityMemberCentreBinding.initBinding() {
        setTitleText("会员中心")
        activateNow.setOnClickListener(this@MemberCentreActivity)
        buyCurrency.setOnClickListener(this@MemberCentreActivity)
        refreshLocalUserInfo()
        tabLayout.apply {
            addVipTab()
            addOnTabSelectedListener(this@MemberCentreActivity)
        }
        contentVip.apply {
            removeAllViews()
            if (intent.getBooleanExtra(ExtraKeyFactory.buyGoldVip, false)) {
                addView(getVipContentText(goldVip))
                addView(getSelectPriceGroup(goldVip))
                tabLayout.apply { getTabAt(tabCount - 1)?.select() }
            } else {
                addView(getVipContentText())
                addView(getSelectPriceGroup())
            }
        }
    }

    private fun refreshLocalUserInfo() {
        binding.item = GlobalHome.userInfo
        val listen = if (!GlobalHome.isLogin()) {
            this@MemberCentreActivity
        } else {
            null
        }
        binding.vipGoLogin.setOnClickListener(listen)
    }


    /**
     * 获取选择价格的RadioGroup
     * */
    private fun getSelectPriceGroup(vipType: Int = appVip): RadioGroup {
        val group = with(RadioGroup(ContextThemeWrapper(this, R.style.VerticalMargin))) {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            this
        }
        val list = mutableListOf<VipBean>()
        when (vipType) {
            appVip -> list.apply {
                add(VipBean(0, 30, "本应用会员1个月"))
                add(VipBean(1, 99, "本应用会员12个月"))
                add(VipBean(2, 199, "本应用会员36个月"))
                add(VipBean(3, 299, "本应用会员60个月"))
            }
            allVip -> list.apply {
                add(VipBean(0, 50, "全站会员1个月"))
                add(VipBean(1, 198, "全站会员6个月"))
                add(VipBean(2, 298, "全站会员12个月"))
                add(VipBean(3, 588, "全站会员36个月"))
            }
            goldVip -> list.apply {
                add(VipBean(0, 98, "黄金会员1个月"))
                add(VipBean(1, 288, "黄金会员3个月"))
                add(VipBean(2, 518, "黄金会员6个月"))
                add(VipBean(3, 998, "黄金会员12个月"))
            }
        }
        list.forEach {
            group.addSingleRadioButton(this, it.realDescription(), it.id, 20)
        }
        if (list.isNotEmpty()) {
            (group.getChildAt(0) as RadioButton).isChecked = true
        }
        currentSelect = group
        return group
    }

    private fun TabLayout.addVipTab() {
        val descArray = resources.getStringArray(R.array.vip_desc_array)
        for (i in descArray.indices) {
            val tab = with(newTab()) {
                text = descArray[i]
                id = vipArray[i]
                this
            }
            addTab(tab)
        }
    }

    /**
     * 获取不同会员的描述文字
     * */
    private fun getVipContentText(vipType: Int = appVip): TextView {
        val content = when (vipType) {
            appVip -> getString(R.string.vip_describe)
            allVip -> getString(R.string.all_vip_describe)
            goldVip -> getString(R.string.gold_vip_describe)
            else -> ""
        }
        val params = with(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        ) {
            bottomMargin = 5
            topMargin = 5
            leftMargin = 20
            rightMargin = 20
            this
        }
        val view = with(TextView(this)) {
            textSize = 15f
            text = content
            layoutParams = params
            this
        }
        return view
    }

    /**
     * 全站会员功能展示
     * */
    private fun getAllVip(): LinearLayout {
        val groupLayout = with(LinearLayout(this)) {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            this
        }
        val list = mutableListOf(
            AllVipItem("无广告", R.drawable.tequan1),
            AllVipItem("尊贵标识", R.drawable.tequan2),
            AllVipItem("调节语速", R.drawable.tequan3)
        )
        val groupParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        groupLayout.apply {
            addView(getAllVipRowLayout(list), groupParams)
            addBlackLine()
        }
        list.apply {
            clear()
            add(AllVipItem("高速下载", R.drawable.tequan4))
            add(AllVipItem(getString(R.string.view_analysis), R.drawable.tequan5))
            add(AllVipItem("语音评测", R.drawable.tequan6))
        }
        groupLayout.apply {
            addView(getAllVipRowLayout(list), groupParams)
            addBlackLine()
        }
        list.apply {
            clear()
            add(AllVipItem("PDF导出", R.drawable.tequan7))
            add(AllVipItem("全部应用", R.drawable.tequan8))
            add(AllVipItem("换话费", R.drawable.tequan9))
        }
        groupLayout.addView(getAllVipRowLayout(list), groupParams)
        return groupLayout
    }

    private fun getAllVipRowLayout(list: List<AllVipItem>): LinearLayout {
        val rowLayout = with(LinearLayout(this)) {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            this
        }
        for (i in list.indices) {
            val textParams =
                with(LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)) {
                    setMargins(5)
                    this
                }
            val iconText = with(TextView(this)) {
                text = list[i].desc
                typeface = Typeface.DEFAULT_BOLD
                layoutParams = textParams
                gravity = Gravity.CENTER
                val top = ContextCompat.getDrawable(this@MemberCentreActivity, list[i].resource)
                setCompoundDrawablesWithIntrinsicBounds(null, top, null, null)
                this
            }
            rowLayout.addView(iconText)
            //最后一个
            if (i != list.size - 1) {
                rowLayout.addBlackLine(false)
            }
        }
        return rowLayout
    }

    private fun LinearLayout.addBlackLine(isHorizontal: Boolean = true) {
        val line = with(View(this@MemberCentreActivity)) {
            setBackgroundColor(Color.BLACK)
            layoutParams = if (isHorizontal) {
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
            } else {
                LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT)
            }
            this
        }
        addView(line)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.vip_go_login -> showGoLoginDialog()
            R.id.buy_currency -> startActivity<BuyCurrencyActivity> { }
            R.id.activate_now -> {
                if (!GlobalHome.userInfo.isSuccess()) {
                    showGoLoginDialog()
                } else {
                    //需要获取当前RadioGroup
                    val array = currentSelect.getSingleRadio()[0].text.split(":￥")
                    startPayActivity(array[0], array[1])
                }
            }
        }
    }


    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.let {
            binding.contentVip.apply {
                removeAllViews()
                if (it.id != allVip) {
                    addView(getVipContentText(it.id))
                    addView(getSelectPriceGroup(it.id))
                } else {
                    if (!::allVipLayout.isInitialized) allVipLayout = getAllVip()
                    addView(allVipLayout)
                    addView(getSelectPriceGroup(allVip))
                    addView(getVipContentText(allVip))
                }
            }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onRestart() {
        super.onRestart()
        refreshLocalUserInfo()
    }
}