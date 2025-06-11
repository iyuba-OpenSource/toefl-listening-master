package com.iyuba.toelflistening.java;

public class Constant {




    /**
     * 微信key
     */
    public final static String WX_KEY = "wx29ba260785a1e6ba";


    public static String DOMAIN = "iyuba.cn";
    public static String DOMAIN_LONG = "iyuba.com.cn";


    /**
     * 获取域名
     */
    public final static String HOST_URL = "http://111.198.52.105:8085";

    public static String URL_API = "http://api." + DOMAIN_LONG;


    /**
     * 2022 4 25 从日语系列搬迁过来
     */
    public static String API_CN_URL = "http://api." + DOMAIN;


    public static String API_COM_CN_URL = "http://api." + DOMAIN_LONG;

    /**
     * 购买vip
     */
    public static String URL_VIP = "http://vip." + DOMAIN;


    public static String AIS_URL = "http://ai." + DOMAIN;

    public static String AI_URL = "http://ai." + DOMAIN;


    public static String VOA_URL = "http://voa." + DOMAIN;

    public static String USERSPEECH_URL = "http://iuserspeech." + DOMAIN + ":9001";

    public static String STATICVIP2_URL = "http://staticvip2." + DOMAIN;

    public static String STATIC2_URL = "http://static2." + DOMAIN;

    public static String STATIC1_URL = "http://static1." + DOMAIN;

    public static String APPS_URL = "https://apps." + DOMAIN;

    public static String WORD_URL = "http://word." + DOMAIN;

    public static String M_URL = "http://m." + DOMAIN;

    public static String DAXUE_URL = "http://daxue." + DOMAIN;

    public static String APP_URL = "http://app." + DOMAIN;


    public static String CLASS_URL = "http://class." + DOMAIN;


    public static String DEV_URL = "http://dev." + DOMAIN;

    /**
     * 广告接口
     */
    public static String GET_AD_ENTRY_ALL = Constant.DEV_URL + "/getAdEntryAll.jsp";

    /**
     * 有端口的地址
     */
    public static String PROTOCOL_URL = API_COM_CN_URL + "/v2/api.iyuba";
    /**
     * 获取雅思的单词
     */
    public static String GET_EXAM_WORD = APPS_URL + "/concept/getExamWord.jsp";

    /**
     * 自定义appid，与购买会员有关
     * 与personal.aar
     */
    public final static int APPID = 220 ;


    /**
     * 注册链接
     */
    public static String REGIST_URL = "http://api.+" + DOMAIN_LONG + "/v2/api.iyuba";


    //验证手机号是否注册
    public static String Vertify_PHONENUM = Constant.URL_API + "/sendMessage3.jsp?format=json";


    /**
     * sp存储地址的文件名
     */
    public static final String SP_NAME_ADDRESS = "SERVER_ADDRESS";

    /**
     * sp的key   主要是用来存储数据的 短地址
     */
    public static final String SP_KEY_ADDRESS = "ADDRESS";

    /**
     * sp的key   主要是用来存储数据的 长地址
     */
    public static final String SP_KEY_ADDRESS_LONG = "ADDRESS_LONG";


    /**
     * 存储单词闯关单词数的文件名
     */
    public final static String SP_BREAK_THROUGH = "BREAK_THROUGH";

    /**
     * 存储单词闯关单词数的key
     */
    public final static String SP_KEY_WORD_NUM = "WORD_NUM";


    /**
     * 隐私协议的确认状态
     */
    public final static String SP_PERMISSION = "PERMISSION";

    public final static String SP_KEY_PERMISSION_RECORD = "RECORD_AUDIO";


    /**
     * 评测
     */
    public static String EVAL_URL = USERSPEECH_URL + "/test/ai10/";


    //口语报告
    public static String TONGJI_SPEAK = AI_URL + "/management/getHomePage.jsp?";

    //口语详情 http://ai.iyuba.cn/management/getDetailInfo.jsp?userId=7813602&newstype=concept&language=English&lastId=0&pageCounts=15
    public static String TONGJI_SPEAK_DETAIL = AI_URL + "/management/getDetailInfo.jsp?";

    // 听力详情 http://daxue.iyuba.cn/ecollege/getStudyRecordByTestMode.jsp?uid=7813602&TestMode=1&Pageth=1&NumPerPage=15&sign=67c6c5a67a2acd1e0fe085da1bddd970
    public static String TONGJI_LISTEN_DETAIL = DAXUE_URL + "/ecollege/getStudyRecordByTestMode.jsp?";

    //单词报告详情 http://daxue.iyuba.cn/ecollege/getPassHomePage.jsp?chooseType=detail&pageNumber=1&pageCounts=20&userId=6307010&Lesson=NewConcept1
    public static String TONGJI_WORD_DETAIL = DAXUE_URL + "/ecollege/getPassHomePage.jsp?";

    /**
     * 习题统计功能
     */
    public static String TONGJI_TEST_RANKING = DAXUE_URL + "/ecollege/getTestRanking.jsp";


    //获取打卡历史记录
    public static String GET_CALENDAR_URL = APP_URL + "/getShareInfoShow.jsp";


    /**
     * 能力图谱获取试题
     */
    public static String GET_QUESTION = CLASS_URL + "/getClass.iyuba";

    /**
     * 练习和评测
     * 上传做题记录
     */
    public static String UPDATE_EXAM_RECORD_NEW = DAXUE_URL + "/ecollege/updateExamRecordNew.jsp";


    /**
     * 同步练习和测评的数据
     */
    public static String GET_EXAM_DETAIL = DAXUE_URL + "/ecollege/getExamDetail.jsp";


    /**
     * 取消收藏和收藏单词
     */
    public static String UPDATE_WORD = Constant.WORD_URL + "/words/updateWord.jsp";


    /**
     * 上床做题记录
     */
    public static String UPDATE_EXAM_RECORD = DAXUE_URL + "/ecollege/updateExamRecord.jsp";


    /**
     * 由于与能力训练、能力测拼同步数据冲突，所以单独给单词闯关设置CATEGORY_TYPE
     */
    public final static String BREAK_CATEGORY_TYPE = "ieltsBreak";


    //广告
    public static final String AD_ADS1 = "ads1";//倍孜
    public static final String AD_ADS2 = "ads2";//创见
    public static final String AD_ADS3 = "ads3";//头条穿山甲
    public static final String AD_ADS4 = "ads4";//广点通优量汇
    public static final String AD_ADS5 = "ads5";//快手


    /**
     * 更新地址
     */
    public static void updateAddress() {

        REGIST_URL = "http://api." + DOMAIN_LONG + "/v2/api.iyuba";
        URL_API = "http://api." + DOMAIN_LONG;
        API_CN_URL = "http://api." + DOMAIN;
        URL_VIP = "http://vip." + DOMAIN;
        AIS_URL = "https://ai." + DOMAIN;
        AI_URL = "http://ai." + DOMAIN;
        VOA_URL = "http://voa." + DOMAIN;
        USERSPEECH_URL = "http://iuserspeech." + DOMAIN + ":9001";
        STATICVIP2_URL = "http://staticvip2." + DOMAIN;
        STATIC2_URL = "http://static2." + DOMAIN;
        APPS_URL = "https://apps." + DOMAIN;
        WORD_URL = "http://word." + DOMAIN;
        M_URL = "http://m." + DOMAIN;
        DAXUE_URL = "http://daxue." + DOMAIN;
        Vertify_PHONENUM = Constant.URL_API + "/sendMessage3.jsp?format=json";

        API_COM_CN_URL = "http://api." + DOMAIN_LONG;

        GET_CALENDAR_URL = APP_URL + "/getShareInfoShow.jsp";
    }

}
