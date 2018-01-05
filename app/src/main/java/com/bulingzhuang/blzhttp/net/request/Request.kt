package com.bulingzhuang.blzhttp.net.request

import com.bulingzhuang.blzhttp.net.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.Charset

/**
 * ================================================
 * 作    者：bulingzhuang
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2018/1/3
 * 描    述：
 * ================================================
 */
abstract class Request<BLZ>(val method: HttpMethod, val url: String, private val listener: RequestListener<BLZ>) : Comparable<Request<BLZ>> {
    //请求方法，请求的url，请求的listener

    /**
     * http请求方法枚举
     */
    enum class HttpMethod(val typeName: String) {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");
    }

    /**
     * 优先级枚举
     */
    enum class Priority {
        LOW, NORMAL, HIGH, IMMEDIATE
    }

    companion object {
        /**
         * 默认编码格式
         */
        val DEFAULT_PARAMS_ENCODING = "UTF-8"
        /**
         * Default Content-type
         */
        val HEADER_CONTENT_TYPE = "Content-Type"
    }

    /**
     * 请求序列号
     */
    var mSerialNum = 0

    /**
     * 优先级
     */
    var mPriority = Priority.NORMAL

    /**
     * 是否取消该请求
     */
    var isCancel = false

    /**
     * 是否缓存该请求
     */
    var mShouldCache = true

    /**
     * 请求Header
     */
    val mHeaders = HashMap<String, String>()


    /**
     * 请求参数(body中)
     */
    private val mBodyParams = HashMap<String, String>()

    /**
     * 从原生网络请求中解析结果，子类覆写
     */
    abstract fun parseResponse(response: Response?): BLZ?

    /**
     * 处理Response，该方法运行在UI线程
     */
    fun deliveryResponse(response: Response?) {
        val result = parseResponse(response)
        val stCode = response?.statusCode ?: -1
        val msg = response?.message ?: "unkown error"
        listener.onComplete(stCode, result, msg)
    }

    fun getBodyContentType(): String {
        return "application/x-www-form-urlencoded; charset=$DEFAULT_PARAMS_ENCODING"
    }

    /**
     * 返回POST或PUT的Body参数字节数组
     */
    fun getBody(): ByteArray? {
        return if (mBodyParams.size > 0) {
            encodeParameters(mBodyParams, DEFAULT_PARAMS_ENCODING)
        } else null
    }

    fun isHttps(): Boolean {
        return url.toLowerCase().startsWith("https")
    }

    /**
     * 将参数转换为Url编码的参数串
     */
    private fun encodeParameters(params: HashMap<String, String>, paramsEncoding: String): ByteArray {
        val sb = StringBuilder()
        for (entry in params) {
            sb.append(URLEncoder.encode(entry.key, paramsEncoding))
            sb.append("=")
            sb.append(URLEncoder.encode(entry.value, paramsEncoding))
            sb.append("&")
        }
        return sb.toString().toByteArray(Charset.forName(paramsEncoding))
    }

    /**
     * 对请求进行排序处理，根据由县级和加入到队列的序号进行排序
     */
    override fun compareTo(other: Request<BLZ>): Int {
        val mPriorityO = other.mPriority
        return if (mPriority == mPriorityO) {
            mSerialNum - other.mSerialNum
        } else {
            mPriority.ordinal - mPriorityO.ordinal
        }
    }

    /**
     * 网络请求Listener，在UI线程中执行
     */
    interface RequestListener<in BLZ> {
        /**
         * 请求完成的回调
         *
         * @param result
         */
        fun onComplete(stCode: Int, result: BLZ?, errMsg: String)
    }

    fun sendRequest(url: String): ByteArray {
        val newUrl = URL(url)
        val conn = newUrl.openConnection() as HttpURLConnection
        //设置读取超时为10秒
        conn.readTimeout = 10000
        //设置链接超时为15秒
        conn.connectTimeout = 15000
        //设置请求方式
        conn.requestMethod = method.typeName
        //接收输入流
        conn.doInput = true
        //启动输出流，当需要传递参数时需要开启
        conn.doOutput = true
        //添加Header
        mHeaders.forEach {
            conn.setRequestProperty(it.key, it.value)
        }
        //写入请求参数(body中)
        writeParams(conn.outputStream)

        //发起请求
        conn.connect()
        val inputStream = conn.inputStream
        val result = convertStream2ByteArray(inputStream)
        inputStream.close()
        return result
    }

    private fun writeParams(output: OutputStream) {
        val body = getBody()
        if (body != null) {
            output.write(body)
            output.flush()
        }
    }

    private fun convertStream2ByteArray(inputStream: InputStream): ByteArray {
        val bos = ByteArrayOutputStream()
        val byteArray = ByteArray(4096)
        var count = inputStream.read(byteArray, 0, 4096)
        while (count != -1) {
            bos.write(byteArray, 0, count)
            count = inputStream.read(byteArray, 0, 4096)
        }
        val outArray = bos.toByteArray()
        bos.close()
        return outArray
    }
}
