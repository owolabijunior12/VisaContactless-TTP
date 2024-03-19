package com.lovisgod.testVisaTTP.handlers.network.networkInterface

import com.lovisgod.testVisaTTP.Constants
import com.lovisgod.testVisaTTP.handlers.network.simplecalladapter.SimpleCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class paybleClient {
    private var retrofit: Retrofit? = null

    fun getClient(): clientInterface {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .connectTimeout(40, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor).build()


        val strategy = AnnotationStrategy()
        val serializer = Persister(strategy)

        retrofit = Retrofit.Builder()
            .baseUrl("https://trans-middleware-31e82172a3e1.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(SimpleCallAdapterFactory.create())
            .client(client)
            .build()
        return retrofit!!.create(clientInterface::class.java)
    }


    fun getPaymentMiddleWareClient(): PaymentClientInterface {
        val headerInterceptor = Interceptor { chain ->
            val builder = chain.request().newBuilder()
            builder.header("sskey", "${Constants.SESSION_KEY}")
            builder.header("api_key", "MnhaRntOiN4/zSSIp0ZuLH1FAM440wy2jk9KDZ/ch+Q=&prd")
            builder.header("merchant_id", "64bfd702-ef2b-42f3-8c47-3a6a0b7139ad")
            return@Interceptor chain.proceed(builder.build())
        }
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .connectTimeout(40, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addInterceptor(headerInterceptor)
            .build()


        val strategy = AnnotationStrategy()
        val serializer = Persister(strategy)

        retrofit = Retrofit.Builder()
            .baseUrl("https://trans-middleware-31e82172a3e1.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(SimpleCallAdapterFactory.create())
            .client(client)
            .build()
        return retrofit!!.create(PaymentClientInterface::class.java)
    }
}