package com.bulingzhuang.blzhttp.net.config

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory

/**
 * ================================================
 * 作    者：bulingzhuang
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2018/1/4
 * 描    述：
 * ================================================
 */
class HttpUrlConnConfig : HttpConfig() {
    companion object {
        val sConfig = HttpUrlConnConfig()
    }

    var mSslSocketFactory: SSLSocketFactory? = null
    var mHostnameVerifier: HostnameVerifier? = null

    fun setHttpsConfig(sslSocketFactory: SSLSocketFactory, hostnameVerifier: HostnameVerifier) {
        mSslSocketFactory = sslSocketFactory
        mHostnameVerifier = hostnameVerifier
    }
}