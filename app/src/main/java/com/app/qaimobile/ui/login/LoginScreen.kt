package com.app.qaimobile.ui.login

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.qaimobile.R
import com.app.qaimobile.ui.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination(route = "login")
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    navHostController: DestinationsNavigator? = null
) {
    var emailState by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }
    var isRememberMeChecked by remember { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val loginState by loginViewModel.loginState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ -> }

    Scaffold { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val (column, row) = createRefs()
            Column(modifier = Modifier
                .constrainAs(column) {
                    top.linkTo(parent.top)
                    bottom.linkTo(row.top)
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center) {
                Text(
                    text = stringResource(R.string.welcome_back),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight(700),
                    ),
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .align(Alignment.CenterHorizontally)
                )
                OutlinedTextField(
                    value = emailState,
                    onValueChange = { emailState = it },
                    placeholder = { Text(stringResource(R.string.email_address)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        cursorColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                )
                OutlinedTextField(
                    value = passwordState,
                    onValueChange = { passwordState = it },
                    placeholder = { Text(stringResource(R.string.password)) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    trailingIcon = {
                        val image =
                            if (passwordVisible) painterResource(id = R.drawable.ic_visibility)
                            else painterResource(R.drawable.ic_visibility_off)

                        val description = if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(painter = image, description)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        cursorColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                        Checkbox(
                            checked = isRememberMeChecked,
                            onCheckedChange = { isRememberMeChecked = it },
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Text(text = stringResource(R.string.remember_me))
                }
                Button(
                    onClick = {
                        val email = emailState
                        val password = passwordState
                        loginViewModel.login(email, password)
                        navHostController?.navigate(HomeScreenDestination)
                    },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text(text = stringResource(R.string.str_continue))
                }

                val forgotPasswordText = stringResource(R.string.forgot_password)
                val annotatedText = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            textDecoration = TextDecoration.Underline,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        append(forgotPasswordText)
                    }
                }

                ClickableText(text = annotatedText, modifier = Modifier.padding(top = 16.dp)) { _ ->
                    println("Clicked on forgot password text")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.dont_have_an_account),
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    val signUpAnnotatedText = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                textDecoration = TextDecoration.Underline,
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            append(stringResource(R.string.sign_up))
                        }
                    }

                    ClickableText(text = signUpAnnotatedText) { _ ->
                        println("Clicked on don't have an account text")
                    }
                }


                when (loginState) {
                    is LoginState.Loading -> {
                        Text(text = "Logging in...")
                    }

                    is LoginState.Success -> {
                        Text(text = "Login successful!")
                    }

                    is LoginState.Error -> {
                        Text(text = "Login failed: ${(loginState as LoginState.Error).message}")
                    }

                    is LoginState.Idle -> Unit
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(row) {
                    width = Dimension.matchParent
                    bottom.linkTo(parent.bottom, 16.dp)
                }) {
                Text(
                    text = stringResource(R.string.terms_off_use),
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse("https://www.projectdavid.ai/")
                        val chooserIntent = Intent.createChooser(intent, "Open with")
                        launcher.launch(chooserIntent)
                    }
                )
                Spacer(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .height(20.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.onBackground)
                )
                Text(text = stringResource(R.string.privacy_policy), modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://www.projectdavid.co.uk/privacy-policy")
                    val chooserIntent = Intent.createChooser(intent, "Open with")
                    launcher.launch(chooserIntent)
                })
            }
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewLoginScreen() {
    //LoginScreen(hiltViewModel())
}