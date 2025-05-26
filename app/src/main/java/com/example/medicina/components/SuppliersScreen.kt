package com.example.medicina.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.example.medicina.components.LayoutGuidelines.setupColumnGuidelines
import com.example.medicina.functions.MedicinaException
import com.example.medicina.ui.theme.CustomRed
import com.example.medicina.viewmodel.InventoryViewModel
import com.example.medicina.viewmodel.MedicineViewModel
import com.example.medicina.viewmodel.OrderViewModel
import com.example.medicina.viewmodel.SupplierViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ViewSuppliers(
    navController: NavController,
    supplierViewModel: SupplierViewModel,
    orderViewModel: OrderViewModel
) {
    val suppliers by supplierViewModel.suppliers.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        item{
            CreateButton(
                "Add New Supplier",
                inheritedModifier = Modifier.fillMaxWidth(),
                onclick = {
                    navController.navigate(Screen.UpsertSupplier.createRoute(-1))
                }
            )
        }
        if(suppliers.isNotEmpty()){
            items(suppliers) { supplier ->
                InfoPills(
                    infoColor = CustomRed,
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        SupplierPillText(
                            supplierName = supplier.name,
                            orders = orderViewModel.getOrdersBySupplierId(supplier.id ?: -1).size
                        )
                    },
                    onClickAction = {
                        navController.navigate(Screen.ViewSupplier.createRoute(supplier.id))
                    }
                )
            }
        }
    }
}

@Composable
fun ViewSupplier(
    supplierID: Int,
    navController: NavController,
    orderViewModel: OrderViewModel,
    inventoryViewModel: InventoryViewModel,
    supplierViewModel: SupplierViewModel
){
    val orders by orderViewModel.supplierOrder.collectAsState()
    val medicines by inventoryViewModel.medicineMap.collectAsState()
    val supplier by supplierViewModel.supplierMap.collectAsState()
    val supplierData by supplierViewModel.supplierData.collectAsState()

    LaunchedEffect (supplierID) {
        supplierViewModel.getSupplierById(supplierID)
        orderViewModel.getOrdersBySupplierId(supplierID)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        item{
            PageHeader(
                title = supplierData.name
            )
        }

        item{
            EditButton(
                onEdit = {
                    navController.navigate(Screen.UpsertSupplier.createRoute(supplierID))
                }
            )
        }

        if(orders.isNotEmpty()){
            items(orders) { order ->
                val orderedItem = medicines[order.medicineId]?.copy()
                val orderSupplier = supplier[order.supplierId]?.copy()
                InfoPills(
                    infoColor = CustomRed,
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        OrderPillText(
                            orderedItem = orderedItem?.brandName ?: "",
                            supplier = orderSupplier?.name ?: "",
                            date = order.orderDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                            quantity = order.quantity,
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
    }
}

@Composable
fun UpsertSuppliersScreen(
    supplierID: Int? = -1,
    supplierViewModel: SupplierViewModel,
    navController: NavController
){
    val coroutineScope = rememberCoroutineScope()

    val supplier by supplierViewModel.upsertSupplier.collectAsState()

    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(supplierID) {
        if(supplierID == -1){
            supplierViewModel.reset()
            isEditing = false
        } else {
            supplierID?.let{
                supplierViewModel.getSupplierById(supplierID)
            }
            isEditing = true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item{
            PageHeader(
                title = if (isEditing) "Edit Supplier" else "Add Supplier"
            )
        }

        item{
            InputField(
                inputName = "Supplier Name",
                inputHint = "Enter supplier name",
                inputValue = supplier.name,
                onValueChange = { newValue -> supplierViewModel.updateData { it.copy(name = newValue) } },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item{
            InputField(
                inputName = "Email",
                inputHint = "Enter email",
                inputValue = supplier.email,
                onValueChange = { newValue -> supplierViewModel.updateData { it.copy(email = newValue) } },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
        }

        item{
            val context = LocalContext.current
            Confirm(
                action = "Confirm Supplier",
                confirmOnclick = {
                    coroutineScope.launch {
                        try{
                            supplierViewModel.validateScreen()
                            val id = supplierViewModel.save()
                            supplierViewModel.getSupplierById(id)
                            val savedSupplier = supplierViewModel.upsertSupplier
                            Toast.makeText(context, "Supplier saved: ${savedSupplier.value.name}", Toast.LENGTH_SHORT).show()
                            supplierViewModel.reset()
                            navController.popBackStack()
                        } catch (e: MedicinaException){
                            Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                cancelOnclick = {
                    println("DELETE CLICKED")
                    coroutineScope.launch{
                        println("DELETE ONGOING!")
                        supplierViewModel.delete()
                        supplierViewModel.reset()
                        if (isEditing){
                            navController.navigate(Screen.Suppliers.route){
                                popUpTo(Screen.Suppliers.route) {
                                    inclusive = true
                                }
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                }
            )
            Spacing(Global.edgeMargin)
        }
    }
}
