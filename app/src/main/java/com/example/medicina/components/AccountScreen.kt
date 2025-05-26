package com.example.medicina.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.example.medicina.components.LayoutGuidelines.setupColumnGuidelines
import com.example.medicina.functions.AccountFunctions
import com.example.medicina.functions.MedicinaException
import com.example.medicina.model.UserSession
import com.example.medicina.ui.theme.CustomBlack
import com.example.medicina.ui.theme.CustomRed
import com.example.medicina.view.Homepage
import com.example.medicina.viewmodel.AccountViewModel

@Composable
fun ViewAccounts(
    navController: NavController,
    accountViewModel: AccountViewModel
){
    val accounts by accountViewModel.accounts.collectAsState()
    val designationMap by accountViewModel.designationsMap.collectAsState()

    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if(accounts.isNotEmpty()){
            items(accounts) { item ->
                InfoPills(
                    infoColor = CustomRed,
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        NotificationPillText(
                            title = "${item.firstname} ${item.lastname}",
                            subtitle = item.username,
                            details = designationMap[item.designationID]?.designation ?: ""
                        )
                    },
                    onClickAction = {
                        navController.navigate(Screen.ViewAccount.createRoute(item.id))
                    }
                )
            }
        }
    }
}

@Composable
fun AccountScreen(
    id: Int,
    accountViewModel: AccountViewModel
){
    val context = LocalContext.current

    LaunchedEffect(id) {
        accountViewModel.getAccountById(id)
    }

    val account by accountViewModel.account.collectAsState()
    val accountInformation by accountViewModel.editAccount.collectAsState()

    val designations by accountViewModel.designations.collectAsState()
    val designationNames = designations.map { it.designation }
    val designationMap by accountViewModel.designationsMap.collectAsState()

    var confirmPassword by remember { mutableStateOf("") }

    var selectedDesignation by remember { mutableStateOf("") }

    var editing by remember { mutableStateOf(false) }

    val actionText = if (editing) "Save" else "Edit"

    LaunchedEffect(accountInformation) {
        selectedDesignation = designationMap.values.firstOrNull { it.id == accountInformation.designationID }?.designation?: ""
    }

    LazyColumn (
        modifier = Modifier.fillMaxWidth()
    ) {
        item{
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val guidelines = setupColumnGuidelines()

                val (
                    bannerText,
                    editButton
                ) = createRefs()

                Text(
                    text = "Hello, ${account.firstname}!",
                    fontSize = 32.sp,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    color = CustomBlack,
                    modifier = Modifier.constrainAs(bannerText) {
                        top.linkTo(parent.top, margin = 32.dp)
                        start.linkTo(parent.start, margin = 8.dp)
                    }
                )

                UIButton(
                    text = actionText,
                    modifier = Modifier
                        .constrainAs(editButton) {
                            top.linkTo(bannerText.top)
                            end.linkTo(guidelines.c4end)
                            width = Dimension.fillToConstraints
                        },
                    onClickAction = {
                        if(!editing){
                            editing = true
                        } else {
                            try{
                                AccountFunctions.handleSignUp(
                                    account.firstname,
                                    account.lastname,
                                    account.middlename,
                                    account.username,
                                    account.password,
                                    confirmPassword
                                )

                                accountViewModel.updateData { it.copy(designationID = 2) }
                                accountViewModel.saveAccount()
                                Toast.makeText(context, "${account.username} signed up!", Toast.LENGTH_SHORT).show()

                                confirmPassword = ""
                                editing = false
                            } catch(e: MedicinaException){
                                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    isCTA = false
                )
            }
        }
        item{
            InputField(
                inputName = "Username",
                inputHint = "Username",
                inputValue = accountInformation.username,
                onValueChange = { newValue -> accountViewModel.updateData{ it.copy(username = newValue) } },
                modifier = Modifier.fillMaxWidth(),
                editable = editing
            )
        }

        item{
            DropdownInputField(
                inputName = "Designation",
                inputHint = "Designation",
                selectedValue = selectedDesignation,
                onValueChange = { newSelection ->
                    selectedDesignation = newSelection

                    val selectedId = designationMap.values.firstOrNull { it.designation == selectedDesignation }?.id
                    selectedId?.let {
                        accountViewModel.updateData { it.copy(designationID = selectedId) }
                    }
                },
                dropdownOptions = designationNames,
                width = Dimension.fillToConstraints,
                editable = if(UserSession.designationID != 0) false else editing
            )
        }

        item{
            InputField(
                inputName = "First name",
                inputHint = "First name",
                inputValue = accountInformation.firstname,
                onValueChange = { newValue -> accountViewModel.updateData{ it.copy(firstname = newValue) } },
                modifier = Modifier.fillMaxWidth(),
                editable = editing
            )
        }
        item{
            InputField(
                inputName = "Last name",
                inputHint = "Last name",
                inputValue = accountInformation.lastname,
                onValueChange = { newValue -> accountViewModel.updateData{ it.copy(lastname = newValue) } },
                modifier = Modifier.fillMaxWidth(),
                editable = editing
            )
        }
        item{
            InputField(
                inputName = "Middle name",
                inputHint = "Middle name",
                inputValue = accountInformation.middlename,
                onValueChange = { newValue -> accountViewModel.updateData{ it.copy(middlename = newValue) } },
                modifier = Modifier.fillMaxWidth(),
                editable = editing
            )
        }

        if(accountInformation.id == UserSession.accountID){
            item{
                InputField(
                    inputName = "Password",
                    inputHint = "Password",
                    inputValue = accountInformation.password,
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = { newValue -> accountViewModel.updateData{ it.copy(password = newValue) } },
                    modifier = Modifier.fillMaxWidth(),
                    editable = editing
                )
            }
            item{
                if(editing){
                    InputField(
                        inputName = "Confirm Password",
                        inputHint = "Confirm Password",
                        visualTransformation = PasswordVisualTransformation(),
                        inputValue = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}