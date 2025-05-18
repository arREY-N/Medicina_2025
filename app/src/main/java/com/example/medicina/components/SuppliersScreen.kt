package com.example.medicina.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
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

@Composable
fun ViewSuppliers(navController: NavController){
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
    ){
        ConstraintLayout(modifier = Modifier.fillMaxSize()){
            val guidelines = setupColumnGuidelines()

            val (supplier1, supplier2, supplier3, createButton) = createRefs()

            CreateButton(
                "Add New Supplier",
                inheritedModifier = Modifier.constrainAs(createButton){
                    top.linkTo(parent.top, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                onclick = {
                    navController.navigate(Screen.UpsertSupplier.createRoute(0))
                }
            )

            InfoPills(
                infoColor = CustomRed,
                modifier = Modifier.constrainAs(supplier1){
                    top.linkTo(createButton.bottom, margin = 8.dp)
                    start.linkTo(guidelines.c1start)
                },
                content = {
                    NotificationPillText(
                        title = "Supplier 1",
                        subtitle = "Email Address",
                        details = "Contact Number"
                    )
                },
                onClickAction = {
                    navController.navigate(Screen.UpsertSupplier.createRoute(1))
                }
            )

            InfoPills(
                infoColor = CustomRed,
                modifier = Modifier.constrainAs(supplier2){
                    top.linkTo(supplier1.bottom, margin = 8.dp)
                    start.linkTo(guidelines.c1start)
                },
                content = {
                    NotificationPillText(
                        title = "Supplier 2",
                        subtitle = "Email Address",
                        details = "Contact Number"
                    )
                },
                onClickAction = {
                    navController.navigate(Screen.UpsertSupplier.createRoute(1))
                }
            )

            InfoPills(
                infoColor = CustomRed,
                modifier = Modifier.constrainAs(supplier3){
                    top.linkTo(supplier2.bottom, margin = 8.dp)
                    start.linkTo(guidelines.c1start)
                },
                content = {
                    NotificationPillText(
                        title = "Supplier 3",
                        subtitle = "Email Address",
                        details = "Contact Number"
                    )
                },
                onClickAction = {
                    navController.navigate(Screen.UpsertSupplier.createRoute(1))
                }
            )
        }
    }

}

@Composable
fun UpsertSuppliersScreen(
    categoryID: Int = 1,
    // viewModel: OrderViewModel = viewModel()
){
    val scrollState = rememberScrollState()

    Column(modifier = Modifier
        .verticalScroll(scrollState)
        .fillMaxSize()
    ) {
        ConstraintLayout(modifier = Modifier
            .fillMaxSize()
        ) {
            val guidelines = setupColumnGuidelines()

            var supplierName by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var number by remember { mutableStateOf("") }

            val (
                supplierNameField,
                emailField,
                numberField
            ) = createRefs()

            InputField(
                inputName = "Supplier Name",
                inputHint = "Enter supplier name",
                inputValue = supplierName,
                onValueChange = { supplierName = it },
                modifier = Modifier.constrainAs(supplierNameField) {
                    top.linkTo(parent.top, margin = 16.dp)
                    start.linkTo(guidelines.c1start)
                    end.linkTo(guidelines.c4end)
                    width = Dimension.fillToConstraints
                }
            )

            InputField(
                inputName = "Email",
                inputHint = "Enter email",
                inputValue = email,
                onValueChange = { email = it },
                modifier = Modifier.constrainAs(emailField) {
                    top.linkTo(supplierNameField.bottom, margin = 8.dp)
                    start.linkTo(guidelines.c1start)
                    end.linkTo(guidelines.c4end)
                    width = Dimension.fillToConstraints
                }
            )

            InputField(
                inputName = "Contact Number",
                inputHint = "Enter number",
                inputValue = number,
                onValueChange = { number = it },
                modifier = Modifier.constrainAs(numberField) {
                    top.linkTo(emailField.bottom, margin = 8.dp)
                    start.linkTo(guidelines.c1start)
                    end.linkTo(guidelines.c4end)
                    width = Dimension.fillToConstraints
                }
            )
        }
    }
}
