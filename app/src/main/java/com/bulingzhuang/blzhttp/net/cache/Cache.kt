package com.bulingzhuang.blzhttp.net.cache

import android.icu.lang.UCharacter.GraphemeClusterBreak.V


/**
 * ================================================
 * 作    者：bulingzhuang
 * 邮    箱：bulingzhuang@foxmail.com
 * 创建日期：2018/1/4
 * 描    述：
 * ================================================
 */
interface Cache<in K, V> {
    operator fun get(key: K): V?

    fun put(key: K, value: V)

    fun remove(key: K)
}