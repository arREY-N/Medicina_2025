package com.example.medicina.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.example.medicina.functions.MedicinaException
import com.example.medicina.viewmodel.InventoryViewModel
import com.example.medicina.viewmodel.OrderViewModel
import com.example.medicina.viewmodel.SupplierViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun UpsertOrderScreen(
    orderID: Int? = null,
    orderViewModel: OrderViewModel,
    inventoryViewModel: InventoryViewModel,
    supplierViewModel: SupplierViewModel,
    navController: NavController
){
    var isEditing by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val medicineMap by inventoryViewModel.medicineMap.collectAsState()
    val medicines by inventoryViewModel.medicines.collectAsState()
    val medicineNames = medicines.map { it.brandName }

    val supplierMap by supplierViewModel.supplierMap.collectAsState()
    val suppliers by supplierViewModel.suppliers.collectAsState()
    val supplierNames = suppliers.map { it.name }

    val upsertOrder by orderViewModel.upsertOrder.collectAsState()
    val upsertPrice by orderViewModel.upsertPrice.collectAsState()

    var selectedMedicine by remember { mutableStateOf("") }
    var selectedSupplier by remember { mutableStateOf("") }

    LaunchedEffect(orderID) {
        if (orderID == -1) {
            orderViewModel.reset()
            isEditing = false
        } else {
            isEditing = true
            orderID?.let{
                orderViewModel.getOrderById(orderID)
            }
        }
    }

    LaunchedEffect(upsertOrder, medicineMap, supplierMap) {
        if (isEditing && upsertOrder.id != null && medicineMap.isNotEmpty() && supplierMap.isNotEmpty()) {
            selectedMedicine = medicineMap[upsertOrder.medicineId]?.brandName ?: ""
            selectedSupplier = supplierMap[upsertOrder.supplierId]?.name ?: ""
        }
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = Global.edgeMargin),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item{
            PageHeader(
                title = if (isEditing) "Edit Order" else "Add Order"
            )
        }

        if(upsertOrder.id != null){
            item{
                InputField(
                    inputName = "Order Date",
                    inputHint = "Order Date",
                    inputValue = upsertOrder.orderDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                    editable = false
                )
            }
        }

        item{
            DropdownInputField(
                inputName = "Medicine",
                inputHint = "Select Medicine",
                selectedValue = selectedMedicine,
                onValueChange = { newSelection ->
                    selectedMedicine = newSelection

                    val selectedId = medicineMap.values.firstOrNull { it.brandName == selectedMedicine }?.id
                    selectedId?.let {
                        orderViewModel.updateData { it.copy(medicineId = selectedId) }
                    }
                },
                dropdownOptions = medicineNames,
                width = Dimension.fillToConstraints
            )
        }

        item {
            DropdownInputField(
                inputName = "Supplier",
                inputHint = "Select Supplier",
                selectedValue = selectedSupplier,
                onValueChange = { newSelection ->
                    selectedSupplier = newSelection

                    val selectedId = supplierMap.values.firstOrNull { it.name == selectedSupplier }?.id
                    selectedId?.let {
                        orderViewModel.updateData { it.copy(supplierId = selectedId) }
                    }
                },
                dropdownOptions = supplierNames,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item{
            InputField(
                inputName = "Ordered Quantity",
                inputHint = "Enter quantity",
                inputValue = if (upsertOrder.quantity == -1) "" else upsertOrder.quantity.toString(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { newValue ->
                    val quantity = newValue.toIntOrNull() ?: -1
                    orderViewModel.updateData { it.copy(quantity = quantity) }
                },
            )
        }

        if(upsertOrder.id != null){
            item{
                InputField(
                    inputName = "Remaining Quantity",
                    inputHint = "Enter quantity",
                    inputValue = if (upsertOrder.remainingQuantity == -1) "" else upsertOrder.remainingQuantity.toString(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { newValue ->
                        val quantity = newValue.toIntOrNull() ?: -1
                        orderViewModel.updateData { it.copy(remainingQuantity = quantity) }
                    },
                )
            }
        }

        item{
            InputField(
                inputName = "Price Per Unit",
                inputHint = "Enter price",
                inputValue = if (upsertPrice == "") "" else upsertPrice,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                onValueChange = { newValue -> orderViewModel.updatePrice(newValue) }
            )
        }

        item{
            val selectedDate =
                if (upsertOrder.expirationDate == LocalDate.of(2000, 1, 1)) "Choose date"
                else upsertOrder.expirationDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))

            DatePickerInputField(
                inputName = "Expiration Date",
                inputHint = "Choose date",
                selectedDate = selectedDate,
                onDateSelected = { newValue ->
                    val parsedDate = LocalDate.parse(newValue, DateTimeFormatter.ofPattern("MMM d, yyyy"))
                    orderViewModel.updateData { it.copy(expirationDate = parsedDate) }
                }
            )
        }

        item {
            Confirm(
                action = "Confirm Order",
                confirmOnclick = {
                    coroutineScope.launch {
                        try{
                            orderViewModel.validateOrder()
                            orderViewModel.save()
                            Toast.makeText(context, "Order placed", Toast.LENGTH_SHORT).show()
                            orderViewModel.reset()

                            navController.popBackStack()

                        } catch (e: MedicinaException){
                            Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                cancelOnclick = {
                    orderViewModel.delete()
                    orderViewModel.reset()
                    if(isEditing){
                        navController.navigate(Screen.Orders.route){
                            popUpTo(Screen.Orders.route) {
                                inclusive = true
                            }
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
            Spacing(Global.edgeMargin)
        }
    }
}