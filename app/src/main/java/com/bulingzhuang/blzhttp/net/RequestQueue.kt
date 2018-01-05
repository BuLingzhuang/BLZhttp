package com.bulingzhuang.blzhttp.net

import com.bulingzhuang.blzhttp.net.request.Request
import com.bulingzhuang.blzhttp.net.stack.HttpUrlConnStack
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * ================================================
 * 作    者：bulingzhuang
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2018/1/4
 * 描    述：
 * ================================================
 */
class RequestQueue {

    companion object {
        /**
         * 默认核心数(根据设备核心数)
         */
        val DEFAULT_CORE_NUM = Runtime.getRuntime().availableProcessors() + 1
    }

    /**
     * 请求队列
     */
    private val mRequestQueue = PriorityBlockingQueue<Request<*>>()

    /**
     * 请求的序列化生成器
     */
    private val mSerialNumGenerator = AtomicInteger(0)

    /**
     * 分发线程数
     */
    private val mDispatcherNum = DEFAULT_CORE_NUM

    /**
     * 执行网络请求的线程
     */
    private val mDispatchers = ArrayList<NetworkExecutor>()

    /**
     * Http请求的真正执行者
     */
    private val httpStack = HttpUrlConnStack()

    private fun startNetworkExecutors() {
        mDispatchers.clear()
        for (position in 0 until mDispatcherNum) {
            val executor = NetworkExecutor(mRequestQueue, httpStack)
            executor.start()
            mDispatchers.add(executor)
        }
    }

    fun start() {
        stop()
        startNetworkExecutors()
    }

    fun stop() {
        if (mDispatchers.size > 0) {
            mDispatchers.forEach {
                it.quit()
            }
        }
    }

    fun addRequest(request: Request<*>) {
        if (!mRequestQueue.contains(request)) {
            request.mSerialNum = getSerialNumber()
            mRequestQueue.add(request)
        }
    }

    fun clear(){
        mRequestQueue.clear()
    }

    private fun getSerialNumber(): Int {
        return mSerialNumGenerator.incrementAndGet()
    }
}