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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.medicina.ui.theme.CustomBlack
import com.example.medicina.ui.theme.CustomRed
import com.example.medicina.viewmodel.BrandedGenericViewModel
import com.example.medicina.viewmodel.CategoryViewModel
import com.example.medicina.viewmodel.GenericViewModel
import com.example.medicina.viewmodel.MedicineCategoryViewModel
import com.example.medicina.viewmodel.MedicineViewModel
import com.example.medicina.viewmodel.OrderViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun UpsertCategoryScreen(
    categoryID: Int? = null,
    categoryViewModel: CategoryViewModel,
    navController: NavController
){
    val coroutineScope = rememberCoroutineScope()

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
                        val id = categoryViewModel.save()
                        categoryViewModel.getCategoryById(id)
                        val savedCategory = categoryViewModel.upsertCategory
                        Toast.makeText(context, "Medicine saved: ${savedCategory.value.categoryName}", Toast.LENGTH_SHORT).show()
                        categoryViewModel.reset()
                        navController.popBackStack()
                    }
                },
                cancelOnclick = {
                    println("DELETE CLICKED")
                    coroutineScope.launch{
                        println("DLETE ONGOING!")
                        categoryViewModel.delete()
                        categoryViewModel.reset()
                        navController.popBackStack()
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
                    infoColor = Color(android.graphics.Color.parseColor(category.hexColor)),
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
        modifier = Modifier.fillMaxSize().padding(vertical = Global.edgeMargin),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item{
            Spacing(4.dp)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = category.categoryName,
                    color = CustomBlack,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacing(12.dp)
        }
        if (categoryMedicine.isNotEmpty()) {
            items(categoryMedicine) { medicine ->
                InfoPills(
                    modifier = Modifier.fillMaxWidth(),
                    infoColor = Color(android.graphics.Color.parseColor(medicineViewModel.getMedicineColor(categoryId))),
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