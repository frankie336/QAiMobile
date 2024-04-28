//LoginScreen.kt
package com.app.qaimobile.ui.login

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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.app.qaimobile.R
import com.app.qaimobile.navigation.Destinations
import com.app.qaimobile.ui.composables.ComposeTextView
import com.app.qaimobile.ui.composables.CustomButton
import com.app.qaimobile.ui.composables.LoadingDialog
import com.app.qaimobile.ui.composables.PasswordTextField
import com.app.qaimobile.ui.composables.SimpleTextField
import com.app.qaimobile.ui.theme.CustomBoldTextStyle
import com.app.qaimobile.ui.theme.CustomRegularTextStyle
import com.app.qaimobile.ui.theme.QAiMobileTheme
import com.app.qaimobile.util.Constants
import com.app.qaimobile.util.openLink
import com.app.qaimobile.util.rememberActivityOrNull
import com.app.qaimobile.util.showToast
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Destination(Destinations.LOGIN_ROUTE)
@Composable
fun LoginScreen(
    state: LoginState = LoginState(),
    onEvent: (LoginViewModelEvent) -> Unit = {},
    uiEvent: SharedFlow<LoginUiEvent> = MutableSharedFlow(),
    navHostController: DestinationsNavigator? = null
) {

    val context = LocalContext.current
    val activity = rememberActivityOrNull()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        uiEvent.collect {
            when (it) {
                is LoginUiEvent.ShowError -> {
                    showToast(context, it.message)
                }

                LoginUiEvent.Success -> {
                    navHostController?.navigate(Destinations.HOME_ROUTE) {
                        popUpTo(Destinations.LOGIN_ROUTE) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    if (state.isLoading) {
        LoadingDialog()
    }

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
                ComposeTextView(
                    text = stringResource(R.string.welcome_back),
                    style = CustomBoldTextStyle.copy(fontSize = 36.sp),
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .align(Alignment.CenterHorizontally)
                )

                SimpleTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.email,
                    onValueChange = { onEvent(LoginViewModelEvent.UpdateEmail(it)) },
                    placeholder = stringResource(R.string.email_address),
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    singleLine = true,
                )

                PasswordTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.password,
                    onValueChange = { onEvent(LoginViewModelEvent.UpdatePassword(it)) },
                    placeholder = stringResource(R.string.password),
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                        Checkbox(
                            checked = state.isRememberMeChecked,
                            onCheckedChange = { onEvent(LoginViewModelEvent.UpdateRememberMe(it)) },
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    ComposeTextView(text = stringResource(R.string.remember_me))
                }

                CustomButton(
                    btnText = stringResource(R.string.str_continue),
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                    onClick = {
                        keyboardController?.hide()
                        onEvent(LoginViewModelEvent.Authenticate)
                    })

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

                ClickableText(
                    text = annotatedText,
                    modifier = Modifier.padding(top = 16.dp),
                    style = CustomRegularTextStyle
                ) { _ ->
                    navHostController?.navigate(Destinations.FORGOT_PASSWORD_ROUTE)
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
                        style = CustomRegularTextStyle
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

                    ClickableText(text = signUpAnnotatedText, style = CustomRegularTextStyle.copy(fontSize = 18.sp)) { _ ->
                        println("Clicked on don't have an account text")
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.constrainAs(row) {
                    width = Dimension.matchParent
                    bottom.linkTo(parent.bottom, 16.dp)
                }) {
                Text(text = stringResource(R.string.terms_off_use), modifier = Modifier.clickable {
                    activity?.openLink(Constants.TERMS_OF_USE_URL)
                }, style = CustomRegularTextStyle)
                Spacer(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .height(20.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.onBackground)
                )
                Text(text = stringResource(R.string.privacy_policy), modifier = Modifier.clickable {
                    activity?.openLink(Constants.PRIVACY_POLICY_URL)
                }, style = CustomRegularTextStyle)
            }
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewLoginScreen() {
    QAiMobileTheme {
        val state = LoginState(
            email = "Hamza@gmail.com", password = "123456", isRememberMeChecked = true
        )
        LoginScreen(state = state)
    }
}