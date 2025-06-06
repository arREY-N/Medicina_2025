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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.medicina.functions.MedicinaException
import com.example.medicina.model.UserSession
import com.example.medicina.ui.theme.CustomBlack
import com.example.medicina.ui.theme.CustomRed
import com.example.medicina.viewmodel.BrandedGenericViewModel
import com.example.medicina.viewmodel.CategoryViewModel
import com.example.medicina.viewmodel.GenericViewModel
import com.example.medicina.viewmodel.MedicineCategoryViewModel
import com.example.medicina.viewmodel.MedicineViewModel
import com.example.medicina.viewmodel.NotificationViewModel
import com.example.medicina.viewmodel.OrderViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun UpsertGenericScreen(
    genericID: Int? = null,
    genericViewModel: GenericViewModel,
    notificationViewModel: NotificationViewModel,
    navController: NavController,
){
    var isEditing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val upsertGeneric by genericViewModel.upsertGeneric.collectAsState()

    LaunchedEffect (genericID) {
        if(genericID == -1){
            genericViewModel.reset()
            isEditing = false
        } else {
            genericID?.let { genericViewModel.getGenericById(genericID) }
            isEditing = true
        }
    }

    LazyColumn (
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item{
            PageHeader(
                title = if (isEditing) "Edit Generic" else "Add Generic",
                subtitle = "Ensure correct spelling of generic names"
            )
        }

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
                        try{
                            genericViewModel.validateScreen()
                            val id = genericViewModel.save()
                            genericViewModel.getGenericById(id)
                            val savedGeneric = genericViewModel.upsertGeneric
                            Toast.makeText(context, "Generic saved: ${savedGeneric.value.genericName}", Toast.LENGTH_SHORT).show()

                            if(isEditing){
                                notificationViewModel.addNotification(
                                    banner = "A generic name entry was edited!",
                                    message = "${upsertGeneric.genericName} was edited!",
                                    overview = "Generic edited",
                                    action = "EDITED",
                                    source = UserSession.username,
                                )
                                navController.popBackStack()
                            } else {
                                notificationViewModel.addNotification(
                                    banner = "A generic name entry was added!",
                                    message = "${upsertGeneric.genericName} was added!",
                                    overview = "Generic added",
                                    action = "ADDED",
                                    source = UserSession.username,
                                )
                                navController.navigate(Screen.ViewGeneric.createRoute(id)){
                                    popUpTo(Screen.Generics.route) {
                                        inclusive = true
                                    }
                                }
                            }
                            genericViewModel.reset()
                        } catch (e: MedicinaException){
                            Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                cancelOnclick = {
                    coroutineScope.launch{
                        if(isEditing){
                            notificationViewModel.addNotification(
                                banner = "A generic name entry was deleted!",
                                message = "${upsertGeneric.genericName} was deleted!",
                                overview = "Generic deleted",
                                action = "DELETED",
                                source = UserSession.username,
                            )
                            navController.navigate(Screen.Generics.route){
                                popUpTo(Screen.Generics.route) {
                                    inclusive = true
                                }
                            }
                            genericViewModel.delete()
                        } else {
                            navController.popBackStack()
                        }
                        genericViewModel.reset()
                    }
                }
            )
            Spacing(Global.edgeMargin)
        }

        item{
            Spacing(Global.edgeMargin)
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
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item{
            PageHeader(
                title = generic.genericName
            )
        }

        item{
            EditButton(
                onEdit = {
                    navController.navigate(Screen.UpsertGeneric.createRoute(genericId))
                }
            )
        }

        if (genericMedicine.isNotEmpty()) {
            items(genericMedicine) { medicine ->
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
        item{
            Spacing(Global.edgeMargin)
        }
    }
}