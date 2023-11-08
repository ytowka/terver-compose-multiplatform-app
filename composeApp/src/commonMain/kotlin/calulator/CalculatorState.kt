package calulator

data class ScreenState(
    val mode: Mode = Mode.Combinatorics,
    val urnState: UrnState = UrnState(
        n = 0,
        r = 0,
        mode = UrnMode.ALL
    ),
    val combinatoricsState: CombinatoricsState = CombinatoricsState(
        repetitions = false,
        n = 0L,
        k = 0L,
        mode = CombinatoricsMode.Combination
    ),
)

data class UrnState(
    val n: Long,
    val r: Long,
    val mode: UrnMode,
)


data class CombinatoricsState(
    val repetitions: Boolean,
    val n: Long?,
    val k: Long?,
    val mode: CombinatoricsMode,
)

/*sealed interface StartUserEvent : StartEvent{

    data class SetMode(val mode: Mode) : StartUserEvent

    data class SetCombinatoricsMode(val combinatoricsMode: CombinatoricsMode) : StartUserEvent
    data class SetUrnMode(val urnMode: UrnMode) : StartUserEvent

    data class SetRepetitions(val enabled: Boolean) : StartUserEvent
}*/

enum class CombinatoricsMode { Placement, Permutations, Combination }
enum class UrnMode{
    ALL, PARTIAL
}
enum class Mode { Urn , Combinatorics}
