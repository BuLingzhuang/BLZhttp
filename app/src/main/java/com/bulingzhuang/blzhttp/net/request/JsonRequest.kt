package com.bulingzhuang.blzhttp.net.request

import com.bulingzhuang.blzhttp.net.Response
import org.json.JSONObject

/**
 * ================================================
 * 作    者：bulingzhuang
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2018/1/4
 * 描    述：Json类型的请求
 * ================================================
 */
class JsonRequest(method: HttpMethod, url: String, listener: RequestListener<JSONObject>) : Request<JSONObject>(method, url, listener) {

    override fun parseResponse(response: Response?): JSONObject? {
        return if (response != null) {
            JSONObject(String(response.rawData))
        } else {
            null
        }
    }

}