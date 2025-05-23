package com.example.medicina.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.medicina.ui.theme.ComposePracticeTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.Dimension
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicina.R
import com.example.medicina.components.DropdownInputField
import com.example.medicina.components.InputField
import com.example.medicina.components.LayoutGuidelines.setupColumnGuidelines
import com.example.medicina.components.PageHeader
import com.example.medicina.components.Spacing
import com.example.medicina.components.UIButton
import com.example.medicina.functions.AccountException
import com.example.medicina.functions.AccountFunctions
import com.example.medicina.model.UserSession
import com.example.medicina.ui.theme.CustomWhite
import com.example.medicina.viewmodel.AccountViewModel

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)
        setContent {
            ComposePracticeTheme {
                ScreenContainer{ SignUpScreen() }
            }
        }
    }
}

@Composable
fun SignUpScreen(
    accountViewModel: AccountViewModel = viewModel()
){
    val context = LocalContext.current

    val account by accountViewModel.editAccount.collectAsState()

    var confirmPassword by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        item{
            Spacing(72.dp)
            PageHeader(
                modifier = Modifier.fillMaxWidth(),
                title = "Welcome to Medicina!"
            )
        }
        item{
            InputField(
                inputName = "First name",
                inputHint = "First name",
                inputValue = account.firstname,
                onValueChange = { newValue -> accountViewModel.updateData { it.copy(firstname = newValue.trim()) } },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item{
            InputField(
                inputName = "Last name",
                inputHint = "Last name",
                inputValue = account.lastname,
                onValueChange = { newValue -> accountViewModel.updateData { it.copy(lastname = newValue.trim()) } },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item{
            InputField(
                inputName = "Middle name",
                inputHint = "Middle name",
                inputValue = account.middlename,
                onValueChange = { newValue -> accountViewModel.updateData { it.copy(middlename = newValue.trim()) } },
                modifier = Modifier.fillMaxWidth())
        }
        item{
            InputField(
                inputName = "Username",
                inputHint = "at least 8 alphanumeric characters",
                inputValue = account.username,
                onValueChange = { newValue -> accountViewModel.updateData { it.copy(username = newValue.trim()) } },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item{
            InputField(
                inputName = "Password",
                inputHint = "at least 8 characters long",
                inputValue = account.password,
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = { newValue -> accountViewModel.updateData { it.copy(password = newValue.trim()) } },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item{
            InputField(
                inputName = "Confirm Password",
                inputHint = "Retype password",
                inputValue = confirmPassword,
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = { confirmPassword = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item{
            Spacing(8.dp)
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val guidelines = setupColumnGuidelines()

                val (signUpButton) = createRefs()

                UIButton(
                    "Sign up",
                    modifier = Modifier
                        .constrainAs(signUpButton) {
                            start.linkTo(guidelines.c2start)
                            end.linkTo(guidelines.c3end)
                            width = Dimension.fillToConstraints },
                    onClickAction = {
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

                            val intent = Intent(context, Homepage::class.java)
                            context.startActivity(intent)
                        } catch(e: AccountException){
                            Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
        item{
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val guidelines = setupColumnGuidelines()

                val (loginButton) = createRefs()

                UIButton(
                    "Log in",
                    modifier = Modifier
                        .constrainAs(loginButton) {
                            start.linkTo(guidelines.c2start)
                            end.linkTo(guidelines.c3end)
                            width = Dimension.fillToConstraints
                        },
                    isCTA = false,
                    onClickAction = {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }
            Spacing(40.dp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    ComposePracticeTheme {
        ScreenContainer { SignUpScreen() }
    }
}