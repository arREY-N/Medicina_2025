package com.example.medicina.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.example.medicina.functions.MedicinaException
import com.example.medicina.model.UserSession
import com.example.medicina.ui.theme.CustomGray
import com.example.medicina.ui.theme.CustomGreen
import com.example.medicina.viewmodel.InventoryViewModel
import com.example.medicina.viewmodel.MedicineViewModel
import com.example.medicina.viewmodel.NotificationViewModel
import com.example.medicina.viewmodel.OrderViewModel
import com.example.medicina.viewmodel.SupplierViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item{
            PageHeader(
                title = "Orders"
            )
        }

        if(UserSession.designationID != 3){
            item{
                CreateButton(
                    "Add New Order",
                    inheritedModifier = Modifier.fillMaxWidth(),
                    onclick = {
                        navController.navigate(Screen.UpsertOrder.createRoute(-1))
                    }
                )
            }
        }

        if(orders.isNotEmpty()) {
            items(orders) { order ->
                val orderedItem = medicines[order.medicineId]?.copy()
                val supplier = suppliers[order.supplierId]?.copy()
                val expireDate = order.expirationDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                if (orderedItem != null) {
                    InfoPills(
                        modifier = Modifier.fillMaxWidth(),
                        infoColor = listOf(CustomGreen),
                        max = order.quantity,
                        current = order.remainingQuantity,
                        content = {
                            OrderPillText(
                                orderedItem = orderedItem.brandName ?: "",
                                supplier = supplier?.name ?: "",
                                date = expireDate,
                                quantity = order.quantity.toString(),
                                price = String.format(Locale.US, "%.2f", order.price)
                            )
                        },
                        onClickAction = {
                            order.id?.let{
                                navController.navigate(Screen.UpsertOrder.createRoute(order.id))
                            }
                        }
                    )
                }
            }
        } else {
            item{
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacing(80.dp)
                    Text(
                        text = "No Order History",
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
fun UpsertOrderScreen(
    orderID: Int? = null,
    orderViewModel: OrderViewModel,
    inventoryViewModel: InventoryViewModel,
    supplierViewModel: SupplierViewModel,
    notificationViewModel: NotificationViewModel,
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

    val isPermitted = UserSession.designationID != 3

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = Global.edgeMargin),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item{
            PageHeader(
                title = if (isEditing) "Edit Order" else "Add Order",
                subtitle = if (isEditing) "Non-admins can only edit the remaining quantity." else "Please answer all fields to place an order."
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
                width = Dimension.fillToConstraints,
                editable = isPermitted
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
                modifier = Modifier.fillMaxWidth(),
                editable = isPermitted
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
                editable = isPermitted
            )
        }



        item{
            InputField(
                inputName = "Price Per Unit",
                inputHint = "Enter price",
                inputValue = if (upsertPrice == "") "" else upsertPrice,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                onValueChange = { newValue -> orderViewModel.updatePrice(newValue) },
                editable = isPermitted
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
            val selectedDate =
                if (upsertOrder.expirationDate == LocalDate.of(2000, 1, 1)) "Choose date"
                else upsertOrder.expirationDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))

            DatePicker(
                inputName = "Expiration Date",
                inputHint = "Select expiration date",
                selectedDate = selectedDate,
                onDateSelected = { newValue ->
                    val parsedDate = LocalDate.parse(newValue, DateTimeFormatter.ofPattern("MMM d, yyyy"))
                    orderViewModel.updateData { it.copy(expirationDate = parsedDate) }
                },
                editable = isPermitted
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

                            if(isEditing){
                                notificationViewModel.addNotification(
                                    banner = "An order entry was edited!",
                                    message = "${selectedMedicine} order was edited!",
                                    overview = "Order edited",
                                    action = "EDITED",
                                    source = UserSession.username,
                                )

                                if(orderViewModel.getTotalQuantity(orderViewModel.orderData.value.medicineId).value <= Global.threshold){
                                    notificationViewModel.addNotification(
                                        banner = "Medicine quantity below threshold!",
                                        message = "The remaining quantity for ${selectedMedicine}  is now within the set threshold! Restock immediately!",
                                        overview = "Medicine quantity edited",
                                        action = "EDITED",
                                        source = UserSession.username,
                                    )
                                } else if (orderViewModel.getTotalQuantity(orderViewModel.orderData.value.medicineId).value == 0){
                                    notificationViewModel.addNotification(
                                        banner = "Medicine out of stock!",
                                        message = "There's no remaining stock for ${selectedMedicine}! Restock immediately!",
                                        overview = "Medicine out of stock",
                                        action = "EDITED",
                                        source = UserSession.username,
                                    )
                                }
                            } else{
                                notificationViewModel.addNotification(
                                    banner = "A new order was added!",
                                    message = "${selectedMedicine} order was added!",
                                    overview = "Order added",
                                    action = "ADDED",
                                    source = UserSession.username,
                                )
                            }

                            navController.popBackStack()

                        } catch (e: MedicinaException){
                            Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                cancelOnclick = {
                    if(isEditing){
                        if(!isPermitted){
                            Toast.makeText(context, "Must be an admin to delete orders", Toast.LENGTH_SHORT).show()
                        } else {
                            notificationViewModel.addNotification(
                                banner = "An order entry was deleted!",
                                message = "${selectedMedicine} order was deleted!",
                                overview = "Order deleted",
                                action = "DELETED",
                                source = UserSession.username,
                            )

                            navController.navigate(Screen.Orders.route){
                                popUpTo(Screen.Orders.route) {
                                    inclusive = true
                                }
                            }
                            orderViewModel.delete()
                            orderViewModel.reset()
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