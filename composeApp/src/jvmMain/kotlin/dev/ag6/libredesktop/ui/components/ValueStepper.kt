package dev.ag6.libredesktop.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ValueStepper(
    title: String = "Stepper",
    value: Float = 0f,
    step: Float = 1f,
    min: Float = Float.MIN_VALUE,
    max: Float = Float.MAX_VALUE,
    allowDecimal: Boolean = false,
    valueFormatter: (Float) -> String = { if (allowDecimal) it.toString() else it.toInt().toString() },
    valueParser: (String) -> Float? = { if (allowDecimal) it.toFloatOrNull() else it.toIntOrNull()?.toFloat() },
    previousValue: (Float) -> Float = { it - step },
    nextValue: (Float) -> Float = { it + step },
    onValueChange: (Float) -> Unit = {}
) {
    ValueStepper(
        label = { Text(title, style = MaterialTheme.typography.bodyMedium) },
        value = value,
        step = step,
        min = min,
        max = max,
        allowDecimal = allowDecimal,
        valueFormatter = valueFormatter,
        valueParser = valueParser,
        previousValue = previousValue,
        nextValue = nextValue,
        onValueChange = onValueChange
    )
}

@Composable
fun ValueStepper(
    label: @Composable () -> Unit = { Text("Stepper", style = MaterialTheme.typography.bodyMedium) },
    value: Float = 0f,
    step: Float = 1f,
    min: Float = Float.MIN_VALUE,
    max: Float = Float.MAX_VALUE,
    allowDecimal: Boolean = false,
    valueFormatter: (Float) -> String = { if (allowDecimal) it.toString() else it.toInt().toString() },
    valueParser: (String) -> Float? = { if (allowDecimal) it.toFloatOrNull() else it.toIntOrNull()?.toFloat() },
    previousValue: (Float) -> Float = { it - step },
    nextValue: (Float) -> Float = { it + step },
    onValueChange: (Float) -> Unit = {}

) {
    fun validateInput(input: String): Boolean {
        if (input.isEmpty()) return true
        if (input == "-") return min < 0f

        return if (allowDecimal) {
            input.count { it == '.' } <= 1 && valueParser(input) != null
        } else {
            valueParser(input) != null
        }
    }

    var inputText by remember(allowDecimal) { mutableStateOf(valueFormatter(value)) }
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(value, allowDecimal, isFocused) {
        if (!isFocused) inputText = valueFormatter(value)
    }

    fun commit() {
        val valueToFloat = valueParser(inputText)
        if (valueToFloat != null && valueToFloat in min..max) {
            onValueChange(valueToFloat)
            inputText = valueFormatter(valueToFloat)
        } else {
            inputText = valueFormatter(value)
        }
    }

    val previous = previousValue(value)
    val next = nextValue(value)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        label()
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onValueChange(previous) },
                enabled = previous >= min,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease $label",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
            }
            Box(contentAlignment = Alignment.Center) {
                BasicTextField(
                    value = inputText,
                    onValueChange = {
                        if (validateInput(it)) {
                            inputText = it
                            val parsedValue = valueParser(it)
                            if (parsedValue != null && parsedValue in min..max) onValueChange(parsedValue)
                        }
                    },
                    modifier = Modifier
                        .width(64.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.extraSmall)
                        .padding(horizontal = 6.dp, vertical = 5.dp)
                        .onFocusChanged { state ->
                            if (isFocused && !state.isFocused) commit()
                            isFocused = state.isFocused
                        },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (allowDecimal) KeyboardType.Decimal else KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { commit() })
                )
            }
            IconButton(
                onClick = { onValueChange(next) },
                enabled = next <= max,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Increase $label",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
