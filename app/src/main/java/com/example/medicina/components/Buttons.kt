package com.example.medicina.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
fun EditButton(onEdit: () -> Unit = {},){
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
                text = "Edit",
                modifier =  Modifier.fillMaxSize(),
                onClickAction = { onEdit() },
                isCTA = false,
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