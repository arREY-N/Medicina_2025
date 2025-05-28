package com.example.medicina.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.medicina.functions.MedicinaException
import com.example.medicina.model.UserSession
import com.example.medicina.ui.theme.CustomBlack
import com.example.medicina.ui.theme.CustomGray
import com.example.medicina.ui.theme.CustomRed
import com.example.medicina.viewmodel.BrandedGenericViewModel
import com.example.medicina.viewmodel.CategoryViewModel
import com.example.medicina.viewmodel.GenericViewModel
import com.example.medicina.viewmodel.MedicineCategoryViewModel
import com.example.medicina.viewmodel.MedicineViewModel
import com.example.medicina.viewmodel.NotificationViewModel
import com.example.medicina.viewmodel.OrderViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale

@Composable
fun UpsertCategoryScreen(
    categoryID: Int? = null,
    categoryViewModel: CategoryViewModel,
    notificationViewModel: NotificationViewModel,
    navController: NavHostController
){
    var isEditing by remember { mutableStateOf(false) }
    val isPermitted = UserSession.designationID != 3
    val coroutineScope = rememberCoroutineScope()

    val upsertCategory by categoryViewModel.upsertCategory.collectAsState()

    LaunchedEffect (categoryID) {
        if(categoryID == -1){
            categoryViewModel.reset()
            isEditing = false
        } else {
            categoryID?.let { categoryViewModel.getCategoryById(categoryID) }
            isEditing = true
        }
    }

    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(top = Global.edgeMargin),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item{
            PageHeader(
                title = if (isEditing) "Edit Category" else "Add Category",
                subtitle = "Enter color code in hexadecimal format (#000000, #FFFFFF)"
            )
        }

        item{
            InputField(
                inputName = "Category",
                inputHint = "Enter category",
                inputValue = upsertCategory.categoryName,
                onValueChange = { newValue -> categoryViewModel.updateData { it.copy(categoryName = newValue) } },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item{
            InputField(
                inputName = "Category Color",
                inputHint = "#000000",
                inputValue = if (upsertCategory.hexColor.equals("#9E9E9E")) "" else upsertCategory.hexColor,
                onValueChange = { newValue -> categoryViewModel.updateData { it.copy(hexColor = newValue) } },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item{
            InputField(
                inputName = "Description",
                inputHint = "Enter description",
                inputValue = upsertCategory.description,
                onValueChange = { newValue -> categoryViewModel.updateData { it.copy(description = newValue) } },
                isMultiline = true,
                contentAlignment = Alignment.TopStart,
                verticalArrangement = Alignment.Top,
                textPadding = PaddingValues(6.dp),
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth()
            )
        }

        item{
            val context = LocalContext.current
            Confirm(
                action = "Confirm Category",
                confirmOnclick = {
                    coroutineScope.launch {
                        try{
                            categoryViewModel.validateScreen()
                            val id = categoryViewModel.save()
                            categoryViewModel.getCategoryById(id)
                            val savedCategory = categoryViewModel.upsertCategory
                            Toast.makeText(context, "Category saved: ${savedCategory.value.categoryName}", Toast.LENGTH_SHORT).show()

                            if(isEditing){
                                notificationViewModel.addNotification(
                                    banner = "A medicine category was edited!",
                                    message = "${upsertCategory.categoryName} was edited!",
                                    overview = "Category edited",
                                    action = "EDITED",
                                    source = UserSession.username,
                                )
                                navController.popBackStack()
                            } else {
                                notificationViewModel.addNotification(
                                    banner = "A new medicine category was added!",
                                    message = "${upsertCategory.categoryName} was added!",
                                    overview = "Category Added",
                                    action = "ADDED",
                                    source = UserSession.username,
                                )
                                navController.navigateMenuTab(Screen.Categories.route)
                            }
                            categoryViewModel.reset()
                        } catch (e: MedicinaException){
                            Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                cancelOnclick = {
                    coroutineScope.launch{
                        if (isEditing){
                            if(!isPermitted){
                                Toast.makeText(context, "Must be an admin to delete categories", Toast.LENGTH_SHORT).show()
                            } else {
                                notificationViewModel.addNotification(
                                    banner = "A new medicine category was added!",
                                    message = "${upsertCategory.categoryName} was deleted!",
                                    overview = "Category Deleted",
                                    action = "DELETED",
                                    source = UserSession.username,
                                )
                                navController.navigateMenuTab(Screen.Categories.route)
                                categoryViewModel.delete()
                                categoryViewModel.reset()
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                }
            )
            Spacing(80.dp)
        }
    }
}

@Composable
fun CategoriesScreen(
    navController: NavController,
    categoryViewModel: CategoryViewModel,
    medicineCategoryViewModel: MedicineCategoryViewModel
){
    val categories by categoryViewModel.categories.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        item{
            PageHeader(
                title = "Categories"
            )
        }
        if(UserSession.designationID != 3){
            item{
                CreateButton(
                    "Add New Category",
                    inheritedModifier = Modifier.fillMaxWidth(),
                    onclick = {
                        navController.navigate(Screen.UpsertCategory.createRoute(-1)) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        if(categories.isNotEmpty()){
            items(categories){ category ->
                InfoPills(
                    infoColor = listOf(Color(android.graphics.Color.parseColor(category.hexColor))),
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        CategoryPillText(
                            categoryName = category.categoryName,
                            medicineNumber = medicineCategoryViewModel.getCategorySize(category.id ?: -1),
                            description = category.description
                        )
                    },
                    onClickAction = {
                        navController.navigate(Screen.ViewCategory.createRoute(category.id ?: -1))
                    }
                )
            }
        } else {
            item{
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacing(80.dp)
                    Text(
                        text = "No Categories Available",
                        color = CustomGray,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        item{
            Spacing(Global.edgeMargin)
        }
    }
}

@Composable
fun CategoryMedicine(
    categoryId: Int,
    navController: NavController,
    categoryViewModel: CategoryViewModel,
    brandedGenericViewModel: BrandedGenericViewModel,
    medicineViewModel: MedicineViewModel,
    orderViewModel: OrderViewModel,
    medicineCategoryViewModel: MedicineCategoryViewModel
){
    val categoryMedicine by medicineCategoryViewModel.medicineNames.collectAsState()
    val category by categoryViewModel.categoryData.collectAsState()

    LaunchedEffect(categoryId) {
        categoryViewModel.getCategoryById(categoryId)
        medicineCategoryViewModel.getMedicinesByCategory(categoryId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = Global.edgeMargin),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item{
            PageHeader(
                title = category.categoryName
            )
        }

        val isPermitted = UserSession.designationID != 3

        if(isPermitted){
            item{
                EditButton(
                    onEdit = {
                        navController.navigate(Screen.UpsertCategory.createRoute(categoryId))
                    }
                )
            }
        }

        if (categoryMedicine.isNotEmpty()) {
            items(categoryMedicine) { medicine ->

                val quantity by medicine.id?.let {
                    orderViewModel.getTotalQuantity(it).collectAsState()
                } ?: remember { mutableStateOf(0) }

                val categories by medicine.id?.let {
                    medicineCategoryViewModel.getCategoriesById(it).collectAsState(initial = emptyList())
                } ?: remember { mutableStateOf(emptyList()) }

                val infoColor = categories.map { Color(it.hexColor.toColorInt()) }

                InfoPills(
                    modifier = Modifier.fillMaxWidth(),
                    infoColor = infoColor,
                    content = {
                        InventoryPillText(
                            brandName = medicine.brandName,
                            genericName = brandedGenericViewModel.getGenericNamesText(medicine.id),
                            quantity = quantity.toString(),
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