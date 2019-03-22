package com.zqb.baselibrary.mvp.model

import com.zqb.baselibrary.mvp.contact.IModel
import java.lang.ref.WeakReference

/**
 * Created by zqb on 2019/3/22
 *
 */
abstract class BaseModel<M> :IModel<M>{

//    private var weakReference: WeakReference<M>? = null
//
//    override fun attachModel(model: M) {
//        weakReference = WeakReference(model)
//    }
//
//    override fun detachModel() {
//        if (weakReference != null) {
//            weakReference!!.clear()
//            weakReference = null
//        }
//    }
//
//    fun getModel(): M {
//        return weakReference?.get()!!
//    }
}