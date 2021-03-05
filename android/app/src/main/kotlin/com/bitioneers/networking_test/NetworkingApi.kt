package com.bitioneers.networking_test
import android.os.Handler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executor
import kotlin.collections.HashMap
class NetworkingApi(
        private val gson: Gson,
        private val executor: Executor,
        private val handler: Handler
) {
    /**
     * Make an asynchronous request towards a given url
     * @param method Request method
     * @param urlString endpoint
     * @param parameters optional parameters to add to the request
     * @param headers optional headers to add to the request
     * @param completion a completion callback
     */
    fun request(
            method: String?,
            urlString: String,
            parameters: HashMap<String, Any>?,
            headers: HashMap<String, String>?,
            completion: (Any?, String?) -> Unit
    ) {
        executor.execute {
            try {
                val result = syncRequest(method, urlString, parameters, headers)
                if (result is String) {
                    handler.post { completion(null, result) }
                } else {
                    handler.post { completion(result, null) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                handler.post { completion(null, e.message) }
            }
        }
    }

    /**
     * Make a synchronous request towards the given url
     * @param method Request method 'GET' by default
     * @param urlString url
     * @param parameters optional parameters to add to the request
     * @param headers optional headers to add to the request
     * @return returns a Any result
     */
    private fun syncRequest(
            method: String? = "GET",
            urlString: String,
            parameters: HashMap<String, Any>?,
            headers: HashMap<String, String>?
    ): Any? {
        val url = URL(urlString)
        var result: String? = null
        var resultMap: HashMap<Any, Any>? = null
        //Create connection request
        (url.openConnection() as? HttpURLConnection)?.run {
            //setup request method type
            requestMethod = method
            doOutput = true
            //setup headers if any
            headers?.forEach { addRequestProperty(it.key, it.value) }
            //parse parameters if any
            val jsonData = parameters?.let { gson.toJson(it) }
            //write data
            jsonData?.let { outputStream.write(it.toByteArray()) }
            //read response
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = reader.readLine()
            result = ""
            while (line != null) {
                result += line;
                line = reader.readLine();
            }
            if (result != null) {
                try {
                    val type: Type = object : TypeToken<HashMap<Any?, Any?>?>() {}.type
                    resultMap = gson.fromJson<HashMap<Any, Any>>(result, type)
                } catch (e: Exception) {
                    return e.message
                }
            }
            inputStream.close()
        }
        return resultMap
    }
}