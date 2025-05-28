package com.example.medicina.components

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
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
import com.example.medicina.ui.theme.Admin
import com.example.medicina.ui.theme.CustomBlack
import com.example.medicina.ui.theme.CustomGray
import com.example.medicina.ui.theme.CustomRed
import com.example.medicina.ui.theme.SuperAdmin
import com.example.medicina.ui.theme.User
import com.example.medicina.view.Homepage
import com.example.medicina.view.MainActivity
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
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item{
            Spacing(Global.edgeMargin)
            PageHeader(
                title = "Accounts"
            )
        }
        if(accounts.isNotEmpty()){
            items(accounts) { item ->

                val color =
                    when (item.designationID){
                        1 -> {
                            SuperAdmin
                        }
                        2 -> {
                            Admin
                        }
                        else -> {
                            User
                        }
                    }

                InfoPills(
                    infoColor = listOf(color),
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        AccountPillText(
                            name = "${item.firstname} ${item.lastname}",
                            username = item.username,
                            designation = designationMap[item.designationID]?.designation ?: ""
                        )
                    },
                    onClickAction = {
                        navController.navigate(Screen.ViewAccount.createRoute(item.id))
                    }
                )
            }
        } else {
            item{
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacing(80.dp)
                    Text(
                        text = "No Accounts Available",
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Global.edgeMargin),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item{
            PageHeader(
                title = "Hello, ${account.firstname}!"
            )
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
                editable = if(UserSession.designationID == 3) false else editing
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

        item{
            InputField(
                inputName = "Password",
                inputHint = "Password",
                inputValue = accountInformation.password,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = { newValue -> accountViewModel.updateData{ it.copy(password = newValue) } },
                editable = editing
            )
        }

        if(editing && UserSession.designationID == 3){
            item{
                InputField(
                    inputName = "Confirm Password",
                    inputHint = "Confirm Password",
                    visualTransformation = PasswordVisualTransformation(),
                    inputValue = if (UserSession.designationID == 3) confirmPassword else accountInformation.password,
                    onValueChange = { confirmPassword = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item{
            Spacing(8.dp)
            EditButton(
                isCTA = if(editing) true else false,
                text = actionText,
                onEdit = {
                    if(!editing){
                        editing = true
                    } else {
                        try{
                            if(UserSession.designationID == 3){
                                AccountFunctions.handleSignUp(
                                    accountInformation.id ?: 0,
                                    accountInformation.firstname,
                                    accountInformation.lastname,
                                    accountInformation.middlename,
                                    accountInformation.username,
                                    accountInformation.password,
                                    confirmPassword
                                )
                            } else {
                                AccountFunctions.handleSignUp(
                                    id,
                                    accountInformation.firstname,
                                    accountInformation.lastname,
                                    accountInformation.middlename,
                                    accountInformation.username,
                                    accountInformation.password,
                                    accountInformation.password
                                )
                            }

                            accountViewModel.saveAccount()
                            Toast.makeText(context, "Account saved", Toast.LENGTH_SHORT).show()

                            confirmPassword = ""
                            editing = false
                        } catch(e: MedicinaException){
                            Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
            )
        }

        item{
            Spacing(8.dp)
            DeleteButton(
                isCTA = false,
                text = "DELETE",
                onEdit = {
                    accountViewModel.clearLoginState(context)

                    accountViewModel.deleteAccount(account.id)
                    Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()

                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                    (context as Activity).finish()
                },
            )
        }
    }
}