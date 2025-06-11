package com.iyuba.toelflistening.utils.logic

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

class ChoosePhotoContracts : ActivityResultContract<Unit, Uri>() {
    //打开相册选择单张图片
    override fun createIntent(context: Context, input: Unit?): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType("image/*")
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (intent == null || resultCode != Activity.RESULT_OK) return null
        return intent.data
    }
}