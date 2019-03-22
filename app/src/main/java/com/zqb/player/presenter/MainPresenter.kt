package com.zqb.player.presenter

import com.zqb.baselibrary.mvp.contact.IModel
import com.zqb.baselibrary.mvp.presenter.BasePresenter
import com.zqb.player.model.MainModel

/**
 * Created by zqb on 2019/3/22
 *
 */
class MainPresenter<T> :BasePresenter<T,MainModel>() {
    override fun createModel(): MainModel {
        return MainModel()
    }

}