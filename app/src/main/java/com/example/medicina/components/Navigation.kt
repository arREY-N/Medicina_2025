package com.example.medicina.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.medicina.components.LayoutGuidelines.setupColumnGuidelines
import com.example.medicina.ui.theme.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.medicina.R

fun NavHostController.navigateBottomTab(route: String) {
    println("Going to: $route")
    this.navigate(route) {
        popUpTo("bottom_nav_root") {
            inclusive = true
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavHostController.navigateMenuTab(route: String) {
    this.navigate(route){
        popUpTo("menu_nav_root") {
            inclusive = true
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun NavigationBar(
    navController: NavHostController
) {
    Surface(
        color = CustomGreen,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                val guidelines = setupColumnGuidelines()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val(nav1,
                    nav2,
                    nav3,
                    nav4) = createRefs()

                NavigationMenu(
                    text = "Home",
                    onClickAction = {
                        navController.navigateBottomTab(Screen.Home.route)
                    },
                    isClicked = currentRoute == Screen.Home.route,
                    inheritedModifier = Modifier.constrainAs(nav1){
                        start.linkTo(guidelines.c1start)
                        end.linkTo(guidelines.c1end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    },
                    iconDefault = R.drawable.home_ia,
                    iconSelected = R.drawable.home_ac
                )

                NavigationMenu(
                    text = "Search",
                    onClickAction = {
                        navController.navigateBottomTab(Screen.Search.route)
                    },
                    isClicked = currentRoute == Screen.Search.route,
                    inheritedModifier = Modifier.constrainAs(nav2){
                        start.linkTo(guidelines.c2start)
                        end.linkTo(guidelines.c2end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    },
                    iconDefault = R.drawable.search_ia,
                    iconSelected = R.drawable.search_ac
                )

                NavigationMenu(
                    text = "Inventory",
                    onClickAction = {
                        navController.navigateBottomTab(Screen.Inventory.route)
                    },
                    isClicked = currentRoute == Screen.Inventory.route,
                    inheritedModifier = Modifier.constrainAs(nav3){
                        start.linkTo(guidelines.c3start)
                        end.linkTo(guidelines.c3end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    },
                    iconDefault = R.drawable.inventory_ia,
                    iconSelected = R.drawable.inventory_ac
                )

                NavigationMenu(
                    text = "Orders",
                    onClickAction = {
                        navController.navigateBottomTab(Screen.Orders.route)
                    },
                    isClicked = currentRoute == Screen.Orders.route,
                    inheritedModifier = Modifier.constrainAs(nav4){
                        start.linkTo(guidelines.c4start)
                        end.linkTo(guidelines.c4end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    },
                    iconDefault = R.drawable.orders_ia,
                    iconSelected = R.drawable.orders_ac
                )
            }
        }
    }
}

@Composable
fun TopNavigation(
    isHome: Boolean = false,
    pageTitle: String = "Medicina",
    navController: NavHostController,
    showMenu: Boolean = true
) {
    val context = LocalContext.current
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        color = CustomGreen
    ) {
        ConstraintLayout(
            modifier =  Modifier
                .padding(horizontal = Global.edgeMargin, vertical = 16.dp),
        ) {
            val guidelines = setupColumnGuidelines()

            val (back, title, account) = createRefs()

            Surface(
                modifier = Modifier
                    .constrainAs(back) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(guidelines.c4end)
                        height = Dimension.fillToConstraints
                        width = Dimension.ratio("1:1")
                    },
                color = Color.Transparent
            ) {
                if(showMenu){
                    Button(
                        onClick = {
                            navController.navigate(Screen.MainMenu.route)
                        },
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContentColor = CustomGreen,
                            disabledContainerColor = CustomGreen,
                        ),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.menu),
                            contentDescription = "navigation icon",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                }
            }

            Text(
                text = pageTitle,
                modifier = if(!isHome) {
                    Modifier
                        .constrainAs(title) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(guidelines.c1start)
                            end.linkTo(guidelines.c4end)
                        }
                        .background(Color.Transparent)
                } else {
                    Modifier
                        .constrainAs(title) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(guidelines.c1start)
                        }
                        .background(Color.Transparent)
                },
                color = CustomWhite,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            if(!isHome){
                Surface(
                    modifier = Modifier
                        .constrainAs(account) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(guidelines.c1start)
                            height = Dimension.fillToConstraints
                            width = Dimension.ratio("1:1")
                        },
                    color = Color.Transparent
                ) {
                    Button(
                        onClick = {
                            navController.popBackStack()
                        },
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "navigation icon",
                            modifier = Modifier.size(65.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationMenu(
    text: String,
    inheritedModifier: Modifier = Modifier,
    onClickAction: () -> Unit,
    isClicked: Boolean = false,
    iconDefault: Int = 0,
    iconSelected: Int = 0
){
    Button (
        colors = ButtonDefaults.buttonColors(
            containerColor = CustomGreen,
            contentColor = CustomWhite,
            disabledContainerColor = CustomGreen,
            disabledContentColor = CustomWhite),
        modifier = Modifier
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(20.dp),
                color = CustomGreen
            )
            .then(inheritedModifier),
        shape = RoundedCornerShape(20.dp),
        onClick = onClickAction,
        contentPadding = PaddingValues(0.dp),
        enabled = !isClicked
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center){
            Surface(
                color = CustomGreen,
                modifier = Modifier.size(35.dp)
            ) {
                Image(
                    painter = if(isClicked) painterResource(id = iconSelected) else painterResource(id = iconDefault),
                    contentDescription = "navigation icon",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            Spacing(4.dp)
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (isClicked) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun Confirm(
    action: String = "Confirm Action",
    cancelOnclick: () -> Unit = { },
    confirmOnclick: () -> Unit = { }
){
    Spacing(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Cancel button: square
        Button(
            onClick = cancelOnclick,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomRed,
                contentColor = CustomWhite
            ),
            modifier = Modifier
                .aspectRatio(1f)         // square: width = height
                .fillMaxHeight()         // match Row height
        ) {
            Image(
                painter = painterResource(id = R.drawable.trash),
                contentDescription = "Delete",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Confirm button: fills remaining space
        Button(
            onClick = confirmOnclick,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomGreen,
                contentColor = CustomWhite
            ),
            modifier = Modifier
                .weight(1f)              // take all remaining horizontal space
                .fillMaxHeight()         // match Row height
        ) {
            Text(text = action, fontSize = 18.sp)
        }
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview10(){

}