package com.example.medicina.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.Dimension
import com.example.medicina.ui.theme.CustomBlack
import com.example.medicina.ui.theme.CustomGray
import com.example.medicina.ui.theme.CustomGreen

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    inputName: String = "",
    inputHint: String = "",
    inputValue: String = "",
    onValueChange: (String) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isMultiline: Boolean = false,
    contentAlignment: Alignment = Alignment.CenterStart,
    verticalArrangement: Alignment.Vertical = Alignment.CenterVertically,
    textPadding: PaddingValues = PaddingValues(0.dp),
    width: Dimension? = null,
    editable: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = if (width == null) modifier else modifier) {
        Text(
            modifier = Modifier
                .padding(start = 16.dp)
                .padding(bottom = 4.dp),
            text = inputName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = CustomBlack
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = 1.dp,
                    shape = RoundedCornerShape(20.dp),
                    color = if (isFocused) CustomGreen else CustomGray
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .clip(RoundedCornerShape(20.dp))
                    .padding(12.dp),
                verticalAlignment = verticalArrangement
            ) {

                Spacer(modifier = Modifier.width(12.dp))

                BasicTextField(
                    keyboardOptions = keyboardOptions,
                    visualTransformation = visualTransformation,
                    modifier = Modifier
                        .fillMaxSize()
                        .onFocusChanged { focusState -> isFocused = focusState.isFocused }
                        .padding(top = textPadding.calculateTopPadding()),
                    value = inputValue,
                    onValueChange = { if(editable) onValueChange(it) },
                    singleLine = !isMultiline,
                    textStyle = TextStyle(fontSize = 17.sp),
                    readOnly = !editable,
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = contentAlignment
                        ) {
                            if (inputValue.isEmpty()) {
                                Text(
                                    text = inputHint,
                                    color = CustomGray,
                                    fontSize = 17.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DropdownInputField(
    modifier: Modifier = Modifier,
    inputName: String = "",
    inputHint: String = "",
    selectedValue: String = "",
    onValueChange: (String) -> Unit = {},
    dropdownOptions: List<String> = emptyList(),
    contentAlignment: Alignment = Alignment.CenterStart,
    verticalArrangement: Alignment.Vertical = Alignment.CenterVertically,
    textPadding: PaddingValues = PaddingValues(0.dp),
    width: Dimension? = null,
    editable: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = if (width == null) modifier else modifier) {
        Text(
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp),
            text = inputName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = CustomBlack
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = 1.dp,
                    shape = RoundedCornerShape(20.dp),
                    color = if (isFocused || isExpanded) CustomGreen else CustomGray
                )
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .clip(RoundedCornerShape(20.dp))
                        .padding(12.dp)
                        .then(
                            if (editable) Modifier.clickable { isExpanded = !isExpanded }
                            else Modifier
                        ),
                    verticalAlignment = verticalArrangement
                ) {
                    Spacer(modifier = Modifier.width(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState -> isFocused = focusState.isFocused }
                            .padding(top = textPadding.calculateTopPadding()),
                        contentAlignment = contentAlignment
                    ) {
                        if (selectedValue.isEmpty()) {
                            Text(
                                text = inputHint,
                                color = CustomGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 17.sp
                            )
                        } else {
                            Text(
                                text = selectedValue,
                                color = CustomBlack,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 17.sp
                            )
                        }
                    }
                }

                if (isExpanded) {
                    DropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        dropdownOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onValueChange(option)
                                    isExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}