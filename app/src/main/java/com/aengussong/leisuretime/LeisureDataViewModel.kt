package com.aengussong.leisuretime

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import com.aengussong.leisuretime.util.observeTransfer
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent

class LeisureDataViewModel(private val repo: LeisureRepository) : ViewModel(), KoinComponent {

    val leisureLiveData: LiveData<LeisureEntity>
        get() = _leisureLiveData
    val errorLiveData: LiveData<String>
        get() = _errorLiveData

    private val disposables = CompositeDisposable()

    private val _leisureLiveData = MutableLiveData<LeisureEntity>()
    private val _errorLiveData = MutableLiveData<String>()

    private val errorHelper = CoroutineExceptionHandler { _, throwable -> _errorLiveData.value = throwable.localizedMessage }

    override fun onCleared() {
        disposables.dispose()
    }

    fun addLeisure(leisure: LeisureEntity) {
        viewModelScope.launch(errorHelper) {
            repo.addLeisure(leisure)
        }
    }

    fun getLeisure(id: Long) {
        disposables.add(
            repo.getLeisure(id)
                .observeTransfer()
                .subscribe({ entity ->
                    _leisureLiveData.value = entity
                }, {
                    _errorLiveData.value = it.localizedMessage
                })
        )
    }

    fun updateLeisure(id: Long, newName: String) {
        disposables.add(
            repo.updateLeisure(id, newName)
                .observeTransfer()
                .subscribe({
                    //empty implementation
                }, {
                    _errorLiveData.value = it.localizedMessage
                })
        )
    }

    fun deleteLeisure(id: Long) {
        disposables.add(
            repo.deleteLeisure(id)
                .observeTransfer()
                .subscribe({
                    //empty implementation
                }, {
                    _errorLiveData.value = it.localizedMessage
                })
        )
    }
}