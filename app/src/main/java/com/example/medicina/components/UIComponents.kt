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
fun OrderTable(
    modifier: Modifier,
    tableData: List<Order>
){
    Surface(
        modifier = Modifier
            .wrapContentHeight()
            .border(
                1.dp,
                CustomGray,
                RoundedCornerShape(20.dp)
            )
            .then(modifier),
        color = CustomWhite
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Quantity",
                modifier = Modifier.weight(1f).wrapContentWidth(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold,
                color = CustomBlack
            )
            Text(
                "Date",
                modifier = Modifier.weight(1f).wrapContentWidth(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold,
                color = CustomBlack
            )
            Text(
                "Expiration",
                modifier = Modifier.weight(1f).wrapContentWidth(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold,
                color = CustomBlack
            )
        }

        tableData.forEach { order ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    order.quantity.toString(),
                    modifier = Modifier.weight(1f).wrapContentWidth(Alignment.CenterHorizontally),
                    color = CustomBlack
                )
                Text(
                    order.expirationDate.toString(),
                    modifier = Modifier.weight(2f).wrapContentWidth(Alignment.CenterHorizontally),
                    color = CustomBlack
                )
                Text(
                    order.orderDate.toString(),
                    modifier = Modifier.weight(2f).wrapContentWidth(Alignment.CenterHorizontally),
                    color = CustomBlack
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
    regulation: String = "Regulation"
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        val guidelines = setupColumnGuidelines()
        val (
            medIcon,
            medName) = createRefs()

        Surface(
            modifier = Modifier
                .constrainAs(medIcon){
                    top.linkTo(parent.top)
                    start.linkTo(guidelines.c1start)
                    end.linkTo(guidelines.c1end)
                    width = Dimension.fillToConstraints
                    height = Dimension.ratio("1:1")
                },
            color = CustomGray
        ) {}

        Column(
            modifier = Modifier
                .constrainAs(medName) {
                    top.linkTo(medIcon.top)
                    bottom.linkTo(medIcon.bottom)
                    start.linkTo(guidelines.c2start)
                    end.linkTo(guidelines.c4end)
                    width = Dimension.fillToConstraints
                }
        ) {
            Text(
                brandName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CustomBlack)
            Spacing(4.dp)

            Text(genericName,
                color = CustomBlack)

            Spacing(4.dp)
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("P $price",
                    color = CustomBlack)
                Text(category,
                    color = CustomBlack)
                Text(regulation,
                    color = CustomBlack)
            }
        }
    }
}

@Composable
fun SearchBar(navController: NavController){

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
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

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
fun Spacing(space: Dp = 8.dp){
    Spacer(modifier = Modifier
        .height(space)
        .width(space))
}