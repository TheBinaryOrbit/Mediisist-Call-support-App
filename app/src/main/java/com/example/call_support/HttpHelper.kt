package com.example.call_support



import com.example.call_support.LoginRequest
import com.example.call_support.LoginResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

object HttpHelper {
    suspend fun login(request: LoginRequest): LoginResponse? = withContext(Dispatchers.IO) {
        val url = URL("https://2q766kvz-8000.inc1.devtunnels.ms/api/v1/customersupport/customersupportlogin")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonBody = Gson().toJson(request)

            val outputStream: OutputStream = connection.outputStream
            outputStream.write(jsonBody.toByteArray())
            outputStream.flush()
            outputStream.close()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(connection.inputStream.reader())
                val responseText = reader.readText()
                reader.close()
                return@withContext Gson().fromJson(responseText, LoginResponse::class.java)
            } else {
                return@withContext null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        } finally {
            connection.disconnect()
        }
    }
}
