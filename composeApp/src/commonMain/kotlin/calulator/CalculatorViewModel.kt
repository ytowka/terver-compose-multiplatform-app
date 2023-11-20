package calulator

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    fun setCombNField(value: Int?){
        _state.update {
            it.copy(
                combinatoricsState = it.combinatoricsState.copy(
                    n = value
                )
            )
        }
    }

    fun setRepetitions(value: Boolean){
        _state.update {
            it.copy(
                combinatoricsState = it.combinatoricsState.copy(repetitions = value)
            )
        }
    }

    fun setCombKField(value: Int?){
        _state.update {
            it.copy(
                combinatoricsState = it.combinatoricsState.copy(
                    k = value
                )
            )
        }
    }

    fun setCombMultiNField(value: String){
        _state.update {
            it.copy(
                combinatoricsState = it.combinatoricsState.copy(repetitionsNField = value)
            )
        }
    }

    fun setUrnNField(value: Int?){
        _state.update {
            it.copy(
                urnState = it.urnState.copy(n = value)
            )
        }
    }
    fun setUrnMField(value: Int?){
        _state.update {
            it.copy(
                urnState = it.urnState.copy(m = value)
            )
        }
    }
    fun setUrnKField(value: Int?){
        _state.update {
            it.copy(
                urnState = it.urnState.copy(k = value)
            )
        }
    }
    fun setUrnRField(value: Int?){
        _state.update {
            it.copy(
                urnState = it.urnState.copy(r = value)
            )
        }
    }

    private fun setUrnResult(result: String){
        _state.update {
            it.copy(urnState = it.urnState.copy(
                result = result,
                loading = false,
            ))
        }
    }

    fun calculate(){
        _state.value.let {
            when(it.mode){
                Mode.Combinatorics -> if(it.combinatoricsState.isValid) calculateCombinatorics()
                Mode.Urn -> if(it.urnState.isValid) calculateUrnState()
            }
        }
    }

    private fun setCombinatoricsResult(result: String){
        _state.update {
            it.copy(combinatoricsState = it.combinatoricsState.copy(
                result = result,
                loading = false,
            ))
        }
    }

    private var combinatoricsCalculationJob: Job? = null
    private fun calculateCombinatorics(){
        combinatoricsCalculationJob?.cancel()
        combinatoricsCalculationJob = viewModelScope.launch {
            val currentState = state.value
            val delayJob = launch {
                delay(300)
                _state.updateAndGet {
                    it.copy(combinatoricsState = it.combinatoricsState.copy(loading = true))
                }
            }
            val result = currentState.combinatoricsState.run {
                withContext(Dispatchers.Default){
                    when (mode) {
                        CombinatoricsMode.Placement -> if (repetitions) {
                            Calculator.calculatePlacementsWithReps(n!!, k!!)
                        } else Calculator.calculatePlacementsNoReps(n!!, k!!)

                        CombinatoricsMode.Permutations -> if (repetitions) {
                            Calculator.calculatePermutationWithReps(repetitionsN)
                        } else Calculator.calculatePermutation(n!!)

                        CombinatoricsMode.Combination -> if (repetitions) {
                            Calculator.combinationsWithReps(n!!, k!!)
                        } else Calculator.combinations(n!!, k!!)
                    }.toString()
                }
            }
            delayJob.cancel()
            setCombinatoricsResult(result)
        }
    }

    private var urnCalculationJob: Job? = null
    private fun calculateUrnState(){
        urnCalculationJob?.cancel()
        urnCalculationJob = viewModelScope.launch {
            val currentState = state.value
            val delayJob = launch{
                delay(300)
                _state.updateAndGet {
                    it.copy(urnState = it.urnState.copy(loading = true))
                }
            }
            val result = currentState.urnState.run {
                withContext(Dispatchers.Default){
                    when(mode){
                        UrnMode.ALL -> Calculator.urnAll(n!!, m!!, k!!)
                        UrnMode.PARTIAL -> Calculator.urnPartial(n!!, m!!, k!!, r!!)
                    }.toString()
                }
            }
            delayJob.cancel()
            setUrnResult(result)
        }
    }
}