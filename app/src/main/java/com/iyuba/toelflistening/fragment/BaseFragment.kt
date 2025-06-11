package com.iyuba.toelflistening.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.iyuba.toelflistening.utils.OnLoadDialogListener
import com.iyuba.toelflistening.utils.view.BaseBinding
import com.iyuba.toelflistening.viewmodel.AdvertiseViewModel
import com.iyuba.toelflistening.viewmodel.ToeflViewModel
import com.iyuba.toelflistening.viewmodel.UserActionViewModel
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.ParameterizedType

/**
苏州爱语吧科技有限公司
 */
abstract class BaseFragment<VB:ViewDataBinding> :Fragment() , BaseBinding<VB> {
    protected lateinit var bind:VB
    protected val toeflViewModel by lazy { ViewModelProvider(requireActivity())[ToeflViewModel::class.java] }
    protected val userAction by lazy { ViewModelProvider(requireActivity())[UserActionViewModel::class.java] }
    protected val adModel by lazy { ViewModelProvider(requireActivity())[AdvertiseViewModel::class.java] }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind=getViewBinding(inflater, container)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        bind.initBinding()
        if (initEventBus()){
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::bind.isInitialized){
            bind.unbind()
        }
    }

    private fun <VB: ViewBinding> Any.getViewBinding(inflater: LayoutInflater, container: ViewGroup?):VB{
        val vbClass =  (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
        val inflate = vbClass[0].getDeclaredMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
        return inflate.invoke(null, inflater, container, false) as VB
    }
    open fun initView(){}


    fun changeTime(length: Int): String {
        val second = length / 1000
        val end = (second % 60)
        return if (end >= 10) {
            (second / 60).toString() + ":" + end
        } else {
            (second / 60).toString() + ":" + "0" + end
        }
    }

    /**
     * 利用kotlin泛型实例化来调用当前Fragment依附的Activity的loadingDialog
     * */
    inline fun  <reified T : OnLoadDialogListener> showActivityLoad(){
        if (requireActivity() is T){
            (requireActivity() as T).showLoad()
        }
    }

    inline fun  <reified T : OnLoadDialogListener> dismissActivityLoad(){
        if (requireActivity() is T){
            (requireActivity() as T).dismissLoad()
        }
    }

    open fun initEventBus():Boolean=false

    override fun onDetach() {
        super.onDetach()
        if (initEventBus()){
            EventBus.getDefault().unregister(this)
        }
    }
}