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

    fun calculate(){
        _state.update {
            when(it.mode){
                Mode.Combinatorics -> if(it.combinatoricsState.isValid){
                    it.copy(combinatoricsState = it.combinatoricsState.run {
                        copy(
                            result = when (mode) {
                                CombinatoricsMode.Placement -> if (repetitions) {
                                    Calculator.calculatePlacementsWithReps(n!!, k!!)
                                } else Calculator.calculatePlacementsNoReps(n!!, k!!)

                                CombinatoricsMode.Permutations -> if (repetitions) {
                                    Calculator.calculatePermutationWithReps(repetitionsN)
                                } else Calculator.calculatePermutation(n!!)

                                CombinatoricsMode.Combination -> if (repetitions) {
                                    Calculator.combinationsWithReps(n!!, k!!)
                                } else Calculator.combinations(n!!, k!!)
                            }.intValue()
                        )
                    })
                }else it
                Mode.Urn -> if(it.urnState.isValid){
                    it.copy(urnState = it.urnState.copy(
                        result =  it.urnState.run {
                            when (it.urnState.mode) {
                                UrnMode.ALL -> Calculator.urnAll(n!!, m!!, k!!)
                                UrnMode.PARTIAL -> Calculator.urnPartial(n!!, m!!, k!!, r!!)
                            }.toFloat()
                        }
                    ))
                }else it
            }
        }
    }
}