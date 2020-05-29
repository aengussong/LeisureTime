package com.aengussong.leisuretime

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aengussong.leisuretime.model.Leisure
import com.aengussong.leisuretime.usecase.AddLeisureUseCase
import com.aengussong.leisuretime.usecase.GetLeisureUseCase
import com.aengussong.leisuretime.usecase.IncrementLeisureUseCase
import com.aengussong.leisuretime.usecase.RenameLeisureUseCase
import com.aengussong.leisuretime.util.Tree
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LeisureDataViewModel(
    private val addLeisureUseCase: AddLeisureUseCase,
    getLeisureUseCase: GetLeisureUseCase,
    private val incrementLeisureUseCase: IncrementLeisureUseCase,
    private val renameLeisureUseCase: RenameLeisureUseCase
) : ViewModel() {

    val leisureLiveData: LiveData<List<Tree<Leisure>>>
        get() = _leisureLiveData
    val errorLiveData: LiveData<String>
        get() = _errorLiveData

    private val _errorLiveData = MutableLiveData<String>()
    private val _leisureLiveData = getLeisureUseCase.execute()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        _errorLiveData.value = throwable.localizedMessage
    }

    fun addLeisure(name: String, parentId: Long? = null): Job {
        return launchWithHandler {
            addLeisureUseCase.execute(name, parentId)
        }
    }

    fun incrementCounter(leisureId: Long): Job {
        return launchWithHandler {
            incrementLeisureUseCase.execute(leisureId)
        }
    }

    fun renameLeisure(leisureId: Long, newName: String): Job {
        return launchWithHandler {
            renameLeisureUseCase.execute(leisureId, newName)
        }
    }

    private fun launchWithHandler(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(errorHandler, block = block)
}