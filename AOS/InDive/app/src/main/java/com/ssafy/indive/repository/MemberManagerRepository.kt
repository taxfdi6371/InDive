package com.ssafy.indive.repository

import android.util.Log
import com.ssafy.indive.datasource.MemberManagerDataSource
import com.ssafy.indive.model.dto.MemberJoin
import com.ssafy.indive.model.dto.MemberLogin
import com.ssafy.indive.model.dto.Notice
import com.ssafy.indive.model.response.MemberDetailResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton
import com.ssafy.indive.utils.Result
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import okhttp3.MultipartBody

@Singleton
class MemberManagerRepository @Inject constructor(
    private val memberManagerDataSource: MemberManagerDataSource
) {

    fun login(memberLogin: MemberLogin): Flow<Result<Response<String>>> = flow {
        emit(Result.Loading)
        memberManagerDataSource.login(memberLogin).collect {
            emit(Result.Success(it))
        }
    }.catch { e ->
        emit(Result.Error(e))
    }

    fun join(memberJoin: MemberJoin): Flow<Result<Response<Boolean>>> = flow {
        emit(Result.Loading)
        memberManagerDataSource.join(memberJoin).collect {
            emit(Result.Success(it))
        }
    }.catch { e ->
        emit(Result.Error(e))
    }

    fun emailcheck(email: String): Flow<Result<Response<Boolean>>> = flow {
        emit(Result.Loading)
        memberManagerDataSource.emailcheck(email).collect {
            emit(Result.Success(it))
        }
    }.catch { e ->
        emit(Result.Error(e))
    }

    fun memberDetail(memberSeq: Long): Flow<Result<Response<MemberDetailResponse>>> = flow {
        emit(Result.Loading)
        memberManagerDataSource.memberDetail(memberSeq).collect {
            emit(Result.Success(it))
        }
    }.catch { e ->
        emit(Result.Error(e))
    }

    fun modifyMember(memberSeq: Long): Flow<Result<Response<Boolean>>> = flow {
        emit(Result.Loading)
        memberManagerDataSource.modifyMember(memberSeq).collect {
            emit(Result.Success(it))
        }
    }.catch { e ->
        emit(Result.Error(e))
    }

    fun writeNotice(memberSeq: Long, notice: Notice): Flow<Result<Boolean>> = flow {
        emit(Result.Loading)
        Log.d("TAG", "writeNotice:@@@@@@loading  ${this}")
        memberManagerDataSource.writeNotice(memberSeq, notice).collect {
            Log.d("TAG", "writeNotice:@@@@@@success ")
            emit(Result.Success(it))
        }
    }.catch { e ->
        emit(Result.Error(e))
        Log.d("TAG", "writeNotice:@@@@@@error ")
    }

    fun loginMemberDetail(): Flow<Result<Response<MemberDetailResponse>>> = flow{
        emit(Result.Loading)
        memberManagerDataSource.loginMemberDetail().collect {
            emit(Result.Success(it))
        }
    }.catch { e ->
        emit(Result.Error(e))
    }

}