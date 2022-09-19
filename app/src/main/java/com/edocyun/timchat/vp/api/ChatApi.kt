package com.edocyun.timchat.vp.api

import com.edocyun.timchat.network.entity.BaseEntity
import com.edocyun.timchat.network.entity.BaseListEntity
import io.reactivex.Observable
import retrofit2.http.*

/**
 * FileName: com.beijing.zhagen.meiqi.http.api.MainApi.java
 * Author: mouxuefei
 * date: 2018/3/20
 * version: V1.0
 * desc:
 */
interface ChatApi {

    /**
     * 主页接口
     */
    @POST("messages/init")
    fun messages(@Body dto: ChatMessageDTO): Observable<BaseEntity<BaseListEntity<MessageEntity>>>

    @GET("messages/init")
    fun getMessages(@Body dto: ChatMessageDTO): Observable<BaseEntity<BaseListEntity<MessageEntity>>>

    /**
     * 在uri替换location
     */
    @GET("/timezone/africa/{location}")
     fun getWorldTimeGetPath(@Path("location") location: String)

    /**
     * 在url后追加参数
     */
    @GET("/timezone/africa/Abidjan")
    suspend fun getWorldTimeGetQuery(@Query("location") location: String)
}