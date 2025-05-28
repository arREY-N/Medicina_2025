package com.example.medicina.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.medicina.ui.theme.CustomBlack
import com.example.medicina.ui.theme.CustomGray
import com.example.medicina.ui.theme.CustomGreen
import com.example.medicina.ui.theme.CustomWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UIButton(
    text: String = "",
    modifier: Modifier = Modifier,
    onClickAction: () -> Unit = {},
    isCTA: Boolean = true,
    height: Dp = 40.dp
) {
    Button (
        colors =
            if (isCTA) {
                ButtonDefaults.buttonColors(
                    containerColor = CustomGreen,
                    contentColor = CustomWhite
                )
            } else {
                ButtonDefaults.buttonColors(
                    containerColor = CustomWhite,
                    contentColor = CustomGreen
                )
            },
        modifier = Modifier
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(20.dp),
                color = CustomGreen
            )
            .height(height)
            .then(modifier),
        shape = RoundedCornerShape(20.dp),
        onClick = onClickAction
    ) {
        Text(text = text)
    }
}

@Composable
fun ButtonBox(
    text: String,
    inheritedModifier: Modifier = Modifier,
    onClickAction: () -> Unit,
    isClicked: Boolean = false,
    iconSize: Dp = 35.dp,
    fontSize: TextUnit = 14.sp,
    iconId: Int = 0
){
    Button (
        colors =
            if (isClicked) {
                ButtonDefaults.buttonColors(
                    containerColor = CustomGreen,
                    contentColor = CustomWhite
                )
            } else {
                ButtonDefaults.buttonColors(
                    containerColor = CustomWhite,
                    contentColor = CustomBlack
                )
            },
        modifier = Modifier
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(20.dp),
                color = CustomGray
            )
            .then(inheritedModifier),
        shape = RoundedCornerShape(20.dp),
        onClick = onClickAction,
        contentPadding = PaddingValues(6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center){
            Surface(
                color = CustomWhite,
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .size(iconSize)
            ) {
                if(iconId != 0){
                    Image(
                        painter = painterResource(id = iconId),
                        contentDescription = "Menu button",
                        modifier = Modifier.size(50.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            Spacing(4.dp)

            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = fontSize
            )
        }
    }
}

@Composable
fun EditButton(
    text: String = "Edit",
    onEdit: () -> Unit = {},
    isCTA: Boolean = false
){
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (editButton) = createRefs()

        Surface(
            modifier = Modifier
                .constrainAs(editButton) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
                .fillMaxWidth(),
            color = Color.Transparent,
            shape = RoundedCornerShape(50.dp)
        ) {
            UIButton(
                text = text,
                modifier =  Modifier.fillMaxSize(),
                onClickAction = { onEdit() },
                isCTA = isCTA,
                height = 40.dp
            )
        }
    }
}


@Composable
fun CreateButton(
    buttonText: String = "Create New",
    onclick: () -> Unit = {},
    inheritedModifier: Modifier = Modifier){
    Button (
        colors = ButtonDefaults.buttonColors(
            containerColor = CustomWhite,
            contentColor = CustomBlack
        ),
        modifier = Modifier
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(20.dp),
                color = CustomGray
            )
            .height(88.dp)
            .fillMaxWidth()
            .then(inheritedModifier),
        shape = RoundedCornerShape(20.dp),
        onClick = onclick,
        contentPadding = PaddingValues(0.dp)
    ) {
        Surface(
            modifier = Modifier
                .padding(4.dp)
                .border(
                    width = 1.dp,
                    shape = RoundedCornerShape(16.dp),
                    color = CustomGray
                )
                .fillMaxSize(),
            color = CustomWhite
        ){
            Column(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = buttonText,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    inputName: String = "",
    inputHint: String = "",
    editable: Boolean = true,
    selectedDate: String = "",
    onDateSelected: (String) -> Unit,
) {
    val datePickerState = rememberDatePickerState()
    var showDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    var isFocused by remember { mutableStateOf(false) }


    if (showDialog && editable) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                onSurfaceVariant = CustomGray
            )
        ) {
            DatePickerDialog(
                onDismissRequest = {
                    showDialog = false
                    isFocused = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDateSelected(dateFormat.format(Date(millis)))
                        }
                        showDialog = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                androidx.compose.material3.DatePicker(state = datePickerState)
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier
                .padding(start = 16.dp, bottom = 4.dp),
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
                .clickable(enabled = editable) {
                    isFocused = true
                    showDialog = true
                },
            color = CustomWhite
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = if (selectedDate.isEmpty()) inputHint else selectedDate,
                    fontSize = 17.sp,
                    color = if (selectedDate.isEmpty()) CustomGray else CustomBlack
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XDatePicker(
    inputName: String = "",
    inputHint: String = "",
    editable: Boolean = true,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
){
    val datePickerState = rememberDatePickerState()
    var showDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    var isFocused by remember { mutableStateOf(false) }

    if (showDialog && editable) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(dateFormat.format(java.util.Date(millis)))
                    }
                    showDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            androidx.compose.material3.DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier
                .padding(start = 16.dp)
                .padding(bottom = 4.dp),
            text = inputName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = CustomBlack
        )

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomWhite,
                contentColor = CustomGray
            ),
            onClick = {
                isFocused = !isFocused
                showDialog = true
            },
            modifier = Modifier
                .padding(0.dp)
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
                    .background(CustomWhite)
                    .clip(RoundedCornerShape(20.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(selectedDate.isEmpty()){
                    Text(
                        text = inputHint,
                        color = CustomGray,
                        fontSize = 17.sp
                    )
                } else {
                    Text(
                        text = selectedDate,
                        color = CustomBlack,
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
}