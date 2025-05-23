package com.example.medicina.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.example.medicina.R
import com.example.medicina.components.LayoutGuidelines.setupColumnGuidelines
import com.example.medicina.model.Repository.generics
import com.example.medicina.ui.theme.CustomBlack
import com.example.medicina.ui.theme.CustomGreen
import com.example.medicina.viewmodel.CategoryViewModel
import com.example.medicina.viewmodel.GenericViewModel
import com.example.medicina.viewmodel.MedicineViewModel
import com.example.medicina.viewmodel.RegulationViewModel
import java.util.Locale
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import com.example.medicina.ui.theme.CustomGray
import com.example.medicina.ui.theme.CustomRed
import com.example.medicina.ui.theme.CustomWhite
import com.example.medicina.viewmodel.BrandedGenericViewModel
import com.example.medicina.viewmodel.InventoryViewModel
import com.example.medicina.viewmodel.OrderViewModel
import com.example.medicina.viewmodel.SupplierViewModel
import java.time.format.DateTimeFormatter

@Composable
fun ReadMedicine(
    medicineId: Int,
    navController: NavController,
    medicineViewModel: MedicineViewModel,
    genericViewModel: GenericViewModel,
    orderViewModel: OrderViewModel,
    inventoryViewModel: InventoryViewModel,
    supplierViewModel: SupplierViewModel,
    brandedGenericViewModel: BrandedGenericViewModel
) {

    val medicineData by medicineViewModel.medicineData.collectAsState()
    val medicineCategory by medicineViewModel.medicineCategory.collectAsState()
    val medicineRegulation by medicineViewModel.medicineRegulation.collectAsState()
    val medicines by inventoryViewModel.medicineMap.collectAsState()
    val suppliers by supplierViewModel.supplierMap.collectAsState()

    LaunchedEffect(medicineId) {
        brandedGenericViewModel.reset()
        medicineViewModel.getMedicineById(medicineId)
        brandedGenericViewModel.getGenericsById(medicineId)
    }

    LaunchedEffect(medicineData) {
        medicineViewModel.getMedicineCategory(medicineData.categoryId)
        medicineViewModel.getMedicineRegulation(medicineData.regulationId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = Global.edgeMargin),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        item {
            MedicineOverview(
                brandName = medicineData.brandName,
                genericName = brandedGenericViewModel.getGenericNamesText(medicineId),
                price = String.format(Locale.US, "%.2f", medicineData.price),
                category = medicineCategory.categoryName,
                regulation = medicineRegulation.regulation
            )
        }

        item{
            ExpandableTextSurface(
                modifier = Modifier,
                text = medicineData.description
            )
        }

        item{
            ConstraintLayout(
                modifier = Modifier.fillMaxWidth()
            ) {
                val (editButton) = createRefs()

                Surface(
                    modifier = Modifier
                        .constrainAs(editButton) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                        .fillMaxWidth(),
                    color = Color.Transparent,
                    shape = RoundedCornerShape(50.dp)
                ) {
                    UIButton(
                        text = "Edit",
                        modifier =  Modifier.fillMaxSize(),
                    onClickAction = {
                        navController.navigate(Screen.UpsertMedicine.createRoute(medicineId))
                    },
                    isCTA = false,
                    height = 40.dp
                    )
                }
            }
        }


        item{
            ConstraintLayout(
                modifier = Modifier.fillMaxWidth()
            ) {
                val guidelines = setupColumnGuidelines()

                val (infoCard1, infoCard2, inventoryText) = createRefs()

                Text(
                    text = "Inventory",
                    modifier = Modifier.constrainAs(inventoryText){
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CustomBlack)

                InfoCard(
                    modifier = Modifier.constrainAs(infoCard1){
                        top.linkTo(inventoryText.bottom, margin = 8.dp)
                        start.linkTo(guidelines.c1start)
                        end.linkTo(guidelines.c2end)
                        width = Dimension.fillToConstraints},
                    infoLabel = "Expiring Quantity",
                    infoValue = orderViewModel.getExpiringQuantity(medicineId)
                )

                InfoCard(
                    modifier = Modifier.constrainAs(infoCard2){
                        top.linkTo(inventoryText.bottom, margin = 8.dp)
                        start.linkTo(guidelines.c3start)
                        end.linkTo(guidelines.c4end)
                        width = Dimension.fillToConstraints
                    },
                    infoLabel = "Available Quantity",
                    infoValue = orderViewModel.getTotalQuantity(medicineId)
                )
            }
        }

        val tableData = orderViewModel.getOrderHistory(medicineId)

        if(tableData.isNotEmpty()) {
            item{
                Text(
                    text = "Orders",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CustomBlack)
            }

            items(tableData) { order ->
                val orderedItem = medicines[order.medicineId]?.copy()
                val supplier = suppliers[order.supplierId]?.copy()
                val orderDate = order.orderDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))

                InfoPills(
                    modifier = Modifier.fillMaxWidth(),
                    infoColor = Color(android.graphics.Color.parseColor(medicineViewModel.getMedicineColor(medicineCategory.id))),
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
        } else {
            item{
                Spacing(12.dp)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No order history",
                        color = CustomGray,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacing(12.dp)
            }
        }
    }
}

@Composable
fun UpsertMedicineScreen(
    medicineID: Int? = null,
    medicineViewModel: MedicineViewModel,
    regulationViewModel: RegulationViewModel,
    categoryViewModel: CategoryViewModel,
    brandedGenericViewModel: BrandedGenericViewModel,
    navController: NavController
) {
    val genericMap by brandedGenericViewModel.genericMap.collectAsState()
    val generics by brandedGenericViewModel.generics.collectAsState()
    val genericNames = generics.map { it.genericName }

    val genericList by brandedGenericViewModel.upsertGenericNames.collectAsState()

    val categories by categoryViewModel.categories.collectAsState()
    val categoryMap by categoryViewModel.categoryMap.collectAsState()
    val categoryNames = categories.map { it.categoryName }

    val regulations by regulationViewModel.regulations.collectAsState()
    val regulationMap by regulationViewModel.regulationMap.collectAsState()
    val regulationNames = regulations.map { it.regulation }

    val upsertMedicine by medicineViewModel.upsertMedicine.collectAsState()

    var selectedCategory by remember { mutableStateOf("") }
    var selectedRegulation by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf("") }


    LaunchedEffect(medicineID) {
        if(medicineID == -1){
            medicineViewModel.reset()
            brandedGenericViewModel.reset()
        } else {
            medicineID?.let {
                medicineViewModel.getMedicineById(medicineID)
                brandedGenericViewModel.getGenericsById(medicineID)
            }
        }
    }

    LaunchedEffect(upsertMedicine) {
        if(upsertMedicine.id != -1){
            selectedCategory = categoryMap.values.firstOrNull { it.id == upsertMedicine.categoryId }?.categoryName?: ""
            selectedRegulation = regulationMap.values.firstOrNull { it.id == upsertMedicine.regulationId }?.regulation?: ""
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Spacing(8.dp)
            InputField(
                inputName = "Brand Name",
                inputHint = "Enter brand name",
                inputValue = upsertMedicine.brandName,
                onValueChange = { newValue -> medicineViewModel.updateData{ it.copy(brandName = newValue) }},
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            DropdownInputField(
                inputName = "Generic Name/s",
                inputHint = "Add Generic Name",
                selectedValue = "",
                onValueChange = { newSelection ->
                    selectedItem = newSelection

                    val selectedGeneric =
                        genericMap.values.firstOrNull { it.genericName == newSelection }
                    selectedGeneric?.let {
                        brandedGenericViewModel.updateGenericNames(selectedGeneric)
                    }
                },
                dropdownOptions = genericNames,
                modifier = Modifier.fillMaxWidth()
            )

            if (genericList.isNotEmpty()) {
                Spacing(8.dp)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(genericList) { generic ->
                        UIButton(
                            text = generic.genericName,
                            modifier = Modifier
                                .wrapContentWidth()
                                .height(50.dp),
                            onClickAction = {
                                brandedGenericViewModel.removeGeneric(generic)
                            },
                            isCTA = false
                        )
                    }
                }
            } else {
                Text(
                    text = "No generic names selected",
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                    color = CustomGray
                )
            }
        }

        item {
            InputField(
                inputName = "Price",
                inputHint = "Enter price",
                inputValue = if(upsertMedicine.price == 0f) "" else String.format(Locale.US, "%.2f", upsertMedicine.price),
                onValueChange = { newValue ->
                    val price = newValue.toFloatOrNull() ?: 0f
                    medicineViewModel.updateData{ it.copy(price = price) }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            DropdownInputField(
                inputName = "Regulation",
                inputHint = "Select regulation",
                selectedValue = selectedRegulation,
                onValueChange = { newSelection ->
                    selectedRegulation = newSelection

                    val selectedId = regulationMap.values.firstOrNull { it.regulation == newSelection }?.id
                    selectedId?.let {
                        medicineViewModel.updateData { it.copy(regulationId = selectedId) }
                    }
                },
                dropdownOptions = regulationNames,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            DropdownInputField(
                inputName = "Category",
                inputHint = "Select category",
                selectedValue = selectedCategory,
                onValueChange = { newSelection ->
                    selectedCategory = newSelection

                    val selectedId = categoryMap.values.firstOrNull { it.categoryName == newSelection }?.id
                    selectedId?.let {
                        medicineViewModel.updateData { it.copy(categoryId = selectedId) }
                    } },
                dropdownOptions = categoryNames,
                modifier = Modifier.fillMaxWidth(),
                width = Dimension.fillToConstraints
            )
        }

        item {
            InputField(
                inputName = "Description",
                inputHint = "Enter description",
                inputValue = upsertMedicine.description,
                onValueChange = { newValue -> medicineViewModel.updateData{ it.copy(description = newValue) }},
                isMultiline = true,
                contentAlignment = Alignment.TopStart,
                verticalArrangement = Alignment.Top,
                textPadding = PaddingValues(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
        }

        item{
            val context = LocalContext.current
            Confirm(
                action = "Confirm Medicine",
                confirmOnclick = {
                    val id = medicineViewModel.save()
                    medicineViewModel.getMedicineById(id)
                    val savedMedicine = medicineViewModel.upsertMedicine
                    brandedGenericViewModel.save(id)
                    Toast.makeText(context, "Medicine saved: ${savedMedicine.value.brandName}_${savedMedicine.value.id}", Toast.LENGTH_SHORT).show()
                    medicineViewModel.reset()
                    navController.popBackStack()
                },
                cancelOnclick = {
                    medicineViewModel.delete()
                    medicineViewModel.reset()
                    navController.navigate(Screen.Inventory.route){
                        popUpTo(Screen.Inventory.route) {
                            inclusive = true
                        }
                    }
                }
            )
            Spacing(80.dp)
        }
    }
}