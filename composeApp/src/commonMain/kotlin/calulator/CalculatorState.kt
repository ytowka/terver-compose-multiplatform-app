package calulator

import androidx.compose.runtime.Immutable

@Immutable
data class ScreenState(
    val mode: Mode = Mode.Combinatorics,
    val urnState: UrnState = UrnState(
        n = null,
        k = null,
        m = null,
        r = null,
        mode = UrnMode.ALL
    ),
    val combinatoricsState: CombinatoricsState = CombinatoricsState(
        repetitions = false,
        n = null,
        k = null,
        mode = CombinatoricsMode.Placement
    ),
)

@Immutable
data class UrnState(
    val n: Int?,
    val m: Int?,
    val k: Int?,
    val r: Int?,
    val mode: UrnMode,
    val result: Float? = null,
) : Validateable{
    val totalLessThanGrabbedError = if(n != null && k != null){
        n < k
    }else false

    val totalLessThanMarkedError = if(n != null && m != null){
        n < m
    } else false

    val grabbedLessThanMarkedGrabbedError = if(k != null && r != null){
        k < r
    } else false

    val markedLessThanMarkedGrabbedError = if(m != null && r != null){
        m < r
    } else false

    override val isValid = when(mode){
        UrnMode.PARTIAL -> listOf(n,m,k,r).all { it != null && it > 0 } &&
                !totalLessThanGrabbedError &&
                !totalLessThanMarkedError &&
                !grabbedLessThanMarkedGrabbedError &&
                !markedLessThanMarkedGrabbedError
        UrnMode.ALL -> listOf(n,m,k).all { it != null && it > 0 } &&
                !totalLessThanGrabbedError &&
                !totalLessThanMarkedError
    }
}

interface Validateable{
    val isValid: Boolean
}

@Immutable
data class CombinatoricsState(
    val repetitions: Boolean = false,
    val n: Int? = null,
    val k: Int? = null,
    val repetitionsNField: String = "",
    val mode: CombinatoricsMode = CombinatoricsMode.Placement,
    val result: Int? = null,
) : Validateable{
    val isKError = if(k != null && n != null){
        k > n
    }else false

    val repetitionsN = repetitionsNField
        .split(Regex("[, :\\-.=+$#@_]+"))
        .mapNotNull {
            it.toIntOrNull()
        }

    override val isValid = when(mode){
        CombinatoricsMode.Placement -> !isKError && n != null && n > 0 && k != null && k > 0
        CombinatoricsMode.Permutations -> if(repetitions){
            n != null && n > 0
        }else{
            repetitionsN.isNotEmpty() && repetitionsN.all { it > 0 }
        }
        CombinatoricsMode.Combination -> !isKError && n != null && k != null
    }
}

private fun Int.moreThanZero(): Boolean{
    return this > 0
}
interface LabeledEnum{
    val label: String
}
enum class CombinatoricsMode(
    override val label: String
): LabeledEnum { Placement("Размещения"), Permutations("Перестановки"), Combination("Сочетания") }
enum class UrnMode(
    override val label: String
): LabeledEnum{
    ALL("Все"), PARTIAL("Частичные")
}
enum class Mode(
    override val label: String
): LabeledEnum {  Combinatorics("Комбинаторика"), Urn("Урновая Модель")}
