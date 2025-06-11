package com.iyuba.toelflistening.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.OnLoadDialogListener
import com.iyuba.toelflistening.utils.judgeType
import com.iyuba.toelflistening.utils.showToast
import com.iyuba.toelflistening.utils.view.BaseBinding
import com.iyuba.toelflistening.utils.visibilityState
import com.iyuba.toelflistening.viewmodel.AdvertiseViewModel
import com.iyuba.toelflistening.viewmodel.ToeflViewModel
import com.iyuba.toelflistening.viewmodel.UserActionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.ParameterizedType

/**
苏州爱语吧科技有限公司
 */
abstract class BaseActivity<VB : ViewDataBinding> : AppCompatActivity(), BaseBinding<VB>,
    OnLoadDialogListener {
    protected val toeflViewModel by lazy { ViewModelProvider(this)[ToeflViewModel::class.java] }
    protected val userAction by lazy { ViewModelProvider(this)[UserActionViewModel::class.java] }
    protected val adModel by lazy { ViewModelProvider(this)[AdvertiseViewModel::class.java] }

    private lateinit var dialog: AlertDialog
    private lateinit var standardTitle: TextView
    private lateinit var standardLayout: LinearLayout

    // internal 被定义为 “只有这个模块可以调用”
    internal val binding: VB by lazy(mode = LazyThreadSafetyMode.NONE) {
        getViewBinding(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initDialog()
        binding.initBinding()
        if (initEventBus()) {
            EventBus.getDefault().register(this)
        }
        initView()
    }

    private fun <VB : ViewBinding> Any.getViewBinding(inflater: LayoutInflater): VB {
        val vbClass =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<VB>>()
        val inflate = vbClass[0].getDeclaredMethod("inflate", LayoutInflater::class.java)
        return inflate.invoke(null, inflater) as VB
    }


    open fun initView() {}
    private fun initDialog() {
        val inflaterView: View = LayoutInflater.from(this).inflate(R.layout.loading_dialog, null)
        dialog = with(AlertDialog.Builder(this)) {
            setTitle("提示")
            setView(inflaterView)
            setCancelable(false)
            create()
        }

    }

    fun setTitleText(title: String, leftFlag: Boolean = true) {
        standardTitle = findViewById(R.id.standard_title)
        standardTitle.text = title
        if (leftFlag) {
            val standardLeft = findViewById<ImageView>(R.id.standard_left)
            standardLeft.setBackgroundResource(R.drawable.left)
            standardLeft.setOnClickListener { finish() }
        } else {
            standardLayout = findViewById(R.id.standard_layout)
        }
    }

    fun installsRight(@DrawableRes icon: Int, clickEvent: Consumer<Unit>) {
        findViewById<ImageView>(R.id.standard_right).apply {
            setBackgroundResource(icon)
            setOnClickListener { clickEvent.accept(null) }
        }
    }

    fun changeTitle(title: String) {
        standardTitle.text = title
    }

    fun judgeTitleLayout(flag: Boolean) {
        if (::standardLayout.isInitialized) {
            standardLayout.visibilityState(flag)
        }
    }


    @OptIn(FlowPreview::class)
    suspend fun refreshUserInfo() {
        val uid = GlobalHome.userInfo.uid.toString()
        userAction.refreshSelf(uid).flatMapConcat {
            val headUrl = "http://static1.iyuba.cn/uc_server/${it.middle_url}"
//            //此处应为异步
            GlobalHome.userInfo.imgSrc = headUrl
            userAction.changeLocalHead(headUrl).first()
            userAction.saveSelf(it)
        }.flowOn(Dispatchers.IO)
            .catch {
                it.judgeType()?.showToast()
            }
            .collect {}
    }

    override fun showLoad() {
        dialog.apply {
            if (!isShowing) show()
        }
    }

    override fun dismissLoad() {
        dialog.apply {
            if (isShowing) dismiss()
        }
    }

    /**
     * 只有出现@Subscribe注解的类里才注册EventBus
     * */
    open fun initEventBus() = false


    override fun onDestroy() {
        super.onDestroy()
        if (initEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }
}