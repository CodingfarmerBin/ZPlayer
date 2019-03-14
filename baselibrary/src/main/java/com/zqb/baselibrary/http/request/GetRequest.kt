package com.zqb.baselibrary.http.request

class GetRequest :IRequest() {

    override fun setUrl(url: String): IRequest {
        super.url=url
        return this
    }

}