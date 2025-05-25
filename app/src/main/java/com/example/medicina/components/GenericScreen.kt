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
fun UpsertGenericScreen(
    genericID: Int? = null,
    genericViewModel: GenericViewModel,
    navController: NavController
){
    val coroutineScope = rememberCoroutineScope()

    val upsertGeneric by genericViewModel.upsertGeneric.collectAsState()

    LaunchedEffect (genericID) {
        if(genericID == -1){
            genericViewModel.reset()
        } else {
            genericID?.let { genericViewModel.getGenericById(genericID) }
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
                inputName = "Generic Name",
                inputHint = "Enter generic name",
                inputValue = upsertGeneric.genericName,
                onValueChange = { newValue -> genericViewModel.updateData { it.copy(genericName = newValue) } },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item{
            val context = LocalContext.current
            Confirm(
                action = "Confirm Generic",
                confirmOnclick = {
                    coroutineScope.launch {
                        val id = genericViewModel.save()
                        genericViewModel.getGenericById(id)
                        val savedGeneric = genericViewModel.upsertGeneric
                        Toast.makeText(context, "Generic saved: ${savedGeneric.value.genericName}", Toast.LENGTH_SHORT).show()
                        genericViewModel.reset()
                        navController.popBackStack()
                    }
                },
                cancelOnclick = {
                    println("DELETE CLICKED")
                    coroutineScope.launch{
                        println("DLETE ONGOING!")
                        genericViewModel.delete()
                        genericViewModel.reset()
                        navController.popBackStack()
                    }
                }
            )
            Spacing(80.dp)
        }
    }
}

@Composable
fun ReadGeneric(
    genericId: Int,
    navController: NavController,
    categoryViewModel: CategoryViewModel,
    brandedGenericViewModel: BrandedGenericViewModel,
    medicineViewModel: MedicineViewModel,
    orderViewModel: OrderViewModel,
    medicineCategoryViewModel: MedicineCategoryViewModel,
    genericViewModel: GenericViewModel
){
    val genericMedicine by brandedGenericViewModel.medicineNames.collectAsState()
    val generic by genericViewModel.genericData.collectAsState()

    LaunchedEffect(genericId) {
        genericViewModel.getGenericById(genericId)
        brandedGenericViewModel.getMedicinesByGeneric(genericId)
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
                    text = generic.genericName,
                    color = CustomBlack,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacing(12.dp)
        }
        if (genericMedicine.isNotEmpty()) {
            items(genericMedicine) { medicine ->
                InfoPills(
                    modifier = Modifier.fillMaxWidth(),
                    infoColor = Color(android.graphics.Color.parseColor("#123456")),
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