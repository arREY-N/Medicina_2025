package com.example.medicina.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
    var designation by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val designationList = listOf("Pharmacist", "Manager", "Owner" )

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val guidelines = setupColumnGuidelines()

            val (
                bannerText,
                firstNameField,
                lastNameField,
                middleNameField,
                designationField,
                usernameField,
                passwordField,
                confirmPasswordField,
                signUpButton
            ) = createRefs()

            Text(
                "Welcome to Medicina!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(bannerText) {
                    top.linkTo(parent.top, margin = 88.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            InputField(
                inputName = "First name",
                inputHint = "First name",
                inputValue = firstname,
                onValueChange = { firstname = it },
                modifier = Modifier
                    .constrainAs(firstNameField) {
                        top.linkTo(bannerText.bottom, margin = 24.dp)
                        start.linkTo(parent.start)
                    }
            )

            InputField(
                inputName = "Last name",
                inputHint = "Last name",
                inputValue = lastname,
                onValueChange = { lastname = it },
                modifier = Modifier.constrainAs(lastNameField) {
                    top.linkTo(firstNameField.bottom, margin = 12.dp)
                    start.linkTo(guidelines.c1start)
                }
            )

            InputField(
                inputName = "Middle name",
                inputHint = "Middle name",
                inputValue = middlename,
                onValueChange = { middlename = it },
                modifier = Modifier.constrainAs(middleNameField) {
                    top.linkTo(lastNameField.bottom, margin = 12.dp)
                    start.linkTo(guidelines.c1start)
                }
            )

            DropdownInputField(
                inputName = "Designation",
                inputHint = "Designation",
                selectedValue = designation,
                onValueChange = { designation = it },
                dropdownOptions = designationList,
                modifier = Modifier.constrainAs(designationField){
                    top.linkTo(middleNameField.bottom, margin = 12.dp)
                    start.linkTo(guidelines.c1start)
                    end.linkTo(guidelines.c4end)
                    width = Dimension.fillToConstraints
                },
                width = Dimension.fillToConstraints
            )

            InputField(
                inputName = "Username",
                inputHint = "Username",
                inputValue = username,
                onValueChange = { username = it },
                modifier = Modifier.constrainAs(usernameField) {
                    top.linkTo(designationField.bottom, margin = 12.dp)
                    start.linkTo(guidelines.c1start)
                }
            )

            InputField(
                inputName = "Password",
                inputHint = "Password",
                inputValue = password,
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = { password = it },
                modifier = Modifier.constrainAs(passwordField) {
                    top.linkTo(usernameField.bottom, margin = 12.dp)
                    start.linkTo(guidelines.c1start)
                }
            )

            InputField(
                inputName = "Confirm Password",
                inputHint = "Confirm Password",
                inputValue = confirmPassword,
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = { confirmPassword = it },
                modifier = Modifier.constrainAs(confirmPasswordField) {
                    top.linkTo(passwordField.bottom, margin = 12.dp)
                    start.linkTo(guidelines.c1start)
                }
            )

            UIButton(
                "Sign up",
                modifier = Modifier
                    .constrainAs(signUpButton) {
                        top.linkTo(confirmPasswordField.bottom, margin = 24.dp)
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
                            Toast.makeText(context, "$firstname signed up!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(context, Homepage::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            intent.putExtra("firstname", firstname)
                            context.startActivity(intent)
                        }
                    }
                }
            )
        }
        Spacing(80.dp)
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    ComposePracticeTheme {
        ScreenContainer { SignUpScreen() }
    }
}