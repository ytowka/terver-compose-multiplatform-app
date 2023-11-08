package calulator

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalculatorViewModel : ViewModel(){
    private val _state = MutableStateFlow(ScreenState())
    val state: StateFlow<ScreenState> = _state.asStateFlow()


    fun setMode(mode: Mode){
        _state.update {
            it.copy(mode = mode)
        }
    }

    fun setCombinatoricsState(combinatoricsMode: CombinatoricsMode){
        _state.update {
            it.copy(combinatoricsState = it.combinatoricsState.copy(mode = combinatoricsMode))
        }
    }

    fun setUrnState(urnMode: UrnMode){
        _state.update {
            it.copy(urnState = it.urnState.copy(mode = urnMode))
        }
    }
}