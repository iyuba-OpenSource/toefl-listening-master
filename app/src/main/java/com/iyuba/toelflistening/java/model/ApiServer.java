package com.iyuba.toelflistening.java.model;

import com.iyuba.toelflistening.java.entity.ExamRecordPost;
import com.iyuba.toelflistening.java.model.bean.AdEntryBean;
import com.iyuba.toelflistening.java.model.bean.EvalBean;
import com.iyuba.toelflistening.java.model.bean.ExamWordBean;
import com.iyuba.toelflistening.java.model.bean.SyncDataBean;
import com.iyuba.toelflistening.java.model.bean.UserInfoResponse;
import com.iyuba.toelflistening.java.model.bean.WordCollectBean;
import com.iyuba.toelflistening.java.model.bean.WxLoginBean;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiServer {


    /**
     * 获取微信小程序的token
     *
     * @param platform
     * @param format
     * @param protocol
     * @param appid
     * @param sign
     * @return
     */
/*    @GET("/v2/api.iyuba")
    Observable<WxLoginBean> getWxAppletToken(@Query("platform") String platform, @Query("format") String format,
                                             @Query("protocol") String protocol, @Query("appid") String appid,
                                             @Query("sign") String sign);*/


    /**
     * 通过获取token获取uid
     *
     * @param platform
     * @param format
     * @param protocol
     * @param token
     * @return
     */
 /*   @GET("/v2/api.iyuba")
    Observable<WxLoginBean> getUidByToken(@Query("platform") String platform, @Query("format") String format,
                                          @Query("protocol") String protocol, @Query("token") String token, @Query("sign") String sign,
                                          @Query("appid") String appid);*/


    /**
     * 获取能力测试试题
     *
     * @param url
     * @param protocol
     * @param lesson
     * @param category
     * @param sign
     * @param format
     * @param mode
     * @param uid
     * @param lessonid
     * @return
     */
/*    @GET
    Observable<CapabilityBean> getQuestion(@Url String url, @Query("protocol") int protocol, @Query("lesson") String lesson,
                                           @Query("category") String category, @Query("sign") String sign, @Query("format") String format,
                                           @Query("mode") int mode, @Query("uid") int uid, @Query("lessonid") int lessonid);*/


    /**
     * 练习或者测评 上传答题结果
     *
     * @param url
     * @param submitQuestion
     * @return
     */
    /*@POST
    Observable<UpdateExamRecordBean> updateExamRecordNew(@Url String url, @Body SubmitQuestion submitQuestion);*/


    /**
     * 同步联练习和评测的数据
     *
     * @param url
     * @param format
     * @param appId
     * @param uid
     * @param lesson
     * @param TestMode
     * @param mode
     * @param sign
     * @return
     */
    /*@GET
    Observable<SyncDataBean> getExamDetail(@Url String url, @Query("format") String format, @Query("appId") int appId, @Query("uid") int uid,
                                           @Query("lesson") String lesson, @Query("TestMode") String TestMode, @Query("mode") int mode,
                                           @Query("sign") String sign);*/


    /**
     * 同步联练习和评测的数据
     *
     * @param url
     * @param format
     * @param appId
     * @param uid
     * @param lesson
     * @param TestMode
     * @param mode
     * @param sign
     * @return
     */
    @GET
    Observable<SyncDataBean> getExamDetail(@Url String url, @Query("format") String format, @Query("appId") int appId, @Query("uid") int uid,
                                           @Query("lesson") String lesson, @Query("TestMode") String TestMode, @Query("mode") int mode,
                                           @Query("sign") String sign);

    /**
     * 获取雅思听力的单词
     *
     * @param type
     * @param level
     * @param wordNumber
     * @return
     */
    @GET
    Observable<ExamWordBean> getExamWord(@Url String url, @Query("type") String type, @Query("level") int level, @Query("wordNumber") int wordNumber);


    /**
     * 收藏和取消收藏单词
     *
     * @param url
     * @param groupName
     * @param mod
     * @param word
     * @param userId
     * @return
     */
    @GET
    Observable<WordCollectBean> updateWord(@Url String url, @Query("groupName") String groupName, @Query("mod") String mod,
                                           @Query("word") String word, @Query("userId") String userId, @Query("format") String format);


    /**
     * 评测
     *
     * @param requestBody
     * @return
     */
    @POST
    Observable<EvalBean> eval(@Url String url, @Body RequestBody requestBody);

    /**
     * 上传做题记录
     *
     * @param url
     * @param bean
     * @return
     */
    @POST
    Observable<ResponseBody> updateExamRecord(@Url String url, @Body ExamRecordPost bean);


    /**
     * 获取微信小程序的token
     *
     * @param platform
     * @param format
     * @param protocol
     * @param appid
     * @param sign
     * @return
     */
    @GET
    Observable<WxLoginBean> getWxAppletToken(@Url String url, @Query("platform") String platform, @Query("format") String format,
                                             @Query("protocol") String protocol, @Query("appid") String appid,
                                             @Query("sign") String sign);


    /**
     * 通过获取token获取uid
     *
     * @param platform
     * @param format
     * @param protocol
     * @param token
     * @return
     */
    @GET
    Observable<WxLoginBean> getUidByToken(@Url String url, @Query("platform") String platform, @Query("format") String format,
                                          @Query("protocol") String protocol, @Query("token") String token, @Query("sign") String sign,
                                          @Query("appid") String appid);

    /**
     * 获取用户数据
     *
     * @param platform
     * @param format
     * @param appid
     * @param protocol
     * @param id
     * @param myid
     * @param sign
     * @return
     */
    @GET
    Observable<UserInfoResponse> getUserInfo(@Url String url, @Query("platform") String platform, @Query("format") String format, @Query("appid") String appid,
                                             @Query("protocol") String protocol, @Query("id") String id,
                                             @Query("myid") String myid, @Query("sign") String sign);

    /**
     * 获取广告
     *
     * @param appId
     * @param flag  2 广告顺序  5自家广告内容
     * @param uid
     * @return
     */
    @GET
    Observable<List<AdEntryBean>> getAdEntryAll(@Url String url, @Query("appId") String appId, @Query("flag") int flag, @Query("uid") String uid);
}
