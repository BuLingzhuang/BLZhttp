package com.bulingzhuang.blzhttp.net

import android.os.Handler
import android.os.Looper
import com.bulingzhuang.blzhttp.net.request.Request
import java.util.concurrent.Executor

/**
 * ================================================
 * 作    者：bulingzhuang
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2018/1/4
 * 描    述：
 * ================================================
 */
class ResponseDelivery : Executor {

    /**
     * 主线程Handler
     */
    private val mResponseHandler = Handler(Looper.getMainLooper())

    fun deliveryResponse(request: Request<*>, response: Response?) {
        val runnable = Runnable {
            request.deliveryResponse(response)
        }

        execute(runnable)
    }

    override fun execute(command: Runnable?) {
        mResponseHandler.post(command)
    }
}