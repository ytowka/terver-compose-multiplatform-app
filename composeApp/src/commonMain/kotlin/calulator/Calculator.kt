package calulator

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource


private const val MAX_FIELD_LENGTH = 6
val cornerSize = 8.dp
val cardCornerSize = 12.dp

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = getViewModel(
        Unit,
        factory = viewModelFactory { CalculatorViewModel() })
) {
    val state = viewModel.state.collectAsState().value

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SelectorRow(
            labels = Mode.entries,
            selectedLabel = state.mode,
            onLabelSelect = {
                viewModel.setMode(it)
            }
        )
        Divider()
        when (state.mode) {
            Mode.Urn -> SelectorRow(
                labels = UrnMode.entries,
                selectedLabel = state.urnState.mode,
                onLabelSelect = {
                    viewModel.setUrnState(it)
                }
            )

            Mode.Combinatorics -> SelectorRow(
                labels = CombinatoricsMode.entries,
                selectedLabel = state.combinatoricsState.mode,
                onLabelSelect = {
                    viewModel.setCombinatoricsState(it)
                }
            )
        }
        Divider()

        Spacer(modifier = Modifier.size(5.dp))
        AnimatedContent(
            targetState = state.mode,
            contentAlignment = Alignment.Center
        ) {
            when (it) {
                Mode.Urn -> UrnFields(
                    urnState = state.urnState,
                    onNChange = { viewModel.setUrnNField(it) },
                    onMChange = { viewModel.setUrnMField(it) },
                    onKChange = { viewModel.setUrnKField(it) },
                    onRChange = { viewModel.setUrnRField(it) },

                )
                Mode.Combinatorics -> CombinatoricsFields(
                    combState = state.combinatoricsState,
                    onNChange = { viewModel.setCombNField(it) },
                    onKChange = { viewModel.setCombKField(it) },
                    onRepetitionsCheck = { viewModel.setRepetitions(it) },
                    onMultiNChange = { viewModel.setCombMultiNField(it) }
                )
            }
        }
        Spacer(modifier = Modifier.size(5.dp))
        Button(
            onClick = { viewModel.calculate() },
            enabled = when(state.mode){
                Mode.Combinatorics -> state.combinatoricsState.isValid
                Mode.Urn -> state.urnState.isValid
            }
        ){
            Text("Посчитать")
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CombinatoricsFields(
    combState: CombinatoricsState,
    onNChange: (Int?) -> Unit,
    onKChange: (Int?) -> Unit,
    onMultiNChange: (String) -> Unit,
    onRepetitionsCheck: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(cardCornerSize))
            .background(color = MaterialTheme.colors.surface, shape = RoundedCornerShape(cardCornerSize))
            .padding(16.dp)
            .animateContentSize()
            ,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .size(250.dp, 70.dp),
            painter = painterResource(
                when(combState.mode){
                    CombinatoricsMode.Placement -> if(combState.repetitions){
                        "placement-w-reps.png"
                    }else "placement-no-reps.png"
                    CombinatoricsMode.Permutations -> if(combState.repetitions){
                        "perms-w-reps.png"
                    }else "perms-no-reps.png"
                    CombinatoricsMode.Combination -> if(combState.repetitions){
                        "combs-w-reps.png"
                    }else "combs-no-reps.png"
                }),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.size(5.dp))
        if(combState.mode == CombinatoricsMode.Permutations && combState.repetitions){
            OutlinedTextField(
                value = combState.repetitionsNField,
                onValueChange = {
                    onMultiNChange(it)
                },
                maxLines = 1,
                singleLine = true,
                label = {
                    Text("Элементы (n₁ n₂ n n₃ ...)")
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }else{
            OutlinedTextField(
                value = combState.n?.toString() ?: "",
                onValueChange = {
                    if(it.length <= MAX_FIELD_LENGTH) onNChange(it.toIntOrNull())
                },
                maxLines = 1,
                singleLine = true,
                label = {
                    Text("Всего (n)")
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }
        Spacer(modifier = Modifier.size(5.dp))
        val error = combState.isKError
        AnimatedVisibility(combState.mode != CombinatoricsMode.Permutations){
            Column(
                modifier = Modifier.width(IntrinsicSize.Min)
            ) {
                OutlinedTextField(
                    value = combState.k?.toString() ?: "",
                    onValueChange = {
                        if(it.length <= MAX_FIELD_LENGTH) onKChange(it.toIntOrNull())
                    },
                    maxLines = 1,
                    singleLine = true,
                    label = {
                        Text("Выборка (k)")
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = error,
                )
                Spacer(modifier = Modifier.size(2.dp))
                if(error){
                    Text(
                        text = "Выборка должна быть меньше\nколичества всех элементов",
                        color = MaterialTheme.colors.error,
                        fontSize = 12.sp
                    )
                }
            }

        }
        Spacer(modifier = Modifier.size(5.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = combState.repetitions,
                onCheckedChange = onRepetitionsCheck
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text("С повторениями")
        }
        AnimatedVisibility(combState.result != null){
            Column{
                Spacer(modifier = Modifier.size(5.dp))
                Text(
                    text = "Ответ: ${combState.result}",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun UrnFields(
    urnState: UrnState,
    onNChange: (Int?) -> Unit,
    onMChange: (Int?) -> Unit,
    onKChange: (Int?) -> Unit,
    onRChange: (Int?) -> Unit,
){
    Column(
        modifier = Modifier
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(cardCornerSize))
            .background(color = MaterialTheme.colors.surface, shape = RoundedCornerShape(cardCornerSize))
            .padding(16.dp)
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(200.dp, 90.dp),
            painter = painterResource(when(urnState.mode){
                UrnMode.ALL -> "urn-all.png"
                UrnMode.PARTIAL -> "urn-part.png"
            }),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.size(5.dp))

        OutlinedTextField(
            value = urnState.n?.toString() ?: "",
            onValueChange = {
                if(it.length <= MAX_FIELD_LENGTH) onNChange(it.toIntOrNull())
            },
            maxLines = 1,
            singleLine = true,
            label = {
                Text("Всего элементов (n)")
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.size(5.dp))
        OutlinedTextField(
            value = urnState.m?.toString() ?: "",
            onValueChange = {
                if(it.length <= MAX_FIELD_LENGTH) onMChange(it.toIntOrNull())
            },
            maxLines = 1,
            singleLine = true,
            label = {
                Text("Меченых элементов (m)")
            },
            isError = urnState.totalLessThanMarkedError,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        if(urnState.totalLessThanMarkedError){
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text = "Меченых элементов должно быть\nменьше количества всех элементов",
                color = MaterialTheme.colors.error,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.size(25.dp))
        OutlinedTextField(
            value = urnState.k?.toString() ?: "",
            onValueChange = {
                if(it.length <= MAX_FIELD_LENGTH) onKChange(it.toIntOrNull())
            },
            maxLines = 1,
            singleLine = true,
            label = {
                Text("Взяли (k)")
            },
            isError = urnState.totalLessThanGrabbedError,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        if(urnState.totalLessThanGrabbedError){
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text = "Выборка должна быть\nменьше количества всех элементов",
                color = MaterialTheme.colors.error,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.size(5.dp))
        AnimatedVisibility(urnState.mode == UrnMode.PARTIAL){
            Column(

            ) {
                OutlinedTextField(
                    value = urnState.r?.toString() ?: "",
                    onValueChange = {
                        if(it.length <= MAX_FIELD_LENGTH) onRChange(it.toIntOrNull())
                    },
                    maxLines = 1,
                    singleLine = true,
                    label = {
                        Text("Меченых среди взятых (r)")
                    },
                    isError = urnState.grabbedLessThanMarkedGrabbedError || urnState.markedLessThanMarkedGrabbedError,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.size(5.dp))
                if(urnState.grabbedLessThanMarkedGrabbedError){
                    Spacer(modifier = Modifier.size(2.dp))
                    Text(
                        text = "Выбранных меченых должно быть\nменьше чем всего взятых",
                        color = MaterialTheme.colors.error,
                        fontSize = 12.sp
                    )
                }else if(urnState.markedLessThanMarkedGrabbedError){
                    Spacer(modifier = Modifier.size(2.dp))
                    Text(
                        text = "Выбранных меченых должно\nбыть меньше чем всего меченых",
                        color = MaterialTheme.colors.error,
                        fontSize = 12.sp
                    )
                }
            }

        }
        AnimatedVisibility(urnState.result != null){
            Column {
                Spacer(modifier = Modifier.size(5.dp))
                Text(
                    text = "Вероятность: ${urnState.result}",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun <T : LabeledEnum> SelectorRow(
    labels: List<T>,
    selectedLabel: T,
    onLabelSelect: (T) -> Unit,
) {
    val index = labels.indexOf(selectedLabel)
    val indexMultiplier by animateFloatAsState(
        targetValue = index.toFloat()
    )

    val color = MaterialTheme.colors.secondaryVariant

    Row(
        modifier = Modifier.drawBehind {
            val width = size.width / labels.size
            drawRoundRect(
                color = color,
                topLeft = Offset(
                    x = width * indexMultiplier,
                    y = 0f
                ),
                size = size.copy(width = width),
                cornerRadius = CornerRadius(cornerSize.toPx(), cornerSize.toPx())
            )
        }
    ) {
        labels.forEach {
            SelectorButton(
                label = it.label,
                selected = it == selectedLabel,
                onClick = { onLabelSelect(it) }
            )
        }
    }
}

@Composable
fun RowScope.SelectorButton(
    modifier: Modifier = Modifier,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerSize))
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() })
            .height(40.dp)
            .weight(1f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}