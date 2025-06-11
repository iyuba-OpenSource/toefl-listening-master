package com.iyuba.toelflistening.utils;

import android.content.Context;
import android.util.Log;

import com.bun.miitmdid.core.InfoCode;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;
import com.bun.miitmdid.pojo.IdSupplierImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 苏州爱语吧科技有限公司
 *
 * @Date: 2023/1/29
 * @Author: han rong cheng
 */
public class YouDaoHelper implements IIdentifierListener {
    public static final String TAG = "DemoHelper";
    public static final int HELPER_VERSION_CODE = 20220815; // DemoHelper 版本号
    private final AppIdsUpdater appIdsUpdater;
    private boolean isCertInit = false;
    public boolean isSDKLogOn = true; // TODO （1）设置 是否开启 sdk 日志
    public static final String ASSET_FILE_NAME_CERT = "com.example.oaidtest2.cert.pem"; // TODO （2）设置 asset 证书文件名
    public YouDaoHelper(AppIdsUpdater appIdsUpdater){
        // TODO （3）加固版本在调用前必须载入 SDK 安全库,因为加载有延迟，推荐在 application 中调用 loadLibrary 方法
         System.loadLibrary("msaoaidsec");
        // DemoHelper 版本建议与 SDK 版本一致
        if(MdidSdkHelper.SDK_VERSION_CODE != HELPER_VERSION_CODE){
            Log.w(TAG,"SDK version not match.");
        }
        this.appIdsUpdater = appIdsUpdater;
    }
    public void getDeviceIds(Context cxt){
        getDeviceIds(cxt, true, true, true);
    }
    /**
     * 获取 OAID
     * @param cxt
     */
    public void getDeviceIds(Context cxt,boolean isGetOAID,boolean
            isGetVAID,boolean isGetAAID){
        // TODO （4）初始化 SDK 证书
        if(!isCertInit){ // 证书只需初始化一次
            // 证书为 PEM 文件中的所有文本内容（包括首尾行、换行符）
            try {
                isCertInit = MdidSdkHelper.InitCert(cxt, loadPemFromAssetFile(cxt,
                        ASSET_FILE_NAME_CERT));
            } catch (Error e) {
                e.printStackTrace();
            }
            if(!isCertInit){
                Log.w(TAG, "getDeviceIds: cert init failed");
            }
        }
        //（可选）设置 InitSDK 接口回调超时时间(仅适用于接口为异步)，默认值为5000ms.
        // 注：请在调用前设置一次后就不再更改，否则可能导致回调丢失、重复等问题
        try {
            MdidSdkHelper.setGlobalTimeout(5000);
        } catch (Error error) {
            error.printStackTrace();
        }
        int code = 0;
        // TODO （5）调用 SDK 获取 ID
        try {
            code = MdidSdkHelper.InitSdk(cxt, isSDKLogOn, isGetOAID, isGetVAID,
                    isGetAAID, this);
        } catch (Error error) {
            error.printStackTrace();
        }
        // TODO （6）根据 SDK 返回的 code 进行不同处理
        IdSupplierImpl unsupportedIdSupplier = new IdSupplierImpl();
        if(code == InfoCode.INIT_ERROR_CERT_ERROR){
            // 证书未初始化或证书无效，SDK 内部不会回调 onSupport
            // APP 自定义逻辑
            Log.w(TAG,"cert not init or check not pass");
            onSupport(unsupportedIdSupplier);
        }else if(code == InfoCode.INIT_ERROR_DEVICE_NOSUPPORT){
            // 不支持的设备, SDK 内部不会回调 onSupport
            // APP 自定义逻辑
            Log.w(TAG,"device not supported");
            onSupport(unsupportedIdSupplier);
        }else if( code == InfoCode.INIT_ERROR_LOAD_CONFIGFILE){
            // 加载配置文件出错, SDK 内部不会回调 onSupport
            // APP 自定义逻辑
            Log.w(TAG,"failed to load config file");
            onSupport(unsupportedIdSupplier);
        }else if(code == InfoCode.INIT_ERROR_MANUFACTURER_NOSUPPORT){
            // 不支持的设备厂商, SDK 内部不会回调 onSupport
            // APP 自定义逻辑
            Log.w(TAG,"manufacturer not supported");
            onSupport(unsupportedIdSupplier);
        }else if(code == InfoCode.INIT_ERROR_SDK_CALL_ERROR){
            // sdk 调用出错, SSDK 内部不会回调 onSupport
            // APP 自定义逻辑
            Log.w(TAG,"sdk call error");
            onSupport(unsupportedIdSupplier);
        } else if(code == InfoCode.INIT_INFO_RESULT_DELAY) {
            // 获取接口是异步的，SDK 内部会回调 onSupport
            Log.i(TAG, "result delay (async)");
        }else if(code == InfoCode.INIT_INFO_RESULT_OK){
            // 获取接口是同步的，SDK 内部会回调 onSupport
            Log.i(TAG, "result ok (sync)");
        }else {
            // sdk 版本高于 DemoHelper 代码版本可能出现的情况，无法确定是否调用onSupport
            // 不影响成功的 OAID 获取
            Log.w(TAG,"getDeviceIds: unknown code: " + code);
        }
    }
    /**
     * APP 自定义的 getDeviceIds(Context cxt)的接口回调
     * @param supplier
     */
    @Override
    public void onSupport(IdSupplier supplier) {
        if(supplier==null) {
            Log.w(TAG, "onSupport: supplier is null");
            return;
        }
        if(appIdsUpdater ==null) {
            Log.w(TAG, "onSupport: callbackListener is null");
            return;
        }
        // 获取 Id 信息
        // 注：IdSupplier 中的内容为本次调用 MdidSdkHelper.InitSdk()的结果，不会实时更新。 如需更新，需调用 MdidSdkHelper.InitSdk()
        boolean isSupported = supplier.isSupported();
        boolean isLimited = supplier.isLimited();
        String oaid=supplier.getOAID();
        String vaid=supplier.getVAID();
        String aaid=supplier.getAAID();
        //TODO (7) 自定义后续流程，以下显示到 UI 的示例
        String idsText= "support: " + (isSupported ? "true" : "false") +
                "\nlimit: " + (isLimited ? "true" : "false") +
                "\nOAID: " + oaid +
                "\nVAID: " + vaid +
                "\nAAID: " + aaid + "\n";
        Log.d(TAG, "onSupport: ids: \n" + idsText);
        appIdsUpdater.onIdsValid(idsText);
    }
    public interface AppIdsUpdater{
        void onIdsValid(String ids);
    }
    /**
     * 从 asset 文件读取证书内容
     * @param context
     * @param assetFileName
     * @return 证书字符串
     */
    public static String loadPemFromAssetFile(Context context, String assetFileName){
        try {
            InputStream is = context.getAssets().open(assetFileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null){
                builder.append(line);
                builder.append('\n');
            }
            return builder.toString();
        } catch (IOException e) {
            Log.e(TAG, "loadPemFromAssetFile failed");
            return "";
        }
    }
}
