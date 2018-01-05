package com.bulingzhuang.blzhttp.net.stack

import com.bulingzhuang.blzhttp.net.request.Request
import com.bulingzhuang.blzhttp.net.Response

/**
 * ================================================
 * 作    者：bulingzhuang
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2018/1/4
 * 描    述：
 * ================================================
 */
interface HttpStack {
    /**
     * 执行Http请求
     *
     * @param request 待执行的请求
     * @return
     */
    fun performRequest(request: Request<*>): Response
}