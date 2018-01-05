package com.bulingzhuang.blzhttp.net.stack

import com.bulingzhuang.blzhttp.net.request.Request
import com.bulingzhuang.blzhttp.net.Response
import com.bulingzhuang.blzhttp.net.config.HttpUrlConnConfig
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * ================================================
 * 作    者：bulingzhuang
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2018/1/4
 * 描    述：
 * ================================================
 */
class HttpUrlConnStack : HttpStack {

    /**
     * 配置Https
     */
    private val mConfig = HttpUrlConnConfig.sConfig

    override fun performRequest(request: Request<*>): Response {
        //构建HttpURLConnection
        val conn = createUrlConnection(request.url)
        //设置headers
        setRequestHeaders(conn, request)
        //设置Body参数
        setRequestParams(conn, request)
        //https配置
        configHttps(request)

        return fetchResponse(conn)
    }

    private fun createUrlConnection(url: String): HttpURLConnection {
        val newUrl = URL(url)
        val conn = newUrl.openConnection() as HttpURLConnection
        //设置读取超时为10秒
        conn.readTimeout = mConfig.soTimeOut
        //设置链接超时为15秒
        conn.connectTimeout = mConfig.connTimeOut
        //接收输入流
        conn.doInput = true
        conn.useCaches = false
        return conn
    }

    private fun setRequestHeaders(conn: HttpURLConnection, request: Request<*>) {
        request.mHeaders.forEach { conn.addRequestProperty(it.key, it.value) }
    }

    private fun setRequestParams(conn: HttpURLConnection, request: Request<*>) {
        val httpMethod = request.method
        conn.requestMethod = httpMethod.typeName
        val body = request.getBody()
        if (body != null) {
            conn.doOutput = true
            conn.addRequestProperty(Request.HEADER_CONTENT_TYPE, request.getBodyContentType())
            val outputStream = DataOutputStream(conn.outputStream)
            outputStream.write(body)
            outputStream.close()
        }
    }

    private fun configHttps(request: Request<*>) {
        if (request.isHttps()) {
            val sslSocketFactory = mConfig.mSslSocketFactory
            val hostnameVerifier = mConfig.mHostnameVerifier
            if (sslSocketFactory != null && hostnameVerifier != null) {
                HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory)
                HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier)
            }
        }
    }

    private fun fetchResponse(conn: HttpURLConnection): Response {
        //发起请求
        conn.connect()
        val responseCode = conn.responseCode
        val message = conn.responseMessage
        if (responseCode == -1) {
            throw IOException("Could not retrieve response code from HttpUrlConnection.")
        }
        val inputStream = conn.inputStream
        val result = convertStream2ByteArray(inputStream)
        inputStream.close()
        return Response(responseCode, message, result)
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