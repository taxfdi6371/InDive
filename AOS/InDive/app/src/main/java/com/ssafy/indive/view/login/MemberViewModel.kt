package com.ssafy.indive.view.login

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.indive.model.dto.MemberJoin
import com.ssafy.indive.model.dto.MemberLogin
import com.ssafy.indive.model.dto.Notice
import com.ssafy.indive.model.response.MemberDetailResponse
import com.ssafy.indive.repository.MemberManagerRepository
import com.ssafy.indive.utils.JWT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Response
import javax.inject.Inject
import com.ssafy.indive.utils.Result
import com.ssafy.indive.utils.SingleLiveEvent
import com.ssafy.indive.utils.USER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


private const val TAG = "MemberViewModel"

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val memberManagerRepository: MemberManagerRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _login: MutableStateFlow<Result<Response<String>>> =
        MutableStateFlow(Result.Unintialized)
    val login get() = _login.asStateFlow()

    private val _loginSuccess = SingleLiveEvent<String>()
    val loginSuccess get() = _loginSuccess

    private val _loginFail = SingleLiveEvent<String>()
    val loginFail get() = _loginFail

    private val _join: MutableStateFlow<Result<Response<Boolean>>> =
        MutableStateFlow(Result.Unintialized)
    val join get() = _join.asStateFlow()

    private val _emailCheck = SingleLiveEvent<Boolean>()
    val emailCheck get() = _emailCheck

    private val _profile = SingleLiveEvent<MemberDetailResponse>()
    val profile get() = _profile

    private val _noticeSuccess = SingleLiveEvent<Boolean>()
    val noticeSuccess get() = _noticeSuccess

    fun memberLogin(memberLogin: MemberLogin) {
        viewModelScope.launch(Dispatchers.IO) {
            memberManagerRepository.login(memberLogin).collectLatest {
                _login.value = it
                Log.d(TAG, "memberLogin: $it")
                if (it is Result.Success) {
                    val res = it.data.body()
                    if (res == "true") {
                        _loginSuccess.postValue("로그인 성공")
                        sharedPreferences.edit()
                            .putString(JWT, it.data.headers()["Authorization"])
                            .apply()
                    } else{
                        _loginFail.postValue("로그인 실패")
                    }
                    Log.d(TAG, "memberLogin: ${it.data.body()}")
                    //Log.d(TAG, "memberLogin: ${it.data.headers()["Authorization"]!!.split(" ")[1]}")
                }
            }
        }
    }

    fun memberJoin(memberJoin: MemberJoin) {
        viewModelScope.launch(Dispatchers.IO) {
            memberManagerRepository.join(memberJoin).collectLatest {
                _join.value = it
                Log.d(TAG, "memberJoin: $it")
                if (it is Result.Success) {
                    Log.d(TAG, "memberJoin: ${it.data.body()}")
                }
            }
        }
    }

    fun memberEmailCheck(email: String){
        viewModelScope.launch(Dispatchers.IO) {
            memberManagerRepository.emailcheck(email).collectLatest {
                if(it is Result.Success){
                    _emailCheck.postValue(it.data.body())
                    Log.d(TAG, "memberEmailCheck: ${it.data.body()}")
                }
            }
        }
    }

    fun memberDetail(memberSeq: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            memberManagerRepository.memberDetail(memberSeq).collectLatest {
                if(it is Result.Success) {
                    Log.d(TAG, "memberDetail: ${it.data.body()}")
                    _profile.postValue(it.data.body())
                    noticeSuccess.postValue(true)
                }
            }
        }
    }

    fun memberModify(){}
    
    fun writeNotice(memberSeq: Long, notice: Notice) {
        viewModelScope.launch(Dispatchers.IO) { 
            memberManagerRepository.writeNotice(memberSeq, notice).collectLatest {
                Log.d(TAG, "writeNotice: ${it}")
                if(it is Result.Success) {
                    Log.d(TAG, "writeNotice: @@")
                    //noticeSuccess.postValue(it.data.body())
                }
            }
        }
    }
    
    fun loginMemberDetail() {
        viewModelScope.launch(Dispatchers.IO) { 
            memberManagerRepository.loginMemberDetail().collectLatest {
                if(it is Result.Success){
                    Log.d(TAG, "loginMemberDetail: ${it.data.body()}")
                    val res = it.data.body() as MemberDetailResponse
                    sharedPreferences.edit()
                        .putLong(USER, res.memberSeq)
                        .apply()
                }
            }
        }
    }

}