package com.iyuba.toelflistening.utils.logic

/**
苏州爱语吧科技有限公司
@Date:  2023/1/3
@Author:  han rong cheng
 */
enum class VoiceStatus {
    /**
     * 后台播放
     * */
    BACK_PLAY,
    /**
     * 单词闯关
     * */
    BREAK_WORD,
    /**
     * 生词本
     * */
    WORD_BOOK,
    /**
     * 评测等的声音
     * */
    EVAL,
    /**
     * 某些错误
     * */
    ERROR,

    //------------------------------------为了区分在同一个界面多种音频播放时不同的Prepare逻辑------------------------------------
    /**
     * 评测合成
     * */
    EVAL_MERGE,
    /**
     * 评测句子原音
     * */
    EVAL_ORIGINAL,
    /**
     * 评测自己的声音
     * */
    EVAL_SELF;

    override fun toString() = when (this) {
        BACK_PLAY -> "后台播放"
        BREAK_WORD -> "单词闯关"
        WORD_BOOK -> "生词本"
        EVAL -> "评测等的声音"
        ERROR -> "某些错误"
        EVAL_MERGE -> "评测合成"
        EVAL_ORIGINAL -> "评测句子原音"
        EVAL_SELF -> "评测自己的声音"
    }

}