package com.iyuba.toelflistening.activity

import androidx.core.util.Consumer
import androidx.fragment.app.Fragment
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.onekeyshare.OnekeyShare
import com.google.android.material.tabs.TabLayoutMediator
import com.iyuba.toelflistening.AppClient
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.adapter.PagerAdapter
import com.iyuba.toelflistening.databinding.ActivityRankBinding
import com.iyuba.toelflistening.fragment.rank.RankTopicFragment
import com.iyuba.toelflistening.fragment.rank.StudyRankFragment
import com.iyuba.toelflistening.utils.*

class RankActivity : BaseActivity<ActivityRankBinding>() {
    private lateinit var oks: OnekeyShare
    private lateinit var mediator: TabLayoutMediator
    private val topic="口语"
    private val study="学习"
    private val test="测试"
    private val listen="听力"
    private val titleArray=arrayOf(topic,test,listen,study)

    override fun ActivityRankBinding.initBinding() {
        setTitleText("排行榜")
        installsRight(R.drawable.rank_share,clickRightEvent)
        rankTab.apply {
            titleArray.forEach {
                addTab(newTab().setText(it))
            }
        }
        val frameArray= with( mutableListOf<Fragment>()){
            add(RankTopicFragment(true))
            add(RankTopicFragment(false))
            add(StudyRankFragment())
            add(StudyRankFragment(true))
            this
        }
        rankPager.adapter=PagerAdapter(this@RankActivity,frameArray)
        mediator= TabLayoutMediator(rankTab,rankPager,true) { tab, position ->
            tab.text = titleArray[position]
        }
        mediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediator.detach()
    }

    private val callBackOut=object: PlatformActionListener{
        override fun onComplete(p0: Platform?, p1: Int, p2: HashMap<String, Any>?) {
            "分享成功".showToast()
        }

        override fun onError(p0: Platform?, p1: Int, p2: Throwable?) {
            "分享失败".showToast()
        }

        override fun onCancel(p0: Platform?, p1: Int) {
            "分享失败".showToast()
        }
    }

    private val clickRightEvent=Consumer<Unit>{
        val selectText=titleArray[binding.rankTab.selectedTabPosition]
        val type=when(selectText){
            topic->"speaking"
            study->"studying"
            listen->"listening"
            test->"testing"
            else->""
        }
        val uid=GlobalHome.userInfo.uid
        val appId=AppClient.appId
        val sign="${uid}ranking${appId}".toMd5()
        val shareUrl= with(StringBuilder()){
            append("http://m.")
            append(OtherUtils.iyuba_cn)
            append("/")
            append("i/getRanking.jsp?uid=")
            append(uid)
            append("&appId=").append(appId)
            append("&sign=").append(sign)
            append("&topic=").append(AppClient.appName)
            append("&rankingType=").append(type)
            toString()
        }
        val shareTitle="我在看${getString(R.string.app_name)}的${selectText}排行榜排名}"
        if (!::oks.isInitialized){
            oks=startShareWorld(shareTitle,shareUrl,callBackOut)
        }
        oks.show(this)
    }

}