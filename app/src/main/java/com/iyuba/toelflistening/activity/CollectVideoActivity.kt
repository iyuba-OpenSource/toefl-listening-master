package com.iyuba.toelflistening.activity

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.iyuba.module.favor.ui.BasicFavorDeleteDelegate
import com.iyuba.module.favor.ui.BasicFavorFragment
import com.iyuba.module.favor.ui.BasicFavorSyncDelegate
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.databinding.ActivityCollectVideoBinding
import com.iyuba.toelflistening.utils.showDialog

/**
 * 为了改一个back颜色，只能出此下策
 * */
class CollectVideoActivity : BaseActivity<ActivityCollectVideoBinding>(){
    private lateinit var deleteDelegate: BasicFavorDeleteDelegate
    private lateinit var syncDelegate: BasicFavorSyncDelegate

    override fun ActivityCollectVideoBinding.initBinding() {
        completeDel.apply {
            setOnClickListener {
                if (text.isEmpty()){
                    text=getString(R.string.finish_choose)
                    setCompoundDrawables(null,null,null,null)
                    deleteDelegate.startDelete()
                }else{
                    showTextImage()
                }
            }
        }
        standardLeft.setOnClickListener {
            if (completeDel.text.isEmpty()){
                onBackPressed()
            }else{
                completeDel.text=""
                deleteDelegate.cancelDelete()
                completeDel.showImage()
            }
        }
        val fragment= with(BasicFavorFragment.newInstance()){
            deleteDelegate=this
            syncDelegate=this
            this
        }
        supportFragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit()
    }

    private fun showTextImage(){
        binding.completeDel.apply {
            deleteDelegate.commitDelete()
            text=""
            showImage()
        }
    }

    private fun TextView.showImage(){
        val context=this@CollectVideoActivity
        val id=R.drawable.basic_favor_ic_delete
        val drawable=ContextCompat.getDrawable(context,id)
        drawable?.let {
            it.setBounds(0,0,it.minimumWidth,it.minimumHeight)
        }
        setCompoundDrawables(null,null,drawable,null)
    }


    override fun onBackPressed() {
        val p =syncDelegate.synchronizingState
        if (binding.completeDel.text.isNotEmpty()){
            showTextImage()
            return
        }
        if (p.first){
            p.second.showDialog(this){}
        }else{
            super.onBackPressed()
        }
    }
}