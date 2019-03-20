package com.zqb.player.view

import com.zqb.baselibrary.http.base.BaseBean

class NewsBean : BaseBean() {

    /**
     * code : 200
     * message : 成功!
     * result : [{"sid":"29306463","text":"兄弟病了，医院陪护，我看他怎么不大高兴，难道是哪个环节出现了问题","type":"video","thumbnail":"http://wimg.spriteapp.cn/picture/2019/0318/06d7e81e494111e9b0a4842b2b4c75ab_wpd.jpg","video":"http://uvideo.spriteapp.cn/video/2019/0318/06d7e81e494111e9b0a4842b2b4c75ab_wpd.mp4","images":null,"up":"89","down":"4","forward":"0","comment":"2","uid":"20746612","name":"搞笑精选汇","header":"http://wimg.spriteapp.cn/profile/20180709115100136762.png","top_comments_content":null,"top_comments_voiceuri":null,"top_comments_uid":null,"top_comments_name":null,"top_comments_header":null,"passtime":"2019-03-20 02:59:02"},{"sid":"29307905","text":"这新剧炸了，大卫·芬奇出品的《爱·死·机器人》豆瓣高达9.4分！ \r\n画风超炸裂，从美式热血写实风到日式暴力美学，18个独立故事，包括狼人士兵、疯狂机器人、外星蜘蛛、嗜血恶魔等各种猎奇元素。暴力美学 怪诞荒奇 黑色幽默！ \r\n先看一下这段炸裂的CG特效，食肉怪与涡轮猛禽的终极搏杀！","type":"video","thumbnail":"http://wimg.spriteapp.cn/picture/2019/0318/5c8fb77b70cbe_wpd.jpg","video":"http://uvideo.spriteapp.cn/video/2019/0318/5c8fb77b70cbe_wpd.mp4","images":null,"up":"85","down":"5","forward":"4","comment":"10","uid":"8841843","name":"朴朴乐 [     \t\t\t\t苍井玛利亚     \t\t\t]","header":"http://wimg.spriteapp.cn/profile/large/2019/03/16/5c8cbd8600d55_mini.jpg","top_comments_content":null,"top_comments_voiceuri":null,"top_comments_uid":null,"top_comments_name":null,"top_comments_header":null,"passtime":"2019-03-20 02:19:02"}]
     */

    var result: List<ResultBean>? = null

    class ResultBean {
        /**
         * sid : 29306463
         * text : 兄弟病了，医院陪护，我看他怎么不大高兴，难道是哪个环节出现了问题
         * type : video
         * thumbnail : http://wimg.spriteapp.cn/picture/2019/0318/06d7e81e494111e9b0a4842b2b4c75ab_wpd.jpg
         * video : http://uvideo.spriteapp.cn/video/2019/0318/06d7e81e494111e9b0a4842b2b4c75ab_wpd.mp4
         * images : null
         * up : 89
         * down : 4
         * forward : 0
         * comment : 2
         * uid : 20746612
         * name : 搞笑精选汇
         * header : http://wimg.spriteapp.cn/profile/20180709115100136762.png
         * top_comments_content : null
         * top_comments_voiceuri : null
         * top_comments_uid : null
         * top_comments_name : null
         * top_comments_header : null
         * passtime : 2019-03-20 02:59:02
         */

        var sid: String? = null
        var text: String? = null
        var type: String? = null
        var thumbnail: String? = null
        var video: String? = null
        var images: Any? = null
        var up: String? = null
        var down: String? = null
        var forward: String? = null
        var comment: String? = null
        var uid: String? = null
        var name: String? = null
        var header: String? = null
        var top_comments_content: Any? = null
        var top_comments_voiceuri: Any? = null
        var top_comments_uid: Any? = null
        var top_comments_name: Any? = null
        var top_comments_header: Any? = null
        var passtime: String? = null
    }
}
