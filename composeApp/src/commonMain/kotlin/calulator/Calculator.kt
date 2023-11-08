package calulator

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = getViewModel(Unit, factory = viewModelFactory { CalculatorViewModel() })
) {
    val state = viewModel.state.collectAsState().value

   Column {
       SelectorRow(
           labels = listOf("Combinations", "Urn"),
           selectedLabel = when(state.mode){
               Mode.Urn -> "Urn"
               Mode.Combinatorics -> "Combinations"
           },
           onLabelSelect = {
               viewModel.setMode(when(it){
                   "Combinations" -> Mode.Combinatorics
                   "Urn" ->  Mode.Urn
                   else -> Mode.Combinatorics
               })
           }
       )
        Divider()
       when(state.mode) {
           Mode.Urn -> SelectorRow(
               labels = listOf("All", "Partial"),
               selectedLabel = when(state.urnState.mode){
                   UrnMode.ALL -> "All"
                   UrnMode.PARTIAL ->  "Partial"
               },
               onLabelSelect = {
                   viewModel.setUrnState(urnMode = when(it){
                       "All" -> UrnMode.ALL
                       "Partial" -> UrnMode.PARTIAL
                       else -> UrnMode.PARTIAL
                   })
               }
           )
           Mode.Combinatorics -> SelectorRow(
               labels = listOf("Placement", "Permutations", "Combination"),
               selectedLabel = when(state.combinatoricsState.mode){
                   CombinatoricsMode.Placement -> "Placement"
                   CombinatoricsMode.Permutations -> "Permutations"
                   CombinatoricsMode.Combination -> "Combination"
               },
               onLabelSelect = {
                   viewModel.setCombinatoricsState(
                       combinatoricsMode = when(it){
                           "Placement" -> CombinatoricsMode.Placement
                           "Permutations" -> CombinatoricsMode.Permutations
                           "Combination" -> CombinatoricsMode.Combination
                           else -> CombinatoricsMode.Combination
                       }
                   )

               }
           )
       }
        Divider()

    }
}

val cornerSize = 8.dp

@Composable
fun SelectorRow(
    labels: List<String>,
    selectedLabel: String,
    onLabelSelect: (String) -> Unit,
){
    var size by remember { mutableStateOf(IntSize.Zero) }
    val index = labels.indexOf(selectedLabel)
    val offset by animateFloatAsState(
        targetValue = size.width.toFloat() * index
    )

    val color = Color(0xFFFFC8C8)

    Row(
        modifier = Modifier.drawBehind {
            drawRoundRect(
                color = color,
                topLeft = Offset(
                    x = offset,
                    y = 0f
                ),
                size = size.toSize(),
                cornerRadius = CornerRadius(cornerSize.toPx(), cornerSize.toPx())
            )
        }
    ) {
        labels.forEach {
            SelectorButton(
                modifier = Modifier
                    .onPlaced { size = it.size },
                label = it,
                onClick = { onLabelSelect(it) }
            )
        }
    }
}

@Composable
fun RowScope.SelectorButton(
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit,
){
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerSize))
            .clickable(onClick = onClick)
            .height(40.dp)
            .weight(1f),
        contentAlignment = Alignment.Center
    ){
        Text(text = label)
    }
}