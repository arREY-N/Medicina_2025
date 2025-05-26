package com.example.medicina.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.medicina.ui.theme.ComposePracticeTheme
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.constraintlayout.compose.ConstraintLayout
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.constraintlayout.compose.Dimension
import androidx.core.view.WindowCompat
import com.example.medicina.R
import com.example.medicina.components.Global
import com.example.medicina.components.LayoutGuidelines.setupColumnGuidelines
import com.example.medicina.components.InputField
import com.example.medicina.components.Spacing
import com.example.medicina.components.UIButton
import com.example.medicina.ui.theme.CustomWhite
import com.example.medicina.functions.*
import com.example.medicina.model.Repository
import com.example.medicina.model.UserSession

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContent {
            ComposePracticeTheme {
                val context = LocalContext.current

                LaunchedEffect(Unit) {
                    Repository.initialize(context)
                    Repository.initializeSampleData()
                }


                ScreenContainer{ LogInScreen() }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposePracticeTheme {
        ScreenContainer{ LogInScreen() }
    }
}

@Composable
fun LogInScreen() {
    val scrollState = rememberScrollState()

    val context = LocalContext.current
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .imePadding()
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val guidelines = setupColumnGuidelines()

            val (
                usernameField,
                passwordField,
                loginButton,
                signUpButton,
                logo
            ) = createRefs()

            Surface(
                modifier = Modifier
                    .width(300.dp)
                    .height(200.dp)
                    .constrainAs(logo) {
                        top.linkTo(parent.top, margin = 144.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                color = CustomWhite
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_green),
                    contentDescription = "navigation icon",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            InputField(
                inputName = "Username",
                inputHint = "Enter your username",
                inputValue = username,
                onValueChange = { username = it },
                modifier = Modifier
                    .constrainAs(usernameField) {
                        top.linkTo(logo.bottom)
                        start.linkTo(parent.start)
                    }
            )

            InputField(
                inputName = "Password",
                inputHint = "Enter your password",
                visualTransformation = PasswordVisualTransformation(),
                inputValue = password,
                onValueChange = { password = it },
                modifier = Modifier.constrainAs(passwordField) {
                    top.linkTo(usernameField.bottom, margin = 8.dp)
                    start.linkTo(guidelines.c1start)
                }
            )

            UIButton(
                "Log in",
                modifier = Modifier
                    .constrainAs(loginButton) {
                        top.linkTo(passwordField.bottom, margin = 24.dp)
                        start.linkTo(guidelines.c2start)
                        end.linkTo(guidelines.c3end)
                        width = Dimension.fillToConstraints
                    },
                onClickAction = {
                    if (username.isEmpty() || password.isEmpty()) {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    } else {
                        try{
                            val account = AccountFunctions.handleLogin(username, password)
                            Toast.makeText(context, "Successful login!", Toast.LENGTH_SHORT).show()
                            UserSession.accountID = account.id
                            UserSession.designationID = account.designationID
                            val intent = Intent(context, Homepage::class.java)
                            context.startActivity(intent)
                            (context as? ComponentActivity)?.finish()

                        } catch (e: MedicinaException){
                            Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )

            UIButton(
                "Sign up",
                modifier = Modifier
                    .constrainAs(signUpButton) {
                        top.linkTo(loginButton.bottom, margin = 8.dp)
                        start.linkTo(guidelines.c2start)
                        end.linkTo(guidelines.c3end)
                        width = Dimension.fillToConstraints
                    },
                isCTA = false,
                onClickAction = {
                    val intent = Intent(context, SignUpActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }

        Spacing(Global.edgeMargin)
    }
}
