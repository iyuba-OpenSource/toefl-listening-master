package com.iyuba.toelflistening.fragment.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.util.Consumer
import androidx.lifecycle.lifecycleScope
import com.iyuba.headlinelibrary.HeadlineType
import com.iyuba.headlinelibrary.data.model.Headline
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity
import com.iyuba.headlinelibrary.ui.content.AudioContentActivityNew
import com.iyuba.headlinelibrary.ui.content.VideoContentActivity
import com.iyuba.headlinelibrary.ui.content.VideoContentActivityNew
import com.iyuba.headlinelibrary.ui.video.VideoMiniContentActivity
import com.iyuba.module.favor.data.model.BasicFavorPart
import com.iyuba.module.favor.event.FavorItemEvent
import com.iyuba.module.movies.ui.series.SeriesActivity
import com.iyuba.toelflistening.AppClient
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.activity.CollectVideoActivity
import com.iyuba.toelflistening.activity.CustomerServiceActivity
import com.iyuba.toelflistening.activity.LoginActivity
import com.iyuba.toelflistening.activity.MainActivity
import com.iyuba.toelflistening.activity.MemberCentreActivity
import com.iyuba.toelflistening.activity.RankActivity
import com.iyuba.toelflistening.activity.SignActivity
import com.iyuba.toelflistening.activity.StrangeWordActivity
import com.iyuba.toelflistening.adapter.SettingAdapter
import com.iyuba.toelflistening.bean.SettingItem
import com.iyuba.toelflistening.databinding.MineFragmentLayoutBinding
import com.iyuba.toelflistening.fragment.BaseFragment
import com.iyuba.toelflistening.java.actiity.login.WxLoginActivity
import com.iyuba.toelflistening.utils.ExtraKeyFactory
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.judgeLogin
import com.iyuba.toelflistening.utils.judgeType
import com.iyuba.toelflistening.utils.logic.DataCleanManager
import com.iyuba.toelflistening.utils.showDialog
import com.iyuba.toelflistening.utils.showGoLoginDialog
import com.iyuba.toelflistening.utils.showPrivacyDialog
import com.iyuba.toelflistening.utils.showToast
import com.iyuba.toelflistening.utils.skipWeb
import com.iyuba.toelflistening.utils.startActivity
import com.iyuba.toelflistening.utils.visibilityState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import personal.iyuba.personalhomelibrary.event.UserNameChangeEvent
import personal.iyuba.personalhomelibrary.event.UserPhotoChangeEvent
import personal.iyuba.personalhomelibrary.ui.groupChat.GroupChatManageActivity
import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity
import personal.iyuba.personalhomelibrary.ui.message.MessageActivity
import personal.iyuba.personalhomelibrary.ui.my.MySpeechActivity
import personal.iyuba.personalhomelibrary.ui.studySummary.SummaryActivity
import personal.iyuba.personalhomelibrary.ui.studySummary.SummaryType

/**
苏州爱语吧科技有限公司
 */
class MineFragment : BaseFragment<MineFragmentLayoutBinding>(), Consumer<Int>,
    View.OnClickListener {
    private val itemList = mutableListOf<SettingItem>()
    private lateinit var exit: String
    private lateinit var loginRegister: String
    private val adapter = SettingAdapter()
    private val memberCentre = "会员中心"
    private val strangeWord = "生词本"
    private val collectVideo = "视频收藏"
    private val cache = "清除缓存"
    private val privacy = "隐私协议"
    private val logOutDesc = "注销"
    private val studyReport = "学习报告"
    private val rank = "排行榜"
    private val speakCycle = "口语圈"
    private val msgCenter = "消息中心"
    private val groupTitle = "爱语吧官方群"
    private val customerService = "联系客服"
    private var termsUse = ""
    private val logout = SettingItem(R.drawable.log_out, logOutDesc)

    override fun MineFragmentLayoutBinding.initBinding() {
        termsUse = getString(R.string.terms_use)
        itemList.apply {
            add(SettingItem(R.drawable.vip, memberCentre))
            add(SettingItem(R.drawable.new_word, strangeWord))
            add(SettingItem(R.drawable.collect_video, collectVideo))
            add(SettingItem(R.drawable.study_report, studyReport))
            add(SettingItem(R.drawable.rank_me, rank))
            add(SettingItem(R.drawable.speak_cycle, speakCycle))
            add(SettingItem(R.drawable.group, groupTitle))
            add(SettingItem(R.drawable.customer_service, customerService))
            add(SettingItem(R.drawable.message, msgCenter))
            add(SettingItem(R.drawable.cache, cache))
            add(SettingItem(R.drawable.use, termsUse))
            add(SettingItem(R.drawable.privacy, privacy))
        }
        settingList.adapter = adapter
        adapter.actionListener = this@MineFragment
        exit = resources.getString(R.string.exit)
        loginRegister = resources.getString(R.string.login_register)
        refreshUser()
        loginLayout.setOnClickListener(this@MineFragment)
        signIn.apply {
            //飞雷神
//            visibilityState(true)
            visibilityState(!GlobalHome.isLogin())
            setOnClickListener(this@MineFragment)
        }
        lifecycleScope.launch {
            userAction.signResult.collect { result ->
                result.onSuccess {
                    dismissActivityLoad<MainActivity>()
                    if (it.result == "1") {
                        val signStudyTime = 3 * 60
                        val time = it.totalTime.toInt()
                        if (time < signStudyTime) {
                            getStudyTime(time).showToast()
                        } else {
                            requireActivity().startActivity<SignActivity> {
                                putExtra(ExtraKeyFactory.signResult, it)
                            }
                            //打卡成功后刷新本地
                        }
                    } else {
                        "打卡加载失败".showToast()
                    }
                }.onLoading {
                    showActivityLoad<MainActivity>()
                }.onError {
                    dismissActivityLoad<MainActivity>()
                    it.judgeType().showToast()
                }
            }
        }
    }

    private fun getStudyTime(time: Int) = with(StringBuilder("当前已学习")) {
        if (time > 60) {
            val minute = time / 60
            append(minute).append("分").append(time - minute * 60).append("秒")
        } else {
            append(time).append("秒")
        }
        append("\n满3分钟可打卡")
        toString()
    }

    override fun accept(t: Int) {
        when (itemList[t].item) {
            memberCentre -> startActivity(
                Intent(
                    requireContext(),
                    MemberCentreActivity::class.java
                )
            )
            strangeWord -> requireActivity().judgeLogin<StrangeWordActivity>()
            collectVideo -> requireActivity().judgeLogin<CollectVideoActivity>()
            cache -> lifecycleScope.launch {
                DataCleanManager.cleanCustomCache(requireActivity().cacheDir.path)
                "清除成功".showToast()
            }
            privacy -> "撤回已同意的授权".showPrivacyDialog(requireContext(), privacy, "撤回已同意的授权", "去阅读", {
                "撤回后，APP将不可使用，确认撤回授权？".showDialog(requireContext(), "继续撤回") {
                    lifecycleScope.launch {
                        toeflViewModel.saveFirstLogin(false).collect {
                            requireActivity().finish()
                        }
                    }
                }
            }, {
                requireContext().skipWeb(AppClient.privacy)
            })
            logOutDesc -> "确定要注销账号吗？".showDialog(requireContext(), method = {
                startActivity(Intent(requireContext(), WxLoginActivity::class.java))
            })
            termsUse -> requireContext().skipWeb(AppClient.termsUse)
            studyReport -> {
                if (GlobalHome.isLogin()) {
                    val types = arrayOf(
                        SummaryType.LISTEN,
                        SummaryType.EVALUATE,
                        SummaryType.TEST
                    )
                    startActivity(SummaryActivity.getIntent(requireContext(), types, 0))
                } else {
                    requireActivity().showGoLoginDialog()
                }
            }
            rank -> requireActivity().judgeLogin<RankActivity>()
            speakCycle -> requireActivity().judgeLogin<MySpeechActivity>()
            msgCenter -> requireActivity().judgeLogin<MessageActivity>()
            groupTitle -> GroupChatManageActivity.start(
                requireContext(),
                GlobalHome.groupId,
                groupTitle,
                true
            )
            customerService -> requireActivity().startActivity<CustomerServiceActivity> { }
        }

    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.login_layout -> {
                val intent = if (GlobalHome.isLogin()) {
                    PersonalHomeActivity.buildIntent(
                        requireContext(),
                        GlobalHome.userInfo.uid,
                        GlobalHome.userInfo.nickname,
                        0
                    )
                } else {

                    var  bundle = Bundle();
                    bundle.putBoolean("isSec",true)

                    var  intent = Intent(requireContext(), LoginActivity::class.java);
                    intent.putExtras(bundle)

//                    Intent(requireContext(), LoginActivity::class.java)
//                    Intent(requireContext(), WxLoginActivity::class.java)
                }
                startActivity(intent)
            }
            R.id.login -> judgeButtonText()
            R.id.sign_in -> userAction.signToday()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshUser()
    }

    private fun judgeButtonText() {
        when (bind.login.text) {
            exit -> exitUser()
            loginRegister -> startActivity(Intent(requireContext(), WxLoginActivity::class.java))
        }
    }

    private fun exitUser() {
        "确定退出登录吗?".showDialog(requireContext(), method = {
            lifecycleScope.launch {
                userAction.exitLogin().collect {
                    itemList.apply {
                        if (contains(logout)) {
                            val removeIndex = indexOfFirst { it == logout }
                            remove(logout)
                            adapter.notifyItemRemoved(removeIndex)
                            GlobalHome.clearUserInfo()
                            refreshUser()
                        }
                    }
                }
            }
        })
    }

    private fun refreshUser() {
        //飞雷神

        val isLogin = GlobalHome.userInfo.uid != -1


        bind.signIn.visibilityState(!isLogin)
        bind.login.text = if (isLogin) {
            itemList.add(logout)
            exit
        } else {
            itemList.remove(logout)
            loginRegister
        }
        //飞雷神
        adapter.changeData(itemList.distinct())
        bind.login.setOnClickListener(this@MineFragment)


        if (GlobalHome.userInfo.imgSrc != "" && !GlobalHome.userInfo.imgSrc.startsWith("http")) {

            GlobalHome.userInfo.imgSrc =
                "http://static1.iyuba.cn/uc_server/" + GlobalHome.userInfo.imgSrc
        }

        bind.item = GlobalHome.userInfo
        dismissActivityLoad<MainActivity>()
    }

    override fun initEventBus(): Boolean = true

    /**
     * 个人中心模块更改头像
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: UserPhotoChangeEvent) {
        if (requireActivity() is MainActivity) {
            lifecycleScope.launch {
                (requireActivity() as MainActivity).refreshUserInfo()
            }
        }
    }

    /**
     * 个人中心模块更改昵称
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: UserNameChangeEvent) {
        bind.item = with(GlobalHome.userInfo) {
            nickname = event.newName
            this
        }
        lifecycleScope.launch {
            userAction.updateNickName(event.newName).first()
        }
    }

    /**
     * 视频收藏跳转
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: FavorItemEvent) {
        val part = event.items[event.position]
        //primary
        //readingstreet
        //junior
        when (part.type) {
            HeadlineType.NEWS, HeadlineType.VOA, HeadlineType.CSVOA -> {

            }
            HeadlineType.BBC -> {
                startActivity(
                    AudioContentActivityNew.buildIntent(
                        requireContext(),
                        part.categoryName,
                        part.title,
                        part.titleCn,
                        part.pic,
                        part.type,
                        part.id,
                        part.sound
                    )
                )
            }
            HeadlineType.SONG -> {
                startActivity(
                    AudioContentActivity.buildIntent(
                        requireContext(),
                        part.categoryName,
                        part.title,
                        part.titleCn,
                        part.pic,
                        part.type,
                        part.id,
                        part.sound
                    )
                )
            }

            HeadlineType.VOAVIDEO, HeadlineType.MEIYU, HeadlineType.TED, HeadlineType.BBCWORDVIDEO, HeadlineType.TOPVIDEOS, HeadlineType.JAPANVIDEOS -> {
                startActivity(
                    VideoContentActivityNew.buildIntent(
                        requireContext(),
                        part.categoryName,
                        part.title,
                        part.titleCn,
                        part.pic,
                        part.type,
                        part.id,
                        part.sound
                    )
                )
            }
            "series" -> {
                startActivity(SeriesActivity.buildIntent(requireContext(), part.seriesId, part.id))
            }

            HeadlineType.SMALLVIDEO -> {
                startActivity(
                    VideoMiniContentActivity.buildIntentForOne(
                        requireContext(),
                        part.id,
                        0,
                        1,
                        1
                    )
                )
            }
            HeadlineType.HEADLINE -> {
                startActivity(
                    VideoContentActivity.buildIntent(
                        requireContext(),
                        part.changeHeadline()
                    )
                )
            }
        }

    }

    private fun BasicFavorPart.changeHeadline() = with(Headline()) {
        this.type = this@changeHeadline.type
        this.titleCn = this@changeHeadline.titleCn
        this.title = this@changeHeadline.title
        this.sound = this@changeHeadline.sound
        this.id = this@changeHeadline.id
        this.pic = this@changeHeadline.pic
        this
    }


}