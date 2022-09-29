package com.ssafy.indive.api

import com.ssafy.indive.model.dto.MemberJoin
import com.ssafy.indive.model.dto.MemberLogin
import com.ssafy.indive.model.dto.Notice
import com.ssafy.indive.model.response.MemberDetailResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface MemberManagerApi {
    // 로그인
    @POST("members/login")
    suspend fun login(@Body memberLogin: MemberLogin): Response<String>

    // 회원가입
    @POST("members/join")
    suspend fun join(@Body memberJoin: MemberJoin): Response<Boolean>

    // 멤버 세부 정보
    @GET("members/{memberSeq}")
    suspend fun memberDetail(@Path("memberSeq") memberSeq: Long): Response<MemberDetailResponse>

    // 이메일 중복확인
    @GET("members/duplicated-email")
    suspend fun emailCheck(@Query("email") email: String): Response<Boolean>

    // 멤버 정보 수정
    @PUT("members/{memberSeq}")
    suspend fun memberModify(@Path("memberSeq") memberSeq: Long) : Response<Boolean>

    // 공지사항 작성
    @POST("members/{memberSeq}/notice")
    suspend fun writeNotice(@Path("memberSeq") memberSeq: Long, @Body notice: Notice): Boolean

    // 로그인 멤버 정보
    @GET("members/my-account")
    suspend fun loginMemberDetail(): Response<MemberDetailResponse>




//    //13 Test
//    @Multipart
//    @PUT("members/{memberSeq}")
//    suspend fun modifyMember(
//        @Path("memberSeq") memberSeq: Long,
//        @Query("nickname") nickname: String,
//        @Part profileFile: MultipartBody.Part?,
//        @Part backgroundFile: MultipartBody.Part?,
//        @Query("profileMessage") profileMessage: String?
//    ) : Boolean

}