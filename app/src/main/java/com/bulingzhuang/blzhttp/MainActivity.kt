package com.bulingzhuang.blzhttp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bulingzhuang.blzhttp.net.request.JsonRequest
import com.bulingzhuang.blzhttp.net.request.Request
import com.bulingzhuang.blzhttp.net.RequestQueue
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestQueue = RequestQueue()
        requestQueue.start()
        click.setOnClickListener {
            sendJsonObjectRequest()
        }
    }

    private fun sendJsonObjectRequest() {
        val request = JsonRequest(Request.HttpMethod.GET, "http://39.106.7.250/comic/top/pop/1", object : Request.RequestListener<JSONObject> {
            override fun onComplete(stCode: Int, result: JSONObject?, errMsg: String) {
                Log.e("blz", "stCode=$stCode")
                Log.e("blz", "errMsg=$errMsg")
                if (result != null) {
                    content.text = result.toString()
                }
                code.text = "code=$stCode"
                error.text = errMsg
            }
        })
        requestQueue.addRequest(request)
    }

    override fun onDestroy() {
        super.onDestroy()
        requestQueue.stop()
    }
}
