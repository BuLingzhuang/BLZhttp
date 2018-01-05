package com.bulingzhuang.blzhttp.net

import android.util.Log
import com.bulingzhuang.blzhttp.net.cache.LruMemCache
import com.bulingzhuang.blzhttp.net.request.Request
import com.bulingzhuang.blzhttp.net.stack.HttpStack
import java.util.concurrent.PriorityBlockingQueue

/**
 * ================================================
 * 作    者：bulingzhuang
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2018/1/4
 * 描    述：
 * ================================================
 */
class NetworkExecutor(private val mRequestQueue: PriorityBlockingQueue<Request<*>>, private val httpStack: HttpStack) : Thread() {
    //网络请求队列，网络请求栈

    companion object {
        /**
         * 结果分发器，将结果投递到主线程
         */
        private val mResponseDelivery = ResponseDelivery()

        /**
         * 请求缓存
         */
        private val mReqCache = LruMemCache()
    }

    /**
     * 是否停止
     */
    private var isStop = false

    override fun run() {
        while (!isStop) {
            val request = mRequestQueue.take()
            if (request.isCancel) {
                Log.e("blz", "取消执行了")
                continue
            }
            val response = if (isUseCache(request)) {
                //从缓存中取
                mReqCache[request.url]
            } else {
                //从网络上获取数据
                val response = httpStack.performRequest(request)
                //如果该请求需要缓存，那么请求成功则缓存到mResponseCache中
                if (request.mShouldCache && isSuccess(response)) {
                    mReqCache.put(request.url, response)
                }
                response
            }
            //分发请求结果
            mResponseDelivery.deliveryResponse(request, response)
        }
    }

    private fun isSuccess(response: Response?): Boolean {
        return response != null && response.statusCode == 200
    }

    private fun isUseCache(request: Request<*>): Boolean {
        return request.mShouldCache && mReqCache[request.url] != null
    }

    fun quit() {
        isStop = true
        interrupt()
    }
}