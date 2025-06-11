package com.iyuba.toelflistening.fragment.toelf

import android.view.View
import androidx.core.util.Consumer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import com.iyuba.toelflistening.activity.QuestionInfoActivity
import com.iyuba.toelflistening.adapter.RankPagingAdapter
import com.iyuba.toelflistening.bean.RankItem
import com.iyuba.toelflistening.databinding.RankFragmentBinding
import com.iyuba.toelflistening.fragment.BaseFragment
import com.iyuba.toelflistening.utils.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
苏州爱语吧科技有限公司
 */
class RankFragment: BaseFragment<RankFragmentBinding>(),View.OnClickListener, Consumer<RankItem> {
    private lateinit var rankAdapter: RankPagingAdapter

    override fun RankFragmentBinding.initBinding() {
        rankLayout.visibility=View.GONE
        rankProgress.visibility=View.VISIBLE
        rankAdapter= with(RankPagingAdapter()){
            itemListener=this@RankFragment
            this
        }
        rankHead.setOnClickListener(this@RankFragment)
        rankPager.apply {
            adapter=rankAdapter
            val decoration=DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
        }
        loadData()
        rankAdapter.addLoadStateListener { state->
            val notLoading=state.refresh is LoadState.NotLoading
            val showProgress=(rankProgress.visibility==View.VISIBLE)
            if (notLoading&&showProgress){
                item=GlobalHome.rankResponse
                rankProgress.visibility=View.GONE
                rankLayout.visibility=View.VISIBLE
            }
        }
        toeflViewModel.releaseSimpleResponse.observe(this@RankFragment){ loadData() }
    }

    private fun gotoEvaluationInfo(userId:Int=GlobalHome.userInfo.uid,
                                   userName:String=GlobalHome.userInfo.username,
                                   userHead:String=GlobalHome.userInfo.imgSrc){
        requireActivity().gotoEvaluationInfo(userId,userName,userHead)
    }
    override fun onClick(p0: View?) {
        if (GlobalHome.isLogin()){
            gotoEvaluationInfo()
        }else{
            activity?.showGoLoginDialog()
        }
    }



    override fun onResume() {
        super.onResume()
        if (GlobalHome.isLogin()){
            lifecycleScope.launch {
                try {
                    val itemResult=toeflViewModel.getRankDataUser()
                    bind.item=itemResult
                }catch (e:Exception){
                    e.judgeType().showToast()
                }
            }
        }
    }

    override fun accept(t: RankItem?) {
        t?.let {
            gotoEvaluationInfo(it.uid, it.name,it.imgSrc)
        }
    }
    private fun loadData(){
        lifecycleScope.launch {
            toeflViewModel.getRankData().onStart {
                showActivityLoad<QuestionInfoActivity>()
            }.catch {
                it.judgeType().showToast()
                dismissActivityLoad<QuestionInfoActivity>()
            }.collect{
                dismissActivityLoad<QuestionInfoActivity>()
                rankAdapter.submitData(it)
            }
        }
    }

}