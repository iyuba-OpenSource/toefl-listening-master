package com.iyuba.toelflistening.utils.view

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.iyuba.toelflistening.R

/**
苏州爱语吧科技有限公司
 */
object ImageUtil {

    @JvmStatic
    @BindingAdapter("loadUrl")
    fun loadUrl(view: ImageView, url: String?) {
        url?.let {
            if (it.isEmpty()){
                view.load(R.drawable.icon)
            }else{
                view.load(it)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("loadLocal")
    fun loadLocal(view: ImageView, url: Int) {
        view.load(url)
    }
}