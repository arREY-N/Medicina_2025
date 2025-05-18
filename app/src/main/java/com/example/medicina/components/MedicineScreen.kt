package com.example.medicina.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.medicina.ui.theme.CustomBlack
import com.example.medicina.ui.theme.CustomGreen
import com.example.medicina.viewmodel.CategoryViewModel
import com.example.medicina.viewmodel.MedicineViewModel
import com.example.medicina.viewmodel.RegulationViewModel
import java.util.Locale

@Composable
fun ReadMedicine(
    medicineId: Int,
    navController: NavController,
    viewModel: MedicineViewModel
) {
    val scrollState = rememberScrollState()

    val medicineData by viewModel.medicineData.collectAsState()
    val medicineCategory by viewModel.medicineCategory.collectAsState()
    val medicineRegulation by viewModel.medicineRegulation.collectAsState()

    LaunchedEffect(medicineId) {
        viewModel.getMedicineById(medicineId)
    }

    LaunchedEffect(medicineData) {
        viewModel.getMedicineCategory(medicineData.categoryId)
        viewModel.getMedicineRegulation(medicineData.regulationId)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val guidelines = setupColumnGuidelines()

            val (
                overview,
                editButton,
                description,
                infoCard1,
                infoCard2,
                inventoryText,
                medOrders) = createRefs()

            MedicineOverview(
                modifier = Modifier.constrainAs(overview){
                    top.linkTo(parent.top, margin = 16.dp)
                },
                brandName = medicineData.brandName,
                genericName = medicineData.genericName,
                price = String.format(Locale.US, "%.2f", medicineData.price),
                category = medicineCategory.categoryName,
                regulation = medicineRegulation.regulation
            )

            Surface(
                modifier = Modifier
                    .constrainAs(editButton) {
                        top.linkTo(overview.top, margin = 4.dp)
                        end.linkTo(overview.end)
                    }
                    .size(40.dp),
                color = Color.Transparent,
                shape = RoundedCornerShape(50.dp)
            ) {
                Button(
                    onClick = {
                        navController.navigate(Screen.UpsertMedicine.createRoute(medicineId))
                    },
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CustomGreen
                    ),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "Edit button",
                        modifier = Modifier.size(30.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            ExpandableTextSurface(
                modifier = Modifier.constrainAs(description){
                    top.linkTo(overview.bottom, margin = 8.dp)
                },
                text = medicineData.description
            )

            Text(
                text = "Inventory",
                modifier = Modifier.constrainAs(inventoryText){
                    top.linkTo(description.bottom, margin = 16.dp)
                    start.linkTo(guidelines.c1start)
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
                infoValue = viewModel.getExpiringQuantity(medicineId)
            )

            InfoCard(
                modifier = Modifier.constrainAs(infoCard2){
                    top.linkTo(inventoryText.bottom, margin = 8.dp)
                    start.linkTo(guidelines.c3start)
                    end.linkTo(guidelines.c4end)
                    width = Dimension.fillToConstraints
                },
                infoLabel = "Available Quantity",
                infoValue = viewModel.getTotalQuantity(medicineId)
            )

            val tableData = viewModel.getOrderHistory(medicineId)

            OrderTable(modifier = Modifier.constrainAs(medOrders){
                top.linkTo(infoCard1.bottom, margin = 8.dp)
                start.linkTo(guidelines.c1start)
                end.linkTo(guidelines.c4end)
                width = Dimension.fillToConstraints
            },
                tableData = tableData
            )
        }
        Spacing(24.dp)
    }
}

@Composable
fun UpsertMedicineScreen(
    medicineID: Int? = null,
    medicineViewModel: MedicineViewModel,
    regulationViewModel: RegulationViewModel,
    categoryViewModel: CategoryViewModel
) {
    val categories by categoryViewModel.categories.collectAsState()
    val categoryNames = categories.map { it.categoryName }
    val categoryMap by categoryViewModel.categoryMap.collectAsState()

    val regulations by regulationViewModel.regulations.collectAsState()
    val regulationNames = regulations.map { it.regulation }
    val regulationMap by regulationViewModel.regulationMap.collectAsState()

    val upsertMedicine by medicineViewModel.upsertMedicine.collectAsState()

    var selectedCategory by remember { mutableStateOf("") }
    var selectedRegulation by remember { mutableStateOf("") }

    LaunchedEffect(medicineID) {
        if(medicineID == -1){
            medicineViewModel.reset()
        } else {
            medicineID?.let { medicineViewModel.getMedicineById(medicineID) }
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
            .fillMaxSize()
    ) {
        item {
            Spacing(16.dp)
            InputField(
                inputName = "Brand Name",
                inputHint = "Enter brand name",
                inputValue = upsertMedicine.brandName,
                onValueChange = { newValue -> medicineViewModel.updateData{ it.copy(brandName = newValue) }},
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Spacing(8.dp)
            InputField(
                inputName = "Generic Name",
                inputHint = "Enter generic name",
                inputValue = upsertMedicine.genericName,
                onValueChange = { newValue -> medicineViewModel.updateData{ it.copy(genericName = newValue) }},
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Spacing(8.dp)
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
            Spacing(8.dp)
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
            Spacing(8.dp)
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
            Spacing(8.dp)
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
    }
}