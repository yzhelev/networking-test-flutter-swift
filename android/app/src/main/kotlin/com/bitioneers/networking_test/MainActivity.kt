package com.bitioneers.networking_test

import android.os.Handler
import android.os.Looper
import androidx.core.os.HandlerCompat
import com.google.gson.Gson
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity: FlutterActivity(),  MethodChannel.MethodCallHandler {

    private val methodChannelName = "com.bitpioneers.networking"
    private lateinit var methodChannel: MethodChannel


    private val executorService: ExecutorService = Executors.newFixedThreadPool(2)
    private val mainThreadHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())
    private lateinit var networkingApi: NetworkingApi


    /// FlutterActivity method
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        methodChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, methodChannelName)
        methodChannel.setMethodCallHandler(this)

        networkingApi = NetworkingApi(
                gson = Gson(),
                executor = executorService,
                handler = mainThreadHandler
        )
    }

    /// FlutterActivity method
    override fun cleanUpFlutterEngine(flutterEngine: FlutterEngine) {
        methodChannel.setMethodCallHandler(null)


        super.cleanUpFlutterEngine(flutterEngine)
    }


    /// MethodChannel MethodCallHandler method
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "request" -> {
                val method: String? = call.argument<String>("method")
                val url: String = call.argument<String>("url")!!
                val params: HashMap<String, Any>? = call.argument<HashMap<String, Any>>("parameters")
                val headers: HashMap<String, String>? = call.argument<HashMap<String, String>>("headers")

                networkingApi.request(
                        method,
                        url,
                        params,
                        headers
                ) { res: Any?, error: String? ->
                    if (res != null) {
                        result.success(res)
                    }

                    if (error != null) {
                        result.success(error)
                    }
                }
            }
            else -> {
                result.notImplemented()
            }
        }
    }
}
