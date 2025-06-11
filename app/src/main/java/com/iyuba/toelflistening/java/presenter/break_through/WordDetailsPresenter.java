package com.iyuba.toelflistening.java.presenter.break_through;


import com.iyuba.toelflistening.java.Constant;
import com.iyuba.toelflistening.java.model.bean.EvalBean;
import com.iyuba.toelflistening.java.model.bean.WordCollectBean;
import com.iyuba.toelflistening.java.model.break_through.WordDetailsModel;
import com.iyuba.toelflistening.java.presenter.BasePresenter;
import com.iyuba.toelflistening.java.view.WordDetailsContract;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class WordDetailsPresenter extends BasePresenter<WordDetailsContract.WordDetailsView, WordDetailsContract.WordDetailsModel>
        implements WordDetailsContract.WordDetailsPresenter {

    @Override
    protected WordDetailsContract.WordDetailsModel initModel() {
        return new WordDetailsModel();
    }

    @Override
    public void updateWord(String url, String groupName, String mod, String word, String userId, String format, String rowid) {

        Disposable disposable = model.updateWord(url, groupName, mod, word, userId, format, new WordDetailsContract.WordCollectCallback() {
            @Override
            public void success(WordCollectBean wordCollectBean) {

                if (wordCollectBean.getResult() == 1) {

                    view.updateCollectComplete(rowid, mod);
                } else {

                }
            }

            @Override
            public void error(Exception e) {

                if (e instanceof UnknownHostException) {

                    view.toast("请求超时");
                }
            }
        });
        addSubscribe(disposable);
    }

    @Override
    public void eval(RequestBody requestBody) {

        Disposable disposable = model.eval(Constant.EVAL_URL,requestBody, new WordDetailsContract.EvalCallback() {
            @Override
            public void success(EvalBean evalBean) {

                if (evalBean.getResult().equals("1")) {

                    view.eval(evalBean);
                } else {

                    view.toast(evalBean.getMessage());
                }
            }

            @Override
            public void error(Exception e) {

                if (e instanceof UnknownHostException) {

                    view.toast("请求超时");
                }
            }
        });
        addSubscribe(disposable);
    }

    private Map<String, RequestBody> getMap(int userId, int IdIndex, int paraId, int newsId, String type, String filePath, String sentence) {


        File file = new File(filePath);

        Map<String, RequestBody> params = new HashMap<>();
        params.put("userId", toRequestBody(userId + ""));
        params.put("IdIndex", toRequestBody(IdIndex + ""));
        params.put("paraId", toRequestBody(paraId + ""));
        params.put("newsId", toRequestBody(newsId + ""));
        params.put("type", toRequestBody(type));
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        params.put("file\";filename=\"" + file.getName(), requestFile);
        String urlSentence = null;
        try {
            urlSentence = URLEncoder.encode(sentence, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlSentence = urlSentence.replaceAll("\\+", "%20");
        params.put("sentence", toRequestBody(urlSentence));

        return params;
    }

    private RequestBody toRequestBody(String param) {
        return RequestBody.create(MediaType.parse("text/plain"), param);
    }
}
