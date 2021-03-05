package com.bitioneers.networking_test

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.os.HandlerCompat
import com.google.gson.Gson
import io.flutter.embedding.android.FlutterActivity
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity: FlutterActivity() {

    private val executorService: ExecutorService = Executors.newFixedThreadPool(2)
    private val mainThreadHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())
    lateinit var networkingApi: NetworkingApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkingApi = NetworkingApi(Gson(), executorService, mainThreadHandler)
        makeRequest()
    }

    fun makeRequest()
    {
        networkingApi.request(
                "GET",
                "https://www.google.com/",
                null,
                null
        ) { result: Any?, error: String? ->
            if (result is String) {
                Log.d("Test", result)
            }
        }
    }
}
