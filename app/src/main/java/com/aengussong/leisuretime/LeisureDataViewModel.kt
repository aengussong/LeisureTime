package com.aengussong.leisuretime

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aengussong.leisuretime.data.LeisureRepository
import com.aengussong.leisuretime.data.local.entity.LeisureEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject

class LeisureDataViewModel(private val repo:LeisureRepository) : ViewModel(), KoinComponent {

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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //empty implementation
                }, { t: Throwable ->
                    _errorLiveData.value = t.localizedMessage
                })
        )
    }

    fun getLeisure(name: String) {
        disposables.add(
            repo.getLeisure(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({ entity ->
                    _leisureLiveData.value = entity
                }, {
                    _errorLiveData.value = it.localizedMessage
                })
        )
    }
}