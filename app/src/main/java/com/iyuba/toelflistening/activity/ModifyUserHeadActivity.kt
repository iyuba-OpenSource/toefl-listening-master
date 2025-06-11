package com.iyuba.toelflistening.activity

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.databinding.ActivityModifyUserHeadBinding
import com.iyuba.toelflistening.utils.*
import com.iyuba.toelflistening.utils.logic.ChoosePhotoContracts
import com.iyuba.toelflistening.utils.view.LocalImageAdapter
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.io.File

@RequiresApi(Build.VERSION_CODES.Q)
class ModifyUserHeadActivity : BaseActivity<ActivityModifyUserHeadBinding>(), View.OnClickListener {
    private lateinit var imageUri: Uri
    private lateinit var outputImage: File
    private val requestTakePermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        it.checkPermission { operatePhoto() }
    }
    private val requestTakeData = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) takePicture()
    }
    private val requestChooseData = registerForActivityResult(ChoosePhotoContracts()) {
        if (it!=null) choosePicture(it)
    }
    private val requestChoosePermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        it.checkPermission { requestChooseData.launch(null) }
    }

    override fun ActivityModifyUserHeadBinding.initBinding() {
        setTitleText("更改头像")
        nowHead.load(GlobalHome.userInfo.imgSrc)
        albumSelection.setOnClickListener(this@ModifyUserHeadActivity)
        finishChoose.setOnClickListener(this@ModifyUserHeadActivity)
        photograph.setOnClickListener(this@ModifyUserHeadActivity)
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.album_selection -> requestChoosePermission.checkObtainPermission { requestChooseData.launch(null) }
            R.id.finish_choose -> startUpload()
            R.id.photograph -> requestTakePermission.checkObtainPermission { operatePhoto() }
        }
    }

    private fun choosePicture(data: Uri) {
        binding.nowHead.load(getBitmapFromUri(data))
        outputImage= File (LocalImageAdapter.convertUriToFile(this,data).toString())

    }

    @OptIn(FlowPreview::class)
    private fun startUpload() {
        if (disconnectNet()){
            return
        }
        if (::outputImage.isInitialized){
            lifecycleScope.launch {
                val part = compressFile(outputImage)
                userAction.uploadUserHeadPhoto(part).onStart {
                        showLoad()
                    }.flatMapConcat {
                        if (it.isSuccess()) {
                            GlobalHome.userInfo.imgSrc=it.bigUrl
                            userAction.changeLocalHead(it.bigUrl)
                        }else{
                            flow { emit(false) }
                        }
                    }.catch {
                        it.judgeType().showToast()
                        dismissLoad()
                    }.collect {
                        dismissLoad()
                        val result = "更换" + if (it) {
                            finish()
                            "成功"
                        } else "失败"
                        result.showToast()
                    }
            }
        }else{
            "请选择图片".showToast()
        }
    }

    private fun takePicture() {
        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
        binding.nowHead.load(bitmap)
    }

    private fun operatePhoto() {
        //创建file对象，用于存储拍照后的照片
        outputImage = File(externalCacheDir, "output_image.jpg")
        if (outputImage.exists()) {
            outputImage.delete()
        }
        outputImage.createNewFile()
        imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(this, "$packageName.change.user.head", outputImage)
        } else {
            Uri.fromFile(outputImage)
        }
        //启动相机程序
        requestTakeData.launch(imageUri)
    }
}