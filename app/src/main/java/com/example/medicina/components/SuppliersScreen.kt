package com.example.medicina.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.example.medicina.components.LayoutGuidelines.setupColumnGuidelines
import com.example.medicina.ui.theme.CustomRed
import com.example.medicina.viewmodel.InventoryViewModel
import com.example.medicina.viewmodel.MedicineViewModel
import com.example.medicina.viewmodel.OrderViewModel
import com.example.medicina.viewmodel.SupplierViewModel
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
                            orders = orderViewModel.getOrdersBySupplierId(supplier.id ?: -1)
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

    LaunchedEffect (supplierID) {
        orderViewModel.getOrdersBySupplierId(supplierID)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        item{
            Text("Supplier Orders")
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
                        navController.navigate(Screen.UpsertOrder.createRoute(order.id))
                    }
                )
            }
        }
    }
}

@Composable
fun UpsertSuppliersScreen(
    supplierID: Int? = -1,
    supplierViewModel: SupplierViewModel
){
    val supplier by supplierViewModel.upsertSupplier.collectAsState()

    LaunchedEffect(supplierID) {
        if(supplierID == -1){
            supplierViewModel.reset()
        } else {
            supplierID?.let{
                supplierViewModel.getSupplierById(supplierID)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
