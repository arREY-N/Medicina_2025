package com.example.medicina.components

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.medicina.R

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
                        navController.navigate(Screen.Home.route){
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                        navController.navigate(Screen.Search.route){
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                        navController.navigate(Screen.Inventory.route){
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = false
                            restoreState = true
                        }
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
                        navController.navigate(Screen.Orders.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
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
    navController: NavHostController
) {
    var displayMenu by remember { mutableStateOf(false) }

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
                Button(
                    onClick = {
                        if(!displayMenu){
                            navController.navigate(Screen.MainMenu.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            navController.popBackStack()
                        }
                        displayMenu = !displayMenu
                    },
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
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
            contentColor = CustomWhite),
        modifier = Modifier
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(20.dp),
                color = CustomGreen
            )
            .then(inheritedModifier),
        shape = RoundedCornerShape(20.dp),
        onClick = onClickAction,
        contentPadding = PaddingValues(0.dp)
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
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val guidelines = setupColumnGuidelines()
        val (cancelButton, confirmButtom) = createRefs()
        Button(
            modifier = Modifier.constrainAs(cancelButton){
                top.linkTo(parent.top)
                start.linkTo(guidelines.c1start)
                end.linkTo(guidelines.c1end)
                width = Dimension.fillToConstraints
                height = Dimension.ratio("1:1")
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomRed,
                contentColor = CustomWhite),
            onClick = cancelOnclick
        ) {
            Image(
                painter = painterResource(id = R.drawable.trash),
                contentDescription = "navigation icon",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        Button(
            modifier = Modifier.constrainAs(confirmButtom){
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(guidelines.c2start)
                end.linkTo(guidelines.c4end)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CustomGreen,
                contentColor = CustomWhite),
            onClick = confirmOnclick
        ) {
            Text(action, fontSize = 18.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview10(){

}


//                        navController.navigate(Screen.Notifications.route) {
//                            popUpTo(navController.graph.startDestinationId) {
//                                saveState = true
//                            }
//                            launchSingleTop = true
//                            restoreState = true
//                        }