package com.iyuba.toelflistening.fragment.main

import android.view.View
import androidx.core.util.Consumer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.iyuba.module.user.IyuUserManager
import com.iyuba.module.user.User
import com.iyuba.sdk.data.iyu.IyuNative
import com.iyuba.sdk.data.ydsdk.YDSDKTemplateNative
import com.iyuba.sdk.data.youdao.YDNative
import com.iyuba.sdk.mixnative.MixAdRenderer
import com.iyuba.sdk.mixnative.MixNative
import com.iyuba.sdk.mixnative.MixViewBinder
import com.iyuba.sdk.mixnative.PositionLoadWay
import com.iyuba.sdk.mixnative.StreamType
import com.iyuba.sdk.nativeads.NativeAdPositioning.ClientPositioning
import com.iyuba.sdk.nativeads.NativeEventListener
import com.iyuba.sdk.nativeads.NativeRecyclerAdapter
import com.iyuba.sdk.nativeads.NativeResponse
import com.iyuba.toelflistening.AppClient
import com.iyuba.toelflistening.BuildConfig
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.activity.MainActivity
import com.iyuba.toelflistening.activity.QuestionTestActivity
import com.iyuba.toelflistening.adapter.ToeflAdapter
import com.iyuba.toelflistening.bean.ToeflItem
import com.iyuba.toelflistening.databinding.ToeflFragmentLayoutBinding
import com.iyuba.toelflistening.fragment.BaseFragment
import com.iyuba.toelflistening.java.Constant
import com.iyuba.toelflistening.java.model.bean.AdEntryBean
import com.iyuba.toelflistening.utils.ExtraKeyFactory
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.disconnectNet
import com.iyuba.toelflistening.utils.judgeType
import com.iyuba.toelflistening.utils.showToast
import com.iyuba.toelflistening.utils.startActivity
import com.youdao.sdk.nativeads.RequestParameters.NativeAdAsset
import com.youdao.sdk.nativeads.RequestParameters.RequestParametersBuilder
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.EnumSet
import java.util.concurrent.TimeUnit

/**
 * 试题列表
 */
class ToeflFragment : BaseFragment<ToeflFragmentLayoutBinding>() {
    private lateinit var adapter: ToeflAdapter

    override fun ToeflFragmentLayoutBinding.initBinding() {
        adapter = with(ToeflAdapter()) {
            actionListener = listener
            this
        }
        bind.toeflList.adapter = adapter
    }


    override fun initView() {
        if (requireActivity().disconnectNet()) {
            return
        }
        lifecycleScope.launch {
            toeflViewModel.requestToeflList().onStart {
                showActivityLoad<MainActivity>()
            }.catch {
                dismissActivityLoad<MainActivity>()
                it.judgeType().showToast()
            }.collect {
                dismissActivityLoad<MainActivity>()
                adapter.changeData(it.data)

                //如果不是会员则请求广告
                if (!GlobalHome.userInfo.isVip()) {

                    toeflViewModel
                        .getAdEntryAll(
                            2,
                            AppClient.adAppId.toString(),
                            GlobalHome.userInfo.uid.toString()
                        )
                        .catch {
                            it.judgeType().showToast()
                        }
                        .collect {

                            if (it.isNotEmpty()) {

                                var adEntryBean = it.get(0)
                                if (!adEntryBean.result.equals("-1")) {

                                    setAdAdapter(adEntryBean.data)
                                }
                            }
                        }
                }
            }
            userAction.getLoginInfo().collect {

                if (it.isSuccess()) {
                    GlobalHome.inflateLoginInfo(it)
                    it.initPersonalHome()
                } else {

                    val user = User()
                    user.uid = 1
                    IyuUserManager.getInstance().currentUser = user
                }
            }
        }
    }

    private val listener = Consumer<ToeflItem> {
        requireActivity().startActivity<QuestionTestActivity> {
            putExtra(ExtraKeyFactory.toeflName, it.name)
        }
    }


    /**
     * 设置
     *
     * @param dataBean
     */
    private fun setAdAdapter(dataBean: AdEntryBean.DataDTO) {
        val mClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

        val desiredAssets = EnumSet.of(
            NativeAdAsset.TITLE,
            NativeAdAsset.TEXT,
            NativeAdAsset.ICON_IMAGE,
            NativeAdAsset.MAIN_IMAGE,
            NativeAdAsset.CALL_TO_ACTION_TEXT
        )
        val requestParameters = RequestParametersBuilder()
            .location(null)
            .keywords(null)
            .desiredAssets(desiredAssets)
            .build()
        val ydNative =
            YDNative(requireActivity(), "edbd2c39ce470cd72472c402cccfb586", requestParameters)

        val iyuNative = IyuNative(requireActivity(), Constant.APPID.toString(), mClient)

        val csjTemplateNative =
            YDSDKTemplateNative(requireActivity(), BuildConfig.TEMPLATE_SCREEN_AD_KEY_CSJ)
        val ylhTemplateNative =
            YDSDKTemplateNative(requireActivity(), BuildConfig.TEMPLATE_SCREEN_AD_KEY_YLH)
        //        YDSDKTemplateNative ksTemplateNative = new YDSDKTemplateNative(requireActivity(), BuildConfig.TEMPLATE_SCREEN_AD_KEY_KS);
        val bdTemplateNative =
            YDSDKTemplateNative(requireActivity(), BuildConfig.TEMPLATE_SCREEN_AD_KEY_BD)

        //添加key
        val ydsdkMap = HashMap<Int, YDSDKTemplateNative>()
        ydsdkMap[StreamType.TT] = csjTemplateNative
        ydsdkMap[StreamType.GDT] = ylhTemplateNative
        //        ydsdkMap.put(StreamType.KS, ksTemplateNative);
        ydsdkMap[StreamType.BAIDU] = bdTemplateNative


        val mixNative = MixNative(ydNative, iyuNative, ydsdkMap)
        val loadWay = PositionLoadWay()
        loadWay.streamSource = intArrayOf(
            dataBean.getFirstLevel().toInt(),
            dataBean.getSecondLevel().toInt(),
            dataBean.getThirdLevel().toInt()
        )
        mixNative.setLoadWay(loadWay)

        val startPosition = 3
        val positionInterval = 5
        val positioning = ClientPositioning()
        positioning.addFixedPosition(startPosition)
        positioning.enableRepeatingPositions(positionInterval)
        val mAdAdapter = NativeRecyclerAdapter(requireActivity(), adapter, positioning)
        mAdAdapter.setNativeEventListener(object : NativeEventListener {
            override fun onNativeImpression(view: View, nativeResponse: NativeResponse) {
            }

            override fun onNativeClick(view: View, nativeResponse: NativeResponse) {
            }
        })
        mAdAdapter.setAdSource(mixNative)


        mixNative.setYDSDKTemplateNativeClosedListener { view ->
            val itemView = (view.parent as View).parent as View
            val viewHolder: RecyclerView.ViewHolder =
                bind.toeflList.getChildViewHolder(itemView)
            val position = viewHolder.bindingAdapterPosition
            mAdAdapter.removeAdsWithAdjustedPosition(position)
        }

        val mixViewBinder = MixViewBinder.Builder(R.layout.item_ad_mix)
            .templateContainerId(R.id.mix_fl_ad)
            .nativeContainerId(R.id.headline_ll_item)
            .nativeImageId(R.id.native_main_image)
            .nativeTitleId(R.id.native_title)
            .build()
        val mixAdRenderer = MixAdRenderer(mixViewBinder)
        mAdAdapter.registerAdRenderer(mixAdRenderer)
        bind.toeflList.setAdapter(mAdAdapter)
        mAdAdapter.loadAds()
    }

}