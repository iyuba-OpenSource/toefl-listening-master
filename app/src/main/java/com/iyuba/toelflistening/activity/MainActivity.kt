package com.iyuba.toelflistening.activity

import android.content.Intent
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationBarView
import com.iyuba.headlinelibrary.HeadlineType
import com.iyuba.headlinelibrary.IHeadline
import com.iyuba.headlinelibrary.IHeadlineManager
import com.iyuba.headlinelibrary.event.HeadlineGoVIPEvent
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity
import com.iyuba.headlinelibrary.ui.content.VideoContentActivity
import com.iyuba.headlinelibrary.ui.title.DropdownTitleFragmentNew
import com.iyuba.imooclib.ImoocManager
import com.iyuba.imooclib.event.ImoocBuyVIPEvent
import com.iyuba.imooclib.ui.mobclass.MobClassFragment
import com.iyuba.module.dl.DLItemEvent
import com.iyuba.toelflistening.AppClient
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.adapter.PagerAdapter
import com.iyuba.toelflistening.databinding.ActivityMainBinding
import com.iyuba.toelflistening.fragment.main.MineFragment
import com.iyuba.toelflistening.fragment.main.ToeflFragment
import com.iyuba.toelflistening.java.actiity.login.WxLoginActivity
import com.iyuba.toelflistening.java.fragment.BreakThroughFragment
import com.iyuba.toelflistening.utils.ExtraKeyFactory
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.OtherUtils
import com.iyuba.toelflistening.utils.startActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : BaseActivity<ActivityMainBinding>(), NavigationBarView.OnItemSelectedListener {
    private val idArray = mutableListOf<Int>()
    private val frameArray = mutableListOf<Fragment>()
    private val titleArray = mutableListOf<String>()

    override fun ActivityMainBinding.initBinding() {

        binding.framePager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomBar.selectedItemId = idArray[position]
                changeTitle(titleArray[position])
                val isOutModel = with(frameArray[position]) {
                    (this is DropdownTitleFragmentNew) || (this is MobClassFragment) || (this is BreakThroughFragment)
                }
                judgeTitleLayout(isOutModel)
            }
        })
        setTitleText(resources.getString(R.string.app_name), false)
        binding.title = "";
    }

    override fun initView() {
        addSelectData(R.id.toefl, ToeflFragment(), R.string.app_name)

        //单词闯关
        addSelectData(R.id.break_through, BreakThroughFragment(), R.string.break_through)
        //飞雷神

        if (System.currentTimeMillis() > AppClient.EXAMINE_TIME.toLong()) {

            addSelectData(R.id.micro_lesson, inflateMicroLesson(), R.string.micro_lesson)
            binding.bottomBar.inflateMenu(R.menu.bottom_menu)
        }else{

            binding.bottomBar.inflateMenu(R.menu.bottom_menu_no_imooc)
            binding.bottomBar.menu
        }

        addSelectData(R.id.small_video, getWatchVideo(), R.string.small_video)
        addSelectData(R.id.mine, MineFragment(), R.string.mine)
        binding.bottomBar.setOnItemSelectedListener(this)
        binding.framePager.apply {
            adapter = PagerAdapter(this@MainActivity, frameArray)
            setCurrentNoScroll(0)
        }
    }

    private fun ViewPager2.setCurrentNoScroll(index: Int) {
        setCurrentItem(index, false)
    }

    override fun initEventBus(): Boolean = true

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val index = idArray.indexOfFirst { item.itemId == it }
        binding.framePager.setCurrentNoScroll(index)
        changeTitle(titleArray[index])
        return true
    }


    private fun addSelectData(@IdRes id: Int, child: Fragment, @StringRes descId: Int) {
        idArray.add(id)
        frameArray.add(child)
        titleArray.add(resources.getString(descId))
    }

    /**
     * 视频模块
     * */
    private fun getWatchVideo(): DropdownTitleFragmentNew {
        IHeadline.resetMseUrl()
        val extraMergeUrl = "http://${OtherUtils.i_user_speech}test/merge/"
        val extraUrl = "http://${OtherUtils.i_user_speech}test/ai/"
        IHeadline.setExtraMergeAudioUrl(extraMergeUrl)
        IHeadline.setExtraMseUrl(extraUrl)
        IHeadlineManager.appId = AppClient.appId.toString()
        IHeadlineManager.appName = AppClient.appName
        val types = arrayOf(
            HeadlineType.SMALLVIDEO,
//            HeadlineType.NEWS,
            HeadlineType.HEADLINE,
//            HeadlineType.SONG,
//            HeadlineType.VOA,
//            HeadlineType.CSVOA,
//            HeadlineType.BBC,
            HeadlineType.VOAVIDEO,
            HeadlineType.MEIYU,
            HeadlineType.TED,
            HeadlineType.BBCWORDVIDEO,
//            HeadlineType.JAPANVIDEOS,
//            HeadlineType.TOPVIDEOS,
//            HeadlineType.SMALLVIDEO_JP,
//            HeadlineType.VIDEO,
//            HeadlineType.WORD,
//            HeadlineType.QUESTION,
//            HeadlineType.CLASS
        )
        val bundle = DropdownTitleFragmentNew.buildArguments(10, types, false)
        return DropdownTitleFragmentNew.newInstance(bundle)
    }

    /**
     * 微课跳转开通黄金会员
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: ImoocBuyVIPEvent) {
        //判断是否登录
        //跳转黄金会员界面
        if (GlobalHome.isLogin()) {
            startActivity<MemberCentreActivity> {
                putExtra(ExtraKeyFactory.buyGoldVip, true)
            }
        } else {
            startActivity(Intent(this, WxLoginActivity::class.java))
        }
    }

    /**
     * 视频下载后点击
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: DLItemEvent) {
        val item = event.items[event.position]
        when (item.type) {
            HeadlineType.VOA,
            HeadlineType.CSVOA,
            HeadlineType.BBC,
            HeadlineType.SONG -> {
                startActivity(
                    AudioContentActivity.buildIntent(
                        this,
                        item.categoryName,
                        item.title,
                        item.titleCn,
                        item.pic,
                        item.type,
                        item.id
                    )
                )
            }
            HeadlineType.VOAVIDEO,
            HeadlineType.MEIYU,
            HeadlineType.TED,
            HeadlineType.BBCWORDVIDEO,
            HeadlineType.TOPVIDEOS,
            HeadlineType.JAPANVIDEOS -> {
                startActivity(
                    VideoContentActivity.buildIntent(
                        this,
                        item.categoryName,
                        item.title,
                        item.titleCn,
                        item.pic,
                        item.type,
                        item.id
                    )
                )
            }
        }
    }

    /**
     * 获取视频模块“现在升级的点击”
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(headlineGoVIPEvent: HeadlineGoVIPEvent?) {
        val intent = Intent(this, MemberCentreActivity::class.java)
        startActivity(intent)
    }

    /**
     * 微课模块
     * */
    private fun inflateMicroLesson(): MobClassFragment {
        ImoocManager.appId = AppClient.appId.toString()
        val newToeflId = 7
        val list = with(ArrayList<Int>()) {
            add(-2) //全部课程
            add(-1) //最新课程
            add(2) //英语四级
            add(3) //VOA英语
            add(4) //英语六级
            add(newToeflId) //托福
            add(8) //考研英语一
            add(9) //考研英语一
            add(21) //新概念英语
            add(22) //走遍美国
            add(28) //学位英语
            add(52) //考研英语二
            add(91) //中职英语
            this
        }
        val args = MobClassFragment.buildArguments(newToeflId, false, list)
        return MobClassFragment.newInstance(args)
    }


}