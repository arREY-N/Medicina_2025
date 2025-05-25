package com.example.medicina.view

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.medicina.components.LayoutGuidelines.setupColumnGuidelines
import com.example.medicina.ui.theme.ComposePracticeTheme
import com.example.medicina.ui.theme.CustomGreen
import com.example.medicina.ui.theme.CustomRed
import com.example.medicina.ui.theme.CustomWhite
import androidx.navigation.NavController
import com.example.medicina.ui.theme.CustomBlack
import com.example.medicina.components.AccountScreen
import com.example.medicina.components.ButtonBox
import com.example.medicina.components.CategoriesScreen
import com.example.medicina.components.CategoryMedicine
import com.example.medicina.components.Global
import com.example.medicina.components.InfoPills
import com.example.medicina.components.InputField
import com.example.medicina.components.InventoryPillText
import com.example.medicina.components.NavigationBar
import com.example.medicina.components.NotificationPillText
import com.example.medicina.components.ReadMedicine
import com.example.medicina.components.Screen
import com.example.medicina.components.TopNavigation
import com.example.medicina.components.UIButton
import com.example.medicina.components.UpsertCategoryScreen
import com.example.medicina.components.UpsertMedicineScreen
import com.example.medicina.components.UpsertOrderScreen
import com.example.medicina.components.UpsertSuppliersScreen
import com.example.medicina.components.ViewAccounts
import com.example.medicina.components.ViewSuppliers
import com.example.medicina.R
import com.example.medicina.model.*
import com.example.medicina.viewmodel.SearchViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicina.components.Confirm
import com.example.medicina.components.CreateButton
import com.example.medicina.components.ViewSupplier
import com.example.medicina.viewmodel.CategoryViewModel
import com.example.medicina.viewmodel.InventoryViewModel
import com.example.medicina.viewmodel.MedicineViewModel
import com.example.medicina.viewmodel.NotificationViewModel
import com.example.medicina.viewmodel.OrderViewModel
import com.example.medicina.viewmodel.RegulationViewModel
import com.example.medicina.viewmodel.SupplierViewModel
import com.example.medicina.viewmodel.AccountViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import com.example.medicina.components.CategoryPillText
import com.example.medicina.components.OrderPillText
import com.example.medicina.components.ReadGeneric
import com.example.medicina.components.Spacing
import com.example.medicina.components.UpsertGenericScreen
import com.example.medicina.viewmodel.BrandedGenericViewModel
import com.example.medicina.viewmodel.GenericViewModel
import com.example.medicina.viewmodel.MedicineCategoryViewModel
import com.example.medicina.viewmodel.ScreenViewModel
import kotlinx.coroutines.delay
import android.graphics.Color as AndroidColor

@Composable
fun ScreenContainer(
    content: @Composable () -> Unit = {},
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        val screenWidth = maxWidth
        val edgeMargin = screenWidth * 0.05f

        SideEffect {
            Global.containerSize = screenWidth - (2 * edgeMargin)
            Global.edgeMargin = edgeMargin
        }

        Box(modifier = Modifier.padding(horizontal = Global.edgeMargin)) {
            content()
        }
    }
}

fun getBaseRoute(route: String?): String? {
    return route?.substringBefore("?")
}

@Composable
fun MainScreen(){
    val context = LocalContext.current

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // viewmodels
    val searchViewModel: SearchViewModel = viewModel()
    val medicineViewModel: MedicineViewModel = viewModel()
    val categoryViewModel: CategoryViewModel = viewModel()
    val regulationViewModel: RegulationViewModel = viewModel()
    val inventoryViewModel: InventoryViewModel = viewModel()
    val orderViewModel: OrderViewModel = viewModel()
    val supplierViewModel: SupplierViewModel = viewModel()
    val notificationViewModel: NotificationViewModel = viewModel()
    val accountViewModel: AccountViewModel = viewModel()
    val genericViewModel: GenericViewModel = viewModel()
    val brandedGenericViewModel: BrandedGenericViewModel = viewModel()
    val medicineCategoryViewModel: MedicineCategoryViewModel = viewModel()

    val (isHome, pageTitle) = when (currentRoute) {
        Screen.Home.route -> true to "Medicina"
        Screen.Search.route -> true to "Search"
        Screen.Inventory.route -> true to "Inventory"
        Screen.Orders.route -> true to "Orders"
        Screen.Notifications.route -> false to "Notification"
        Screen.ViewNotification.route -> false to "Notification"
        else -> false to "Medicina"
    }

    val bottomBarRoutes = listOf(
        "home",
        "search",
        "inventory",
        "orders",
        "suppliers",
        "categories",
        "notifications",
        "medicineCategory",
        "medicine",
        "notification",
        "supplier"
    )

    val baseRoute = getBaseRoute(currentRoute)

    val showBottomBar = bottomBarRoutes.any { it == baseRoute }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
            ) {
                TopNavigation(
                    isHome = isHome,
                    pageTitle = pageTitle,
                    navController
                )
            }
        },
        bottomBar = {
            if (showBottomBar){
                Surface(
                    color = CustomWhite,
                    modifier = Modifier
                        .height(100.dp)
                        .background(CustomWhite)
                        .padding(start = 12.dp, end = 12.dp, bottom = 12.dp, top = 8.dp)
                ) {
                    NavigationBar(navController)
                }
            } else if (currentRoute == Screen.MainMenu.route) {
                Surface(
                    color = CustomWhite,
                    modifier = Modifier
                        .height(100.dp)
                        .background(CustomWhite)
                        .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                ) {
                    UIButton(
                        "Log Out",
                        onClickAction = {
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                            (context as Activity).finish()
                        },
                        isCTA = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    )
                }
            }
        },
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it }, // from right
                    animationSpec = tween(durationMillis = 300)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it }, // to left
                    animationSpec = tween(durationMillis = 300)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it }, // from left
                    animationSpec = tween(durationMillis = 300)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it }, // to right
                    animationSpec = tween(durationMillis = 300)
                )
            },
            modifier = Modifier
                .padding(innerPadding)
                .background(CustomWhite)
        ) {

            // main screens
            composable(Screen.Home.route) {
                Home(navController, searchViewModel)
            }

            composable(
                route = Screen.Search.route,
                arguments = listOf(navArgument("searchItem") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                })
            ) { navBackStackEntry ->
                ScreenContainer {
                    SearchPage(
                        navController,
                        searchViewModel,
                        brandedGenericViewModel,
                        orderViewModel
                    )
                }
            }

            composable(Screen.Inventory.route) {
                ScreenContainer {
                    InventoryPage(
                        navController,
                        inventoryViewModel,
                        genericViewModel,
                        orderViewModel,
                        medicineViewModel,
                        brandedGenericViewModel
                    )
                }
            }

            composable(Screen.Orders.route) {
                ScreenContainer {
                    OrdersPage(
                        navController,
                        orderViewModel,
                        inventoryViewModel,
                        supplierViewModel,
                        medicineViewModel
                    )
                }
            }

            composable(Screen.MainMenu.route) {
                ScreenContainer {
                    MenuScreen(navController)
                }
            }

            // notifications screen
            composable(Screen.Notifications.route) {
                ScreenContainer { NotificationsPage(navController, notificationViewModel) }
            }
            composable(
                route = Screen.ViewNotification.route,
                arguments = listOf(navArgument("notificationID"){
                    type = NavType.IntType
                    defaultValue = -1
                    nullable = false
                })
            ){ backStackEntry ->
                val notificationID = backStackEntry.arguments?.getInt("notificationID") ?: -1

                ScreenContainer { Notification(notificationID, notificationViewModel) }
            }


            // account screens
            composable(
                route = Screen.ViewAccount.route,
                arguments = listOf(navArgument("accountID"){
                    type = NavType.IntType
                    defaultValue = -1
                    nullable = false
                })
            ) { backStackEntry ->
                val accountID = backStackEntry.arguments?.getInt("accountID") ?: -1
                ScreenContainer{
                    AccountScreen(
                        accountID,
                        accountViewModel
                    )
                }
            }
            composable(Screen.Accounts.route) {
                ScreenContainer { ViewAccounts(navController, accountViewModel) }
            }

            // medicine screens
            composable(
                route = Screen.UpsertMedicine.route,
                arguments = listOf(navArgument("medicineID") {
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) { backStackEntry ->
                val medicineID = backStackEntry.arguments?.getInt("medicineID")
                ScreenContainer {
                    UpsertMedicineScreen(
                        medicineID,
                        medicineViewModel,
                        regulationViewModel,
                        categoryViewModel,
                        brandedGenericViewModel,
                        medicineCategoryViewModel,
                        genericViewModel,
                        navController
                    )
                }
            }
            composable(
                route = Screen.ViewMedicine.route,
                arguments = listOf(navArgument("medicineID") {
                    type = NavType.IntType
                    defaultValue = 0
                })
            ) { backStackEntry ->
                val medicineID = backStackEntry.arguments?.getInt("medicineID") ?: 0

                ScreenContainer {
                    ReadMedicine(
                        medicineID,
                        navController,
                        medicineViewModel,
                        genericViewModel,
                        orderViewModel,
                        inventoryViewModel,
                        supplierViewModel,
                        brandedGenericViewModel,
                        medicineCategoryViewModel
                    )
                }
            }
            composable(
                route = Screen.UpsertOrder.route,
                arguments = listOf(navArgument("orderID") {
                    type = NavType.IntType
                    defaultValue = 0
                })
            ) { backStackEntry ->
                val orderID = backStackEntry.arguments?.getInt("orderID")
                ScreenContainer{
                    UpsertOrderScreen(
                        orderID,
                        orderViewModel,
                        inventoryViewModel,
                        supplierViewModel,
                        navController
                    )
                }
            }

            // categories screen
            composable(Screen.Categories.route) {
                ScreenContainer{ CategoriesScreen(navController, categoryViewModel, medicineCategoryViewModel) }
            }
            composable(
                route = Screen.UpsertCategory.route,
                arguments = listOf(navArgument("categoryID") {
                    type = NavType.IntType
                    defaultValue = 0
                })
            ) { backStackEntry ->
                val categoryID = backStackEntry.arguments?.getInt("categoryID") ?: 0

                ScreenContainer{ UpsertCategoryScreen(categoryID, categoryViewModel, navController) }
            }
            composable(
                route = Screen.ViewCategory.route,
                arguments = listOf(navArgument("categoryID") {
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) { backStackEntry ->
                val categoryID = backStackEntry.arguments?.getInt("categoryID") ?: -1

                ScreenContainer {
                    CategoryMedicine(
                        categoryID,
                        navController,
                        categoryViewModel,
                        brandedGenericViewModel,
                        medicineViewModel,
                        orderViewModel,
                        medicineCategoryViewModel
                    )
                }
            }

            // suppliers screen
            composable(Screen.Suppliers.route) {
                ScreenContainer { ViewSuppliers(navController, supplierViewModel, orderViewModel) }
            }
            composable(
                route = Screen.UpsertSupplier.route,
                arguments = listOf(navArgument("supplierID") {
                    type = NavType.IntType
                    defaultValue = 0
                })
            ) { backStackEntry ->
                val supplierID = backStackEntry.arguments?.getInt("supplierID") ?: -1

                ScreenContainer{ UpsertSuppliersScreen(supplierID, supplierViewModel) }
            }
            composable(
                route = Screen.ViewSupplier.route,
                arguments = listOf(navArgument("supplierID") {
                    type = NavType.IntType
                    defaultValue = 0
                })
            ) { backStackEntry ->
                val supplierID = backStackEntry.arguments?.getInt("supplierID") ?: -1

                ScreenContainer{ ViewSupplier(supplierID, navController, orderViewModel, inventoryViewModel, supplierViewModel) }
            }

            composable(Screen.Generics.route){
                ScreenContainer{
                    GenericsScreen(navController, genericViewModel, brandedGenericViewModel)
                }
            }

            composable(
                route = Screen.UpsertGeneric.route,
                arguments = listOf(navArgument("genericID") {
                    type = NavType.IntType
                    defaultValue = 0
                })
            ) { backStackEntry ->
                val genericID = backStackEntry.arguments?.getInt("genericID") ?: -1

                ScreenContainer {
                    UpsertGenericScreen(
                        genericID,
                        genericViewModel,
                        navController
                    )
                }
            }

            composable(
                route = Screen.ViewGeneric.route,
                arguments = listOf(navArgument("genericID") {
                    type = NavType.IntType
                    defaultValue = 0
                })
            ) { backStackEntry ->
                val genericID = backStackEntry.arguments?.getInt("genericID") ?: -1

                ScreenContainer{
                    ReadGeneric(
                        genericID,
                        navController,
                        categoryViewModel,
                        brandedGenericViewModel,
                        medicineViewModel,
                        orderViewModel,
                        medicineCategoryViewModel,
                        genericViewModel)
                }
            }
        }
    }
}

@Composable
fun Home(
    navController: NavController,
    searchViewModel: SearchViewModel
) {
    val context = LocalContext.current
    val searchItem by searchViewModel.searchItem.collectAsState()

    var notificationsList by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var categoriesList by remember { mutableStateOf<List<Category>>(emptyList()) }

    LaunchedEffect(Unit) {
        notificationsList = TestData.NotificationRepository
        categoriesList = TestData.CategoryRepository
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().imePadding()
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CustomGreen)
                    .padding(bottom = 16.dp)
            ) {
                ScreenContainer{
                    ConstraintLayout {
                        val guidelines = setupColumnGuidelines()
                        val (searchField, searchButton) = createRefs()

                        InputField(
                            inputHint = "Looking for a medicine?",
                            inputValue = searchItem,
                            onValueChange = { newValue -> searchViewModel.updateSearchItem(newValue) },
                            modifier = Modifier.constrainAs(searchField) {
                                top.linkTo(parent.top, margin = -16.dp)
                                start.linkTo(parent.start)
                            }
                        )

                        UIButton(
                            "Search",
                            onClickAction = {
                                Toast.makeText(context, "Searching for $searchItem", Toast.LENGTH_SHORT).show()
                                searchViewModel.updateSearchItem(searchItem)
                                searchViewModel.performSearch()
                                navController.navigate(Screen.Search.createRoute(searchItem)){
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            modifier = Modifier
                                .constrainAs(searchButton) {
                                    top.linkTo(searchField.bottom, margin = 8.dp)
                                    start.linkTo(guidelines.c2start)
                                    end.linkTo(guidelines.c3end)
                                    width = Dimension.fillToConstraints
                                },
                            isCTA = false
                        )
                    }
                }
            }
        }
        item {
            ScreenContainer {
                ConstraintLayout(
                    modifier = Modifier.fillMaxWidth()
                ){
                    val guidelines = setupColumnGuidelines()
                    val (categoryText, seeAllText) = createRefs()

                    Text(
                        "Categories",
                        modifier = Modifier.constrainAs(categoryText){
                            top.linkTo(parent.top, margin = 16.dp)
                            start.linkTo(guidelines.c1start)
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CustomBlack
                    )

                    Text(
                        "See all",
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .constrainAs(seeAllText) {
                                top.linkTo(categoryText.top)
                                bottom.linkTo(categoryText.bottom)
                                end.linkTo(guidelines.c4end)
                            }
                            .clickable {
                                navController.navigate(Screen.Categories.route){
                                    popUpTo(Screen.Home.route) {
                                        inclusive = false
                                        saveState = true
                                    }
                                    launchSingleTop = false
                                    restoreState = true
                                }
                            },
                        color = CustomBlack
                    )
                }
            }
        }
        item {
            ScreenContainer{
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoriesList.take(4).forEach { category ->
                        ButtonBox(
                            text = category.categoryName,
                            onClickAction = {
                                val id = category.id
                                id?.let{
                                    navController.navigate(Screen.ViewCategory.createRoute(category.id)) {
                                        popUpTo(Screen.Home.route) {
                                            inclusive = false
                                            saveState = true
                                        }
                                        launchSingleTop = false
                                        restoreState = true
                                    }
                                }
                            },
                            inheritedModifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    }
                }
            }
        }
        item {
            ScreenContainer {
                ConstraintLayout(
                    modifier = Modifier.fillMaxWidth()
                ){
                    val guidelines = setupColumnGuidelines()
                    val (updatesText, seeAllText) = createRefs()

                    Text(
                        "Recent Updates",
                        modifier = Modifier.constrainAs(updatesText){
                            top.linkTo(parent.top, margin = 8.dp)
                            start.linkTo(guidelines.c1start)
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CustomBlack
                    )

                    Text(
                        "See all",
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .constrainAs(seeAllText) {
                                top.linkTo(updatesText.top)
                                bottom.linkTo(updatesText.bottom)
                                end.linkTo(guidelines.c4end)
                            }
                            .clickable {
                                navController.navigate(Screen.Notifications.route){
                                    popUpTo(Screen.Home.route) {
                                        inclusive = false
                                        saveState = true
                                    }
                                    launchSingleTop = false
                                    restoreState = true
                                }
                            },
                        color = CustomBlack
                    )
                }
            }
        }
        if (notificationsList.isNotEmpty()) {
            item {
                ScreenContainer {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        notificationsList.forEach { notification ->
                            InfoPills(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                infoColor = CustomRed,
                                content = {
                                    NotificationPillText(
                                        title = notification.notificationBanner,
                                        subtitle = notification.notificationMessage,
                                        details = notification.notificationOverview
                                    )
                                },
                                onClickAction = {
                                    navController.navigate(Screen.ViewNotification.createRoute(notification.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchPage(
    navController: NavController,
    searchViewModel: SearchViewModel,
    brandedGenericViewModel: BrandedGenericViewModel,
    orderViewModel: OrderViewModel
) {
    val context = LocalContext.current
    val searchItem by searchViewModel.searchItem.collectAsState()
    val searchResults by searchViewModel.searchResults.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val guidelines = setupColumnGuidelines()

                val (searchField, searchButton) = createRefs()

                InputField(
                    inputHint = "Looking for a medicine?",
                    inputValue = searchItem,
                    onValueChange = { newValue -> searchViewModel.updateSearchItem(newValue) },
                    modifier = Modifier.constrainAs(searchField) {
                        top.linkTo(parent.top, margin = -16.dp)
                        start.linkTo(parent.start)
                    }
                )

                UIButton(
                    "Search",
                    modifier = Modifier
                        .constrainAs(searchButton) {
                            top.linkTo(searchField.bottom, margin = 8.dp)
                            start.linkTo(guidelines.c2start)
                            end.linkTo(guidelines.c3end)
                            width = Dimension.fillToConstraints
                        },
                    onClickAction = {
                        Toast.makeText(context, "Searching for $searchItem", Toast.LENGTH_SHORT).show()
                        searchViewModel.performSearch()
                    },
                    isCTA = true
                )
            }
        }
        if (searchResults.isNotEmpty()) {
            items(searchResults) { medicine ->
                InfoPills(
                    modifier = Modifier.fillMaxWidth(),
                    infoColor = CustomRed,
                    content = {
                        InventoryPillText(
                            brandName = medicine.brandName,
                            genericName = brandedGenericViewModel.getGenericNamesText(medicine.id!!),
                            quantity = orderViewModel.getTotalQuantity(medicine.id!!).toString(),
                            price = String.format(Locale.US, "%.2f", medicine.price)
                        )
                    },
                    onClickAction = {
                        navController.navigate(Screen.ViewMedicine.createRoute(medicine.id!!))
                    }
                )
            }
        }
    }
}

@Composable
fun MenuScreen(navController: NavController){
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
    ){
        ConstraintLayout(modifier = Modifier.fillMaxSize()){
            val guidelines = setupColumnGuidelines()

            val (menu1, menu2, menu3, menu4, menu5, menu6, menu7) = createRefs()

            ButtonBox(
                iconId = R.drawable.account_m,
                iconSize = 120.dp,
                fontSize = 14.sp,
                text = "Account",
                onClickAction = {
                    navController.navigate(Screen.ViewAccount.createRoute(UserSession.accountID)) {
                        launchSingleTop = true
                        popUpTo(Screen.MainMenu.route) {
                            inclusive = true
                        }
                    }
                },
                inheritedModifier = Modifier.constrainAs(menu1) {
                    top.linkTo(parent.top, margin = 32.dp)
                    start.linkTo(guidelines.c1start)
                    end.linkTo(guidelines.c2end)
                    width = Dimension.fillToConstraints
                    height = Dimension.ratio("1:1")
                }
            )

            ButtonBox(
                iconId = R.drawable.category_m,
                iconSize = 120.dp,
                fontSize = 14.sp,
                text = "Categories",
                onClickAction = {
                    navController.navigate(Screen.Categories.route) {
                        launchSingleTop = true
                        popUpTo(Screen.MainMenu.route) {
                            inclusive = true
                        }
                    }
                },
                inheritedModifier = Modifier.constrainAs(menu2) {
                    top.linkTo(parent.top, margin = 32.dp)
                    start.linkTo(guidelines.c3start)
                    end.linkTo(guidelines.c4end)
                    width = Dimension.fillToConstraints
                    height = Dimension.ratio("1:1")
                }
            )

            ButtonBox(
                iconId = R.drawable.inventory_m,
                iconSize = 120.dp,
                fontSize = 14.sp,
                text = "Inventory",
                onClickAction = {
                    navController.navigate(Screen.Inventory.route) {
                        launchSingleTop = true
                        popUpTo(Screen.MainMenu.route) {
                            inclusive = true
                        }
                    }
                },
                inheritedModifier = Modifier.constrainAs(menu3) {
                    top.linkTo(menu1.bottom, margin = 16.dp)
                    start.linkTo(guidelines.c1start)
                    end.linkTo(guidelines.c2end)
                    width = Dimension.fillToConstraints
                    height = Dimension.ratio("1:1")
                }
            )

            ButtonBox(
                iconId = R.drawable.message,
                iconSize = 120.dp,
                fontSize = 14.sp,
                text = "Notifications",
                onClickAction = {
                    navController.navigate(Screen.Notifications.route) {
                        launchSingleTop = true
                        popUpTo(Screen.MainMenu.route) {
                            inclusive = true
                        }
                    }
                },
                inheritedModifier = Modifier.constrainAs(menu4) {
                    top.linkTo(menu2.bottom, margin = 16.dp)
                    start.linkTo(guidelines.c3start)
                    end.linkTo(guidelines.c4end)
                    width = Dimension.fillToConstraints
                    height = Dimension.ratio("1:1")
                }
            )

            ButtonBox(
                iconId = R.drawable.category_m,
                iconSize = 120.dp,
                fontSize = 14.sp,
                text = "Generics",
                onClickAction = {
                    navController.navigate(Screen.Generics.route) {
                        launchSingleTop = true
                        popUpTo(Screen.MainMenu.route) {
                            inclusive = true
                        }
                    }
                },
                inheritedModifier = Modifier.constrainAs(menu5) {
                    top.linkTo(menu3.bottom, margin = 16.dp)
                    start.linkTo(guidelines.c1start)
                    end.linkTo(guidelines.c2end)
                    width = Dimension.fillToConstraints
                    height = Dimension.ratio("1:1")
                }
            )

            if(UserSession.designationID != 2){
                ButtonBox(
                    iconId = R.drawable.suppliers_m,
                    iconSize = 120.dp,
                    fontSize = 14.sp,
                    text = "Suppliers",
                    onClickAction = {
                        navController.navigate(Screen.Suppliers.route) {
                            launchSingleTop = true
                            popUpTo(Screen.MainMenu.route) {
                                inclusive = true
                            }
                        }
                    },
                    inheritedModifier = Modifier.constrainAs(menu6) {
                        top.linkTo(menu3.bottom, margin = 16.dp)
                        start.linkTo(guidelines.c1start)
                        end.linkTo(guidelines.c2end)
                        width = Dimension.fillToConstraints
                        height = Dimension.ratio("1:1")
                    }
                )

                ButtonBox(
                    iconId = R.drawable.users_m,
                    iconSize = 120.dp,
                    fontSize = 14.sp,
                    text = "Users",
                    onClickAction = {
                        navController.navigate(Screen.Accounts.route) {
                            launchSingleTop = true
                            popUpTo(Screen.MainMenu.route) {
                                inclusive = true
                            }
                        }
                    },
                    inheritedModifier = Modifier.constrainAs(menu7) {
                        top.linkTo(menu4.bottom, margin = 16.dp)
                        start.linkTo(guidelines.c3start)
                        end.linkTo(guidelines.c4end)
                        width = Dimension.fillToConstraints
                        height = Dimension.ratio("1:1")
                    }
                )
            }
        }
    }
}

@Composable
fun NotificationsPage(
    navController: NavController,
    notificationViewModel: NotificationViewModel
){
    val notifications by notificationViewModel.notifications.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item{
            Spacing(Global.edgeMargin)
        }
        if (notifications.isNotEmpty()) {
            items(notifications) { notification ->
                InfoPills(
                    modifier = Modifier
                        .fillMaxWidth(),
                    infoColor = CustomRed,
                    content = {
                        NotificationPillText(
                            title = notification.notificationBanner,
                            subtitle = notification.notificationMessage,
                            details = notification.notificationOverview
                        )
                    },
                    onClickAction = {
                        navController.navigate(Screen.ViewNotification.createRoute(notification.id))
                    }
                )
            }
        }
    }
}

@Composable
fun Notification(
    notificationId: Int,
    notificationViewModel: NotificationViewModel
){
    val notificationData by notificationViewModel.notificationData.collectAsState()

    LaunchedEffect(notificationId) {
        if(notificationId != -1){
            notificationViewModel.getNotificationById(notificationId)
        }
    }

    LazyColumn (
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item{
            Spacing(Global.edgeMargin)
            Text(
                notificationData.notificationBanner,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CustomBlack
            )
        }
        item{
            Text(
                notificationData.date.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                color = CustomBlack
            )
            Spacing(8.dp)
            HorizontalDivider()
        }

        item{
            Text(
                notificationData.notificationMessage,
                color = CustomBlack
            )
        }
    }

}

@Composable
fun InventoryPage(
    navController: NavController,
    viewModel: InventoryViewModel,
    genericViewModel: GenericViewModel,
    orderViewModel: OrderViewModel,
    medicineViewModel: MedicineViewModel,
    brandedGenericViewModel: BrandedGenericViewModel
) {

    val medicines by viewModel.medicines.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            CreateButton(
                "Add New Medicine",
                inheritedModifier = Modifier.fillMaxWidth(),
                onclick = {
                    navController.navigate(Screen.UpsertMedicine.createRoute(-1))
                }
            )
        }

        if (medicines.isNotEmpty()) {
            items(medicines) { medicine ->
                InfoPills(
                    modifier = Modifier.fillMaxWidth(),
                    infoColor = CustomGreen, // Color(AndroidColor.parseColor(medicineViewModel.getMedicineColor(medicine.categoryId))),
                    content = {
                        InventoryPillText(
                            brandName = medicine.brandName,
                            genericName = brandedGenericViewModel.getGenericNamesText(medicine.id),
                            quantity = orderViewModel.getTotalQuantity(medicine.id!!).toString(),
                            price = String.format(Locale.US, "%.2f", medicine.price)
                        )
                    },
                    onClickAction = {
                        navController.navigate(Screen.ViewMedicine.createRoute(medicine.id!!))
                    }
                )
            }
        }
    }
}

@Composable
fun OrdersPage(
    navController: NavController,
    orderViewModel: OrderViewModel,
    inventoryViewModel: InventoryViewModel,
    supplierViewModel: SupplierViewModel,
    medicineViewModel: MedicineViewModel
){
    val orders by orderViewModel.orders.collectAsState()
    val medicines by inventoryViewModel.medicineMap.collectAsState()
    val suppliers by supplierViewModel.supplierMap.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item{
            CreateButton(
                "Add New Order",
                inheritedModifier = Modifier.fillMaxWidth(),
                onclick = {
                    navController.navigate(Screen.UpsertOrder.createRoute(-1))
                }
            )
        }

        if(orders.isNotEmpty()) {
            items(orders) { order ->
                val orderedItem = medicines[order.medicineId]?.copy()
                val supplier = suppliers[order.supplierId]?.copy()
                val orderDate = order.orderDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                if (orderedItem != null) {
                    InfoPills(
                        modifier = Modifier.fillMaxWidth(),
                        infoColor = Color(AndroidColor.parseColor(medicineViewModel.getMedicineColor(orderedItem.id!!))),
                        content = {
                            OrderPillText(
                                orderedItem = orderedItem?.brandName ?: "",
                                supplier = supplier?.name ?: "",
                                date = orderDate,
                                quantity = order.quantity,
                                price = String.format(Locale.US, "%.2f", order.price)
                            )
                        },
                        onClickAction = {
                            navController.navigate(Screen.UpsertOrder.createRoute(order.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GenericsScreen(
    navController: NavController,
    genericViewModel: GenericViewModel,
    brandedGenericViewModel: BrandedGenericViewModel
){
    val generics by genericViewModel.generics.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        item{
            CreateButton(
                "Add New Generics",
                inheritedModifier = Modifier.fillMaxWidth(),
                onclick = {
                    navController.navigate(Screen.UpsertGeneric.createRoute(-1)) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        if(generics.isNotEmpty()){
            items(generics){ generic ->
                InfoPills(
                    infoColor = Color(android.graphics.Color.parseColor("#123456")),
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        CategoryPillText(
                            categoryName = generic.genericName,
                            medicineNumber = brandedGenericViewModel.getGenericSize(generic.id ?: -1)
                        )
                    },
                    onClickAction = {
                        navController.navigate(Screen.ViewGeneric.createRoute(generic.id ?: -1))
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    ComposePracticeTheme {
        MainScreen()
    }
}

