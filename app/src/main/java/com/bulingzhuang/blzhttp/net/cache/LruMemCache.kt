package com.bulingzhuang.blzhttp.net.cache

import android.support.v4.util.LruCache
import com.bulingzhuang.blzhttp.net.Response

/**
 * ================================================
 * 作    者：bulingzhuang
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2018/1/4
 * 描    述：
 * ================================================
 */
class LruMemCache : Cache<String, Response> {
    private val lruCache: LruCache<String, Response>

    init {
        //计算可使用的最大内存
        val maxMemory = Runtime.getRuntime().maxMemory() / 1024
        val cacheSize = maxMemory / 8
        lruCache = object : LruCache<String, Response>(cacheSize.toInt()) {
            override fun sizeOf(key: String?, value: Response?): Int {
                return if (value != null) {
                    value.rawData.size / 1024
                } else {
                    super.sizeOf(key, value)
                }
            }
        }
    }

    override fun get(key: String): Response? {
        return lruCache.get(key)
    }

    override fun put(key: String, value: Response) {
        lruCache.put(key, value)
    }

    override fun remove(key: String) {
        lruCache.remove(key)
    }
}