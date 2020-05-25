package com.aengussong.leisuretime

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aengussong.leisuretime.model.Leisure
import com.aengussong.leisuretime.usecase.AddLeisureUseCase
import com.aengussong.leisuretime.usecase.GetLeisureUseCase
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class LeisureDataViewModel(
    private val addLeisureUseCase: AddLeisureUseCase,
    private val getLeisureUseCase: GetLeisureUseCase
) : ViewModel() {

    val leisureLiveData: LiveData<List<Leisure>>
        get() = getLeisureUseCase.execute()
    val errorLiveData: LiveData<String>
        get() = _errorLiveData

    private val disposables = CompositeDisposable()

    private val _errorLiveData = MutableLiveData<String>()

    private val errorHelper = CoroutineExceptionHandler { _, throwable ->
        _errorLiveData.value = throwable.localizedMessage
    }

    override fun onCleared() {
        disposables.dispose()
    }

    fun addLeisure(name: String, parentId: Long? = null) {
        viewModelScope.launch(errorHelper) {
            addLeisureUseCase.execute(name, parentId)
        }
    }
}