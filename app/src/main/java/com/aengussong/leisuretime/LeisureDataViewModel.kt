package com.aengussong.leisuretime

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import com.aengussong.leisuretime.util.observeTransfer
import io.reactivex.disposables.CompositeDisposable
import org.koin.core.KoinComponent

class LeisureDataViewModel(private val repo: LeisureRepository) : ViewModel(), KoinComponent {

    val leisureLiveData: LiveData<LeisureEntity>
        get() = _leisureLiveData
    val errorLiveData: LiveData<String>
        get() = _errorLiveData

    private val disposables = CompositeDisposable()

    private val _leisureLiveData = MutableLiveData<LeisureEntity>()
    private val _errorLiveData = MutableLiveData<String>()

    override fun onCleared() {
        disposables.dispose()
    }

    fun addLeisure(leisure: LeisureEntity) {
        disposables.add(
            repo.addLeisure(leisure)
                .observeTransfer()
                .subscribe({
                    //empty implementation
                }, { t: Throwable ->
                    _errorLiveData.value = t.localizedMessage
                })
        )
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