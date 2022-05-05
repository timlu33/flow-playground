package tim.flowpractice

import androidx.compose.animation.defaultDecayAnimationSpec
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    val countDownFlow = flow<Int> {
        val startValue = 5
        var currentValue = startValue
        emit(startValue)
        while (currentValue > 0) {
            delay(1000)
            currentValue--
            emit(currentValue)
        }
    }

    private val _liveData = MutableLiveData<Int>()
    val liveData: LiveData<Int> = _liveData

    val singleLiveEvent = SingleLiveEvent<Int>()

    //TODO 補一個live data case

    // 不會emit same value
    private val _stateFlow = MutableStateFlow<Int>(0)
    val stateFlow = _stateFlow.asStateFlow()

    private val _sharedFlow = MutableSharedFlow<Int>()
    val sharedFlow = _sharedFlow.asSharedFlow().shareIn(viewModelScope, replay = 1, started = SharingStarted.WhileSubscribed())

    init {
        collectFlow()
    }


    @OptIn(FlowPreview::class)
    private fun collectFlow() {
//        val flow = flow {
//            delay(250L)
//            emit("前菜")
//            delay(1000L)
//            emit("主菜")
//            delay(100L)
//            emit("甜點")
//        }
//        viewModelScope.launch {
//            flow.onEach {
//                println("Flow: $it 到了")
//            }.collectLatest {
//                println("Flow: 正在吃 $it")
//                delay(1500L)
//                println("Flow: 吃完 $it")
//            }
//        }
        val p = Person(1)
        val flow2 = flow {
            for (i in 1..10) {
                delay(1000)
                emit(i)
            }
        }

        viewModelScope.launch {
            flow2.collect {
                println("emit $it")
                _liveData.postValue(it)
                singleLiveEvent.value = it
                _stateFlow.value = it
                _sharedFlow.emit(it)
            }
        }


    }

}

data class Person(val name: Int)