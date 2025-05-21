package com.example.medicina.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medicina.ui.theme.CustomRed
import com.example.medicina.viewmodel.CategoryViewModel
import java.util.Locale

@Composable
fun UpsertCategoryScreen(
    categoryID: Int? = null,
    categoryViewModel: CategoryViewModel
){
    val upsertCategory by categoryViewModel.upsertCategory.collectAsState()

    LaunchedEffect (categoryID) {
        if(categoryID == -1){
            categoryViewModel.reset()
        } else {
            categoryID?.let { categoryViewModel.getCategoryById(categoryID) }
        }
    }

    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
    }

}

@Composable
fun CategoriesScreen(
    navController: NavController,
    categoryViewModel: CategoryViewModel
){
    val categories by categoryViewModel.categories.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        item{
            CreateButton(
                "Add New Category",
                inheritedModifier = Modifier.fillMaxWidth(),
                onclick = {
                    navController.navigate(Screen.UpsertCategory.createRoute(-1)) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        if(categories.isNotEmpty()){
            items(categories){ category ->
                InfoPills(
                    infoColor = CustomRed,
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        CategoryPillText(
                            categoryName = category.categoryName,
                            medicineNumber = categoryViewModel.getMedicineNumber(category.id)
                        )
                    },
                    onClickAction = {
                        navController.navigate(Screen.ViewCategory.createRoute(category.id))
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryMedicine(
    categoryId: Int,
    navController: NavController,
    categoryViewModel: CategoryViewModel
){
    val categoryMedicine by categoryViewModel.categoryMedicines.collectAsState()

    LaunchedEffect(categoryId) {
        categoryViewModel.getMedicineInCategory(categoryId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (categoryMedicine.isNotEmpty()) {
            items(categoryMedicine) { medicine ->
                InfoPills(
                    modifier = Modifier.fillMaxWidth(),
                    infoColor = CustomRed,
                    content = {
                        InventoryPillText(
                            brandName = medicine.brandName,
                            genericName = medicine.genericName,
                            quantity = medicine.quantity.toString(),
                            price = String.format(Locale.US, "%.2f", medicine.price)
                        )
                    },
                    onClickAction = {
                        navController.navigate(Screen.ViewMedicine.createRoute(medicine.id))
                    }
                )
            }
        }
    }
}