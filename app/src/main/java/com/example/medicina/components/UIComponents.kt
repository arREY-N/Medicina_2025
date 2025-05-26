package com.example.medicina.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.sp
import com.example.medicina.ui.theme.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.medicina.components.LayoutGuidelines.setupColumnGuidelines
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.example.medicina.model.Order
import java.text.SimpleDateFormat
import java.util.Locale
import android.graphics.Color as AndroidColor

@Preview(showBackground = true)
@Composable
fun UIPreview() {
    ComposePracticeTheme {
        var expirationDate by remember { mutableStateOf("") }

        DatePickerInputField(
            inputName = "Expiration Date",
            inputHint = "Choose date",
            selectedDate = expirationDate,
            onDateSelected = { expirationDate = it }
        )
    }
}

@Composable
fun ExpandableTextSurface(
    modifier: Modifier,
    text: String,
    collapsedMaxLines: Int = 5
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isOverflowing by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .then(modifier),
        color = CustomWhite
    ) {
        Column(modifier = Modifier) {
            Text(
                text = text,
                maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLines,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { result ->
                    if (!isExpanded) {
                        isOverflowing = result.hasVisualOverflow
                    }
                },
                lineHeight = 20.sp,
                color = CustomBlack
            )

            if (isOverflowing || isExpanded) {
                Spacing(4.dp)
                Text(
                    textAlign = TextAlign.Center,
                    text = if (isExpanded) "READ LESS" else "READ MORE",
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { isExpanded = !isExpanded },
                    color = CustomGreen
                )
            }
        }
    }
}

@Composable
fun InfoCard(
    infoLabel: String = "",
    infoValue: Int = 0,
    modifier: Modifier
) {
    Surface(
        modifier = Modifier
            .height(80.dp)
            .border(
                1.dp,
                CustomGray,
                RoundedCornerShape(20.dp)
            )
            .then(modifier),
        color = CustomWhite
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                infoLabel,
                modifier = Modifier.weight(1f),
                color = CustomBlack,
                textAlign = TextAlign.Right,
            )

            Text(
                "$infoValue",
                modifier = Modifier.weight(1f),
                color = CustomBlack,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MedicineOverview(
    modifier: Modifier = Modifier,
    brandName: String = "Brand Name",
    genericName: String = "Generic Name",
    price: String = "P0.00",
    category: String = "Category",
    regulation: String = "Regulation",
    iconColor: String = "#000000"
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        val guidelines = setupColumnGuidelines()
        val (medIcon, medName) = createRefs()

        Surface(
            modifier = Modifier
                .constrainAs(medIcon){
                    top.linkTo(parent.top)
                    start.linkTo(guidelines.c1start)
                    end.linkTo(guidelines.c1end)
                    width = Dimension.fillToConstraints
                    height = Dimension.ratio("1:1")
                },
            color = Color(AndroidColor.parseColor(iconColor))
        ) {}

        Row(
            modifier = Modifier
                .constrainAs(medName){
                    start.linkTo(guidelines.c2start)
                    end.linkTo(guidelines.c4end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                }
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(modifier = Modifier.weight(2f)){
                Text(
                    text = brandName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CustomBlack
                )

                Text(text = genericName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    color = CustomBlack
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.End){

                if(price != "0.00"){
                    Text(
                        text = "P $price",
                        fontSize = 14.sp,
                        color = CustomBlack,
                        fontWeight = FontWeight.Light
                    )
                }
                Text(
                    text = category,
                    fontSize = 14.sp,
                    color = CustomBlack,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = regulation,
                    fontSize = 14.sp,
                    color = CustomBlack,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerInputField(
    modifier: Modifier = Modifier,
    inputName: String = "Date",
    inputHint: String = "Select a date",
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val datePickerState = rememberDatePickerState()
    var showDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    if (showDialog) {
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
            DatePicker(state = datePickerState)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
            .padding(vertical = 4.dp)
    ) {
        InputField(
            inputName = inputName,
            inputHint = inputHint,
            inputValue = selectedDate,
            onValueChange = {}, // prevent manual input
            isMultiline = false,
            keyboardOptions = KeyboardOptions.Default
        )
    }
}

@Composable
fun PageHeader(
    modifier: Modifier = Modifier,
    title: String = "Page Header"
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Global.edgeMargin * 0.5f)
            .then(modifier),
        color = CustomWhite
    ){
        Text(
            color = CustomBlack,
            text = title,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Spacing(space: Dp = 8.dp){
    Spacer(modifier = Modifier
        .height(space)
        .width(space))
}