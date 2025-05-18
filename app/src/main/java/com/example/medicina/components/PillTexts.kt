package com.example.medicina.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicina.ui.theme.CustomBlack
import com.example.medicina.ui.theme.CustomGray
import com.example.medicina.ui.theme.CustomGreen
import com.example.medicina.ui.theme.CustomWhite

@Composable
fun InfoPills(
    modifier: Modifier = Modifier,
    infoColor: Color = CustomGreen,
    isClicked: Boolean = false,
    context: Context = LocalContext.current,
    onClickAction: () -> Unit = {
        Toast.makeText(context, "InfoPill clicked!", Toast.LENGTH_LONG).show()
    },
    content: @Composable () -> Unit = {}
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
                    contentColor = CustomGreen
                )
            },
        modifier = Modifier
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(20.dp),
                color = CustomGray
            )
            .wrapContentHeight()
            .fillMaxWidth()
            .then(modifier),
        shape = RoundedCornerShape(20.dp),
        onClick = onClickAction,
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ){
            Surface(
                color = infoColor,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .padding(10.dp)
                    .width(10.dp)
                    .fillMaxHeight()
            ) { }
            Column(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 12.dp, end  = 12.dp),
                verticalArrangement = Arrangement.Center
            ){
                content()
            }
        }

    }
}

@Composable
fun NotificationPillText(
    title: String = "Notification Title",
    subtitle: String = "Notification Subtitle",
    details: String = "Notification Details"){
    Column(modifier = Modifier){
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = CustomBlack
        )

        Text(text = subtitle,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color = CustomBlack
        )

        Text(text = details,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color = CustomBlack
        )
    }
}

@Composable
fun InventoryPillText(
    brandName: String = "Brand Name",
    genericName: String = "Generic Name",
    quantity: String = "",
    price: String = "0.00"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(modifier = Modifier.weight(1f)){
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
            Text(
                text = "Qty: $quantity",
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = CustomBlack
            )

            if(price != "0.00"){
                Text(
                    text = "P $price",
                    fontSize = 14.sp,
                    color = CustomBlack,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}


@Composable
fun CategoryPillText(
    categoryName: String = "Brand Name",
    medicineNumber: Int = 0,
    description: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(modifier = Modifier.weight(1f)){
            Text(
                text = categoryName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CustomBlack
            )
            Text(
                text = description,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = CustomBlack
            )
            Text(
                text = "Medicines: $medicineNumber",
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = CustomBlack
            )
        }
    }
}

@Composable
fun OrderPillText(
    orderedItem: String = "Ordered Item",
    supplier: String = "Supplier Name",
    date: String = "Order date",
    quantity: Int = 0,
    price: String = "0.00"
){
    Column(){
        Text(
            text = orderedItem,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = CustomBlack
        )

        Row() {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                Text(
                    text = supplier,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    color = CustomBlack
                )

                Text(
                    text = date,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    color = CustomBlack
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = "Qty: ${quantity.toString()}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    color = CustomBlack
                )

                Text(
                    text = "P $price",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    color = CustomBlack
                )
            }
        }
    }
}