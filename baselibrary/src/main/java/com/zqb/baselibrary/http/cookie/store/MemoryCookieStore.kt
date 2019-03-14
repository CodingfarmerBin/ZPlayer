/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zqb.baselibrary.http.cookie.store

import okhttp3.Cookie
import okhttp3.HttpUrl

import java.util.*
import kotlin.collections.ArrayList

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/14
 * 描    述：Cookie 的内存管理
 * 修订历史：
 * ================================================
 */
class MemoryCookieStore : CookieStore {

    private val memoryCookies = HashMap<String, ArrayList<Cookie>>()

    override val allCookie: ArrayList<Cookie>
        @Synchronized get() {
            val cookies = ArrayList<Cookie>()
            val httpUrls = memoryCookies.keys
            for (url in httpUrls) {
                cookies.addAll(memoryCookies[url]!!)
            }
            return cookies
        }

    @Synchronized
    override fun saveCookie(url: HttpUrl, cookies: ArrayList<Cookie>) {
        val oldCookies = memoryCookies[url.host()]!!
        val needRemove = ArrayList<Cookie>()
        for (newCookie in cookies) {
            for (oldCookie in oldCookies) {
                if (newCookie.name() == oldCookie.name()) {
                    needRemove.add(oldCookie)
                }
            }
        }
        oldCookies.removeAll(needRemove)
        oldCookies.addAll(cookies)
    }

    @Synchronized
    override fun saveCookie(url: HttpUrl, cookie: Cookie) {
        val cookies = memoryCookies[url.host()]
        val needRemove = ArrayList<Cookie>()
        for (item in cookies!!) {
            if (cookie.name() == item.name()) {
                needRemove.add(item)
            }
        }
        cookies.removeAll(needRemove)
        cookies.add(cookie)
    }

    @Synchronized
    override fun loadCookie(url: HttpUrl): ArrayList<Cookie> {
        var cookies = memoryCookies[url.host()]
        if (cookies == null) {
            cookies = ArrayList()
            memoryCookies[url.host()] = cookies
        }
        return cookies
    }

    override fun getCookie(url: HttpUrl): ArrayList<Cookie> {
        val cookies = ArrayList<Cookie>()
        val urlCookies = memoryCookies[url.host()]
        if (urlCookies != null) cookies.addAll(urlCookies)
        return cookies
    }

    @Synchronized
    override fun removeCookie(url: HttpUrl, cookie: Cookie): Boolean {
        val cookies = memoryCookies[url.host()]
        return cookies!!.remove(cookie)
    }

    @Synchronized
    override fun removeCookie(url: HttpUrl): Boolean {
        return memoryCookies.remove(url.host()) != null
    }

    @Synchronized
    override fun removeAllCookie(): Boolean {
        memoryCookies.clear()
        return true
    }
}
