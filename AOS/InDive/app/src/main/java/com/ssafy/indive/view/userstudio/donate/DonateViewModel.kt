package com.ssafy.indive.view.userstudio.donate

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.indive.api.NFTApi
import com.ssafy.indive.model.dto.NFTAmount
import com.ssafy.indive.repository.MemberManagerRepository
import com.ssafy.indive.repository.NFTRepository
import com.ssafy.indive.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.asStateFlow
import org.web3j.protocol.Web3j
import org.web3j.protocol.admin.Admin

@HiltViewModel
class DonateViewModel @Inject constructor(
    private val nftRepository: NFTRepository,
    private val web3j: Web3j,
    private val admin: Admin,
    private val sharedPreferences: SharedPreferences,
    private val memberManagerRepository: MemberManagerRepository
) : ViewModel() {

    val quantity: MutableStateFlow<String> = MutableStateFlow("0")

    val memo: MutableStateFlow<String> = MutableStateFlow("")

    val balance: MutableStateFlow<Int> = MutableStateFlow(0)

    private val _successMsgEvent: SingleLiveEvent<String> = SingleLiveEvent()
    val successMsgEvent get() = _successMsgEvent

    private val _priceToGetNFT: MutableStateFlow<Int> = MutableStateFlow(0)
    val priceToGetNFT get() = _priceToGetNFT.asStateFlow()

    private val _artistAddress : MutableStateFlow<String> = MutableStateFlow("")
    val artistAddress get() = _artistAddress.asStateFlow()

    private val _artistName : MutableStateFlow<String> = MutableStateFlow("")
    val artistName get() = _artistName.asStateFlow()

    fun putRewardNFT(artistSeq: Long) {
        if(priceToGetNFT.value <= quantity.value.toInt()) {
            val address = sharedPreferences.getString(USER_ADDRESS, "")
            viewModelScope.launch(Dispatchers.IO) {
                nftRepository.putRewardNFT(NFTAmount(quantity.value.toInt(), artistSeq, address!!))
                    .collectLatest {
                        if (it is Result.Success) {
                            if (it.data) {
                                _successMsgEvent.postValue("NFT??? ?????????????????????.")
                            }

                        } else if (it is Result.Error) {
                            Log.d(TAG, "putRewardNFT: $it")
                        }
                    }
            }
        }
    }

    fun donate(){
        val email = sharedPreferences.getString(USER_EMAIL, "")
        val encryptedPrivateKey = sharedPreferences.getString(email, "")
        val decryptePrivateKey = decrypt(encryptedPrivateKey!!)

        admin.unlockAccount(ADMIN_ADDRESS, ADMIN_PASSWORD)
        web3j.setTokenApprove(decryptePrivateKey, INDIVE_ADDRESS, quantity.value.toInt())
        web3j.donate(decryptePrivateKey, artistAddress.value, quantity.value.toInt(), memo.value)

        _successMsgEvent.postValue("????????? ?????????????????????!")
    }

    fun checkIsGetNFT(artistSeq: Long) {

        viewModelScope.launch(Dispatchers.IO) {
            nftRepository.checkIsGetNFT(artistSeq).collectLatest {
                if (it is Result.Success) {
                    _priceToGetNFT.value = it.data.amount
                    Log.d(TAG, "checkIsGetNFT: ${it.data.amount} ")
                }
            }
        }
    }

    fun getMyBalance(){
        val myAddress = sharedPreferences.getString(USER_ADDRESS, "")
        val email = sharedPreferences.getString(USER_EMAIL, "")
        val encryptedPrivateKey = sharedPreferences.getString(email, "")
        val decryptePrivateKey = decrypt(encryptedPrivateKey!!)

        balance.value = web3j.getTokenBalanceOf(decryptePrivateKey, myAddress!!)

    }

    fun memberDetail(artistSeq: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            memberManagerRepository.memberDetail(artistSeq).collectLatest {
                if (it is Result.Success) {
                    _artistAddress.value = it.data.body()!!.wallet
                    _artistName.value = it.data.body()!!.nickname
                }
            }
        }
    }

}