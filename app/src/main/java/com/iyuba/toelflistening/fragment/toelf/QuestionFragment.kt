package com.iyuba.toelflistening.fragment.toelf

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.iyuba.toelflistening.AppClient
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.activity.SeekAnalysisActivity
import com.iyuba.toelflistening.bean.AnswerItem
import com.iyuba.toelflistening.bean.ExerciseRecord
import com.iyuba.toelflistening.bean.ExplainItem
import com.iyuba.toelflistening.bean.TestRecordItem
import com.iyuba.toelflistening.databinding.QuestionFragmentBinding
import com.iyuba.toelflistening.fragment.BaseFragment
import com.iyuba.toelflistening.utils.ExtraKeyFactory
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.OtherUtils
import com.iyuba.toelflistening.utils.addCheckItem
import com.iyuba.toelflistening.utils.addRadioButton
import com.iyuba.toelflistening.utils.findIndexSuccess
import com.iyuba.toelflistening.utils.getCheckedItem
import com.iyuba.toelflistening.utils.getSingleCheck
import com.iyuba.toelflistening.utils.getSingleRadio
import com.iyuba.toelflistening.utils.isCompleted
import com.iyuba.toelflistening.utils.judgeType
import com.iyuba.toelflistening.utils.showGoLoginDialog
import com.iyuba.toelflistening.utils.showToast
import com.iyuba.toelflistening.utils.spellVideoUrl
import com.iyuba.toelflistening.utils.startActivity
import com.iyuba.toelflistening.utils.timeStampDate
import com.iyuba.toelflistening.utils.visibilityState
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask
import kotlin.collections.set

/**
苏州爱语吧科技有限公司
 */
class QuestionFragment : BaseFragment<QuestionFragmentBinding>(), TextWatcher {

    private val player = MediaPlayer()
    private val timer = Timer(true)
    private lateinit var currentAnswer: AnswerItem
    private val answerList = mutableListOf<AnswerItem>()
    private val explainList = mutableListOf<ExplainItem>()
    private var answerSimple = false
    private val recordList = mutableListOf<ExerciseRecord>()
    private val answerMap = mutableMapOf<Int, String>()
    private val testRecord = mutableListOf<TestRecordItem>()
    private val secondTestRecord = mutableListOf<TestRecordItem>()
    private val handler = Handler(Looper.myLooper()!!) {
        val current = player.currentPosition
        if (!player.isCompleted()) {
            bind.nowQuestion.text = changeTime(current)
            bind.questionSeek.progress = current
        }
        true
    }


    override fun QuestionFragmentBinding.initBinding() {

        selectItem.addOnTabSelectedListener(tabSelectedListener)
        toeflViewModel.transferExplain.observe(requireActivity()) {
            explainList.addAll(it)
            for (i in explainList.indices) {
                explainList[i].position = (i + 1)
            }
        }
        controlQuestion.setOnClickListener(clickListener)
        verifyResult.setOnClickListener(clickListener)
        submitTestRecord.setOnClickListener(clickListener)
        redo.setOnClickListener(clickListener)
        //异常控件的点击事件
        bind.questionTvError.setOnClickListener {

            if (answerList.isNotEmpty()) {

                toeflViewModel.requestTestRecord(answerList.first().titleNum)
            } else {

                "请重新进入页面".showToast()
                requireActivity().finish()
            }
        }
        observeAnswerList()
        /* if (!toeflViewModel.getReDidFlag()) {
             observeAnswerList()
         }*/
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                toeflViewModel.lastSubmitExerciseResult.collect {
                    it.onError { e ->
                        e.judgeType().showToast()
                        Timber.d(e.message)
                    }.onSuccess {
                        changeClickVisibility(false)
                        "做题记录提交成功".showToast()
//                        toeflViewModel.requestTestRecord(answerList.first().titleNum)
                        toeflViewModel.operateRedo(answerList.first().titleNum, true)
                    }
                }
            }
        }

        bind.questionFlError.visibility = View.GONE
        bind.questionLlContent.visibility = View.GONE
        lifecycleScope.launch {
            toeflViewModel.testRecordFlow.collect { result ->
                result.onSuccess {

                    bind.questionFlError.visibility = View.GONE
                    bind.questionLlContent.visibility = View.VISIBLE
                    //根据TestNumber去重
                    val map = mutableMapOf<String, TestRecordItem>()
                    it.forEach { item ->

                        if (map[item.TestNumber] == null) {

                            map[item.TestNumber] = item
                        }
                    }
                    val list = map.map { item -> item.value }
                    secondTestRecord.apply {
                        clear()
                        addAll(list)
                        sortBy { item -> item.TestNumber }
                    }
                    testRecord.apply {
                        clear()
                        addAll(list)
                        sortBy { item -> item.TestNumber }
                    }
                    //重做等按钮显示
                    val testCompleted = (answerList.size == testRecord.size)
                    changeClickVisibility(!testCompleted)

                    //创建tablayout
                    bind.selectItem.removeAllTabs()
                    answerList.forEach { item ->
                        val text = "第" + item.quesIndex + "题"
                        bind.selectItem.apply {
                            addTab(newTab().setText(text))
                        }
                    }

                    //处理
                    /*                    val selectedPos = bind.selectItem.selectedTabPosition
                                        //当有做题记录时，需要设置当前题目的选择项，使得选中
                                        if (selectedPos != -1 && selectedPos < testRecord.size) {//位置正确

                                            val answer = testRecord[selectedPos].UserAnswer
                                            if (answer.length <= 1) {//单选

                                                val index =
                                                    OtherUtils.selectArray.indexOfFirst { index -> index == answer }
                                                if (index.findIndexSuccess()) {

                                                    bind.questionOptions.apply {

                                                        clearCheck()
                                                        val id = getSingleRadio(true)[index].id
                                                        check(id)
                                                    }
                                                }
                                            } else {//多选

                                                answer.forEach { c ->

                                                    val index =
                                                        OtherUtils.selectArray.indexOfFirst { index -> index == c.toString() }
                                                    if (index.findIndexSuccess()) {

                                                        bind.questionOptionsMany.apply {

                                                            val id = getSingleCheck()[index]
                                                            id.isChecked = true;
                                                        }
                                                    }
                                                }
                                            }//多选
                                        }*/
                }.onError {
                    it.judgeType().showToast()

                    //显示题目
                    bind.questionFlError.visibility = View.GONE
                    bind.questionLlContent.visibility = View.VISIBLE
                    //创建tablayout
                    bind.selectItem.removeAllTabs()
                    answerList.forEach { item ->
                        val text = "第" + item.quesIndex + "题"
                        bind.selectItem.apply {
                            addTab(newTab().setText(text))
                        }
                    }
                    changeClickVisibility(true)
                }
            }
        }
    }

    private fun changeClickVisibility(flag: Boolean = true) {
        bind.apply {
            verifyResult.visibilityState(flag)
            redo.visibilityState(flag)
            submitTestRecord.visibilityState(!flag)
        }
    }

    private fun observeAnswerList() {

        toeflViewModel.transferAnswer.observe(requireActivity()) {

            answerList.addAll(it)
            //请求做题记录
            toeflViewModel.requestTestRecord(answerList.first().titleNum)
        }
    }

    private fun inflateData(item: AnswerItem) {

        bind.questionOptions.removeAllViews()
        bind.questionOptionsMany.removeAllViews()

        bind.questionEvAnswer.removeTextChangedListener(this)
        bind.questionOptions.setOnCheckedChangeListener(null)

        answerSimple = item.answer.split(OtherUtils.delimiter).size <= 1
        val answerCount = if (answerSimple) "单" else "多"
        bind.question = "(${answerCount}选)Q" + item.quesIndex + ":" + item.quesText

        if (item.testType == "103") {//多选的填空题
            //103需要添加必要的说明
            bind.question =
                "(${answerCount}选)Q" + item.quesIndex + ":" + item.quesText + "(每道小题从左到右分别代表A/B)"

            bind.questionEvAnswer.visibility = View.VISIBLE
            bind.questionIvImg.visibility = View.VISIBLE
            //加载题为图片的情况
            Glide.with(AppClient.context)
                .load("http://staticvip.iyuba.cn/images/toefl/answer/" + item.quesImage)
                .into(bind.questionIvImg)
        } else {

            bind.questionEvAnswer.visibility = View.GONE;
            bind.questionIvImg.visibility = View.GONE;
            if (answerSimple) {
                bind.questionOptions.addRadioButton(requireContext(), item.answerText)
            } else {
                bind.questionOptionsMany.addCheckItem(requireContext(), item.answerText)
            }
        }


        testRecord.indexOfFirst {
            it.TestNumber == item.quesIndex
        }.let {
            if (it.findIndexSuccess()) {

                if (answerSimple) {//单选

                    val index =
                        OtherUtils.selectArray.indexOfFirst { index -> index == testRecord[it].UserAnswer }
                    if (index.findIndexSuccess()) {

                        bind.questionOptions.apply {

                            clearCheck()
                            val id = getSingleRadio(true)[index].id
                            check(id)
                        }
                    }
                } else {//多选

                    //多选，遍历答案
                    testRecord[it].UserAnswer.forEach { c ->

                        val index =
                            OtherUtils.selectArray.indexOfFirst { index -> index == c.toString() }
                        if (index.findIndexSuccess()) {

                            bind.questionOptionsMany.apply {

                                val id = getSingleCheck()[index]
                                id.isChecked = true;
                            }
                        }
                    }
                }
            }
        }
        currentAnswer = item
        readyPlay(item.sound)
    }

    private fun readyPlay(url: String, flag: Boolean = false) {
        try {
            player.apply {
                if (isPlaying) pause()
                stop()
                reset()
                setDataSource(url.spellVideoUrl())
                prepareAsync()
                setOnPreparedListener {
                    val duration = player.duration
                    bind.questionSeek.max = duration
                    bind.sumQuestion.text = changeTime(duration)
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            handler.sendEmptyMessage(0)
                        }
                    }, 0, 100)
                    if (flag) {
                        player.start()
                        bind.controlQuestion.setBackgroundResource(R.drawable.pause_small)
                    } else {
                        bind.controlQuestion.setBackgroundResource(R.drawable.start_small)
                    }
                }
                setOnCompletionListener {
                    bind.nowQuestion.text = getString(R.string.default_time)
                    bind.controlQuestion.setBackgroundResource(R.drawable.start_small)
                    bind.questionSeek.progress = 0
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            player.apply {
                pause()
                reset()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        bind.controlQuestion.setBackgroundResource(R.drawable.start_small)
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            player.apply {
                stop()
                reset()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        timer.cancel()
        handler.removeCallbacksAndMessages(null)
    }


    private val clickListener = View.OnClickListener {
        when (it.id) {
            //口语圈
            R.id.control_question -> {
                if (player.isPlaying) {
                    player.pause()
                    bind.controlQuestion.setBackgroundResource(R.drawable.start_small)
                } else {
                    if (player.isCompleted()) {

                        readyPlay(currentAnswer.sound, true)
                    } else {
                        player.start()
                    }
                    bind.controlQuestion.setBackgroundResource(R.drawable.pause_small)
                }
            }

            R.id.verify_result -> seekVerifyResult()
            //重做本地标记，重做后查看成绩崩溃
            R.id.redo -> startReDo()
            R.id.submit_test_record -> submitExerciseRecord()
        }
    }

    /**
     * 重做
     * */
    private fun startReDo() {
        recordList.clear()
        answerMap.clear()
        testRecord.clear()
        changeClickVisibility()
        bind.selectItem.getTabAt(0)?.select()
        bind.questionOptions.clearCheck()
        if (answerList.isNotEmpty()) {
            inflateData(answerList.first())
        }
        toeflViewModel.operateRedo(answerList.first().titleNum, false)
    }

    /**
     * 查看做题结果
     * */
    private fun seekVerifyResult() {
        recordList.last().apply {
            if (UserAnswer.isEmpty()) {
                UserAnswer = secondTestRecord.last().UserAnswer
                recordList[recordList.lastIndex] = this
            }
        }
        val list = ArrayList<ExerciseRecord>()

        if (secondTestRecord.isEmpty()) {

            recordList.forEach {

                val item = ExerciseRecord(
                    TestNumber = it.TestNumber,
                    UserAnswer = it.UserAnswer,
                    RightAnswer = it.RightAnswer
                )
                list.add(item)
            }
        } else {

            secondTestRecord.forEach {
                val item = ExerciseRecord(
                    TestNumber = it.TestNumber,
                    UserAnswer = it.UserAnswer,
                    RightAnswer = it.RightAnswer
                )
                list.add(item)
            }
        }


        for (i in answerList.indices) {
            list[i].title = answerList[i].quesText
            list[i].analysis = explainList[i].explains
        }
        requireActivity().startActivity<SeekAnalysisActivity> {
            putParcelableArrayListExtra(ExtraKeyFactory.viewAnalysisList, list)
        }
    }

    private fun submitClick(item: AnswerItem, position: Int) {
        /*  val list = explainList.filter {
              it.position.toString() == currentAnswer.quesIndex
          }*/
        /*        val currentExplain = if (list.isEmpty()) explainList[position] else list[0]
                val nextLine = "\n"*/
        /*        val text = currentExplain.let {
                    if (it.explains.contains(nextLine)) {
                        it.explains.replace(nextLine, "")
                    } else {
                        it.explains
                    }
                }*/
        /*val jieOne = "解"
        val jieSecond = "接"
        val proper = when {
            text.contains(jieOne) -> with(text) {
                substring(indexOf("：") + 1, indexOf(jieOne))
            }
            text.contains(jieSecond) -> with(text) {
                substring(indexOf("：") + 1, indexOf(jieSecond))
            }
            else -> ""
        }*/

        val answerList = item.answer
            .replace("1", "A")
            .replace("2", "B")
            .replace("3", "C")
            .replace("4", "D")
            .replace("5", "E")
            .replace("++", ",")
        val proper = answerList
        val addFlag = recordList.indexOfFirst { it.TestNumber == item.quesIndex }
        if (addFlag == -1) {
            val exerciseItem = ExerciseRecord(
                LessonId = item.titleNum,
                TestNumber = item.quesIndex,
                BeginTime = System.currentTimeMillis().timeStampDate(),
                RightAnswer = proper
            ).let {
                it.title = item.quesText
                it.analysis = explainList[position].explains
                it
            }
            recordList.add(exerciseItem)
        }
    }

    private fun submitExerciseRecord() {
        if (!GlobalHome.isLogin()) {
            requireActivity().showGoLoginDialog()
            return
        }
        if (recordList.filter { it.UserAnswer.isNotEmpty() }.size != answerList.size) {
            "还有题目未完成,请先答题".showToast()
            return
        }
        recordList.forEach {
            if (it.TestTime.isEmpty()) {
                it.TestTime = System.currentTimeMillis().timeStampDate()
            }
            it.AnswerResut = (if (it.UserAnswer == it.RightAnswer) "1" else "0")
        }
        toeflViewModel.submitExerciseRecord(recordList)
    }

    private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {

        override fun onTabSelected(tab: TabLayout.Tab) {

            val position = tab.position
            val item = answerList[position]
            inflateData(item)
            submitClick(item, position)
            val testNumber =
                recordList.indexOfFirst { it.TestNumber == answerList[position].quesIndex }

//            bind.questionOptions.clearCheck()

            //还是得通过题号来判断,根据获取的做题记录来显示答案
            if (answerSimple) {//单选

                val simpleIndex =
                    OtherUtils.selectArray.indexOfFirst { it == recordList[testNumber].UserAnswer }
                if (simpleIndex.findIndexSuccess()) {

                    bind.questionOptions.clearCheck()
                    bind.questionOptions.apply {
                        val radio = getSingleRadio(true)[simpleIndex]
                        radio.isChecked = true
                    }
                }
            } else {//多选

                if (item.testType == "103") {


                    bind.questionEvAnswer.apply {

                        val a = recordList[testNumber].UserAnswer
                        setText(a)
                    }
                } else {

                    answerMap[testNumber]?.let {
                        for (i in it.indices) {
                            OtherUtils.selectArray.indexOfFirst { str ->
                                str == it[i].toString()
                            }.apply {
                                if (findIndexSuccess()) {
                                    bind.questionOptionsMany.getSingleCheck()[this].isChecked = true
                                }
                            }
                        }
                    }
                }
            }
            //通过点击监听来获取答案，而不是onTabUnselected
            if (answerSimple) {//单选

                bind.questionOptions.setOnCheckedChangeListener { _, i ->

                    if (i.findIndexSuccess()) {
                        addRecordList(position, OtherUtils.selectArray[i])
                    }
                }
            } else {//多选

                if (item.testType == "103") {//是不是填空题


                    bind.questionEvAnswer.addTextChangedListener(this@QuestionFragment)
                    //保存用户的作答
                    /*var str = v.toString()
                    str = str.replace("，", ",")
                    addRecordList(position, str)*/

                } else {

                    val list = bind.questionOptionsMany.getSingleCheck()
                    for (i in list.indices) {
                        list[i].setOnCheckedChangeListener { _, _ ->
                            addRecordList(position, bind.questionOptionsMany.getCheckedItem())
                        }
                    }
                }
            }

        }

        override fun onTabUnselected(tab: TabLayout.Tab) {

        }

        override fun onTabReselected(tab: TabLayout.Tab) {

        }
    }


    private fun addRecordList(position: Int, currAnswer: String) {
        val index = recordList.indexOfFirst { it.TestNumber == answerList[position].quesIndex }
        if (index.findIndexSuccess()) {
            recordList[index].apply {
                UserAnswer = currAnswer
                TestTime = System.currentTimeMillis().timeStampDate()
            }
            answerMap[index] = currAnswer
        }
    }

    override fun onResume() {
        super.onResume()
        if (GlobalHome.exerciseResultBack) {
            return
        }
        GlobalHome.exerciseResultBack = true
        readyPlay(currentAnswer.sound)
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    /**
     * TextWatcher的回调
     */
    override fun afterTextChanged(p0: Editable?) {


        addRecordList(bind.selectItem.selectedTabPosition, p0.toString())
    }

}