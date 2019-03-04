package com.zqb.baselibrary.request.observer;


import android.util.Log;
import com.allen.library.base.BaseObserver;
import com.allen.library.bean.BaseBean;
import io.reactivex.disposables.Disposable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Allen on 2017/5/3.
 *
 * @author Allen
 * 通用的Observer
 * 用户可以根据自己需求自定义自己的类继承BaseObserver<T>即可
 */

public abstract class CommonObserver<T> extends BaseObserver<T> {

    /**
     * 失败回调
     *
     * @param errorMsg
     */
    protected abstract void onError(String errorMsg);

    /**
     * 成功回调
     *
     * @param t
     */
    protected abstract void onSuccess(T t);


    @Override
    public void doOnSubscribe(Disposable d) {
    }

    @Override
    public void doOnError(String errorMsg) {
        onError(errorMsg);
    }

    @Override
    public void doOnNext(T t) {
        Log.d("haha",t.toString());
        if(t instanceof String) {
            try {
                JSONObject jsonObject = new JSONObject(t.toString());
                int code = jsonObject.optInt("code");
                String msg = jsonObject.optString("msg");
                if (code == 200) {
                    onSuccess(t);
                } else if(code == 20001){
                    onError(msg);
                }else{
                    onError(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(t instanceof BaseBean){
            int code = ((BaseBean) t).getCode();
            String msg = ((BaseBean) t).getMsg();
            if(code ==200 ) {
                onSuccess(t);
            }else{
                onError(msg);
            }
        }else{
            onSuccess(t);
        }
    }

    @Override
    public void doOnCompleted() {
    }

}
