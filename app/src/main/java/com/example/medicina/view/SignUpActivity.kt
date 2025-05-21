package com.example.medicina.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.Dimension
import androidx.core.view.WindowCompat
import com.example.medicina.components.DropdownInputField
import com.example.medicina.components.InputField
import com.example.medicina.components.LayoutGuidelines.setupColumnGuidelines
import com.example.medicina.components.Spacing
import com.example.medicina.components.UIButton
import com.example.medicina.functions.AccountException
import com.example.medicina.functions.AccountFunctions

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
fun SignUpScreen(){
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var middlename by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        item{
            Spacing(88.dp)
            Text(
                "Welcome to Medicina!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item{
            InputField(
                inputName = "First name",
                inputHint = "First name",
                inputValue = firstname,
                onValueChange = { firstname = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item{
            InputField(
                inputName = "Last name",
                inputHint = "Last name",
                inputValue = lastname,
                onValueChange = { lastname = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item{
            InputField(
                inputName = "Middle name",
                inputHint = "Middle name",
                inputValue = middlename,
                onValueChange = { middlename = it },
                modifier = Modifier.fillMaxWidth())
        }
        item{
            InputField(
                inputName = "Username",
                inputHint = "Username",
                inputValue = username,
                onValueChange = { username = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item{
            InputField(
                inputName = "Password",
                inputHint = "Password",
                inputValue = password,
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item{
            InputField(
                inputName = "Confirm Password",
                inputHint = "Confirm Password",
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
                        if(firstname.isEmpty() || lastname.isEmpty()
                            || username.isEmpty() || password.isEmpty()
                            || confirmPassword.isEmpty()){
                            Toast.makeText(context, "Please fill up all the fields", Toast.LENGTH_SHORT).show()
                        } else {
                            if(password != confirmPassword){
                                Toast.makeText(context, "Retype password", Toast.LENGTH_SHORT).show()
                            } else {
                                try{
                                    AccountFunctions.handleSignUp(firstname, lastname, middlename, username, password)
                                    Toast.makeText(context, "$firstname signed up!", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(context, Homepage::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    }
                                    intent.putExtra("firstname", firstname)
                                    context.startActivity(intent)
                                } catch(e: AccountException){
                                    Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                )
            }
            Spacing(80.dp)
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