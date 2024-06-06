package com.app.qaimobile.ui.forgot_password

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.qaimobile.R
import com.app.qaimobile.navigation.Destinations
import com.app.qaimobile.ui.composables.ComposeTextView
import com.app.qaimobile.ui.composables.CustomButton
import com.app.qaimobile.ui.composables.CustomTopAppBar
import com.app.qaimobile.ui.composables.LoadingDialog
import com.app.qaimobile.ui.composables.SimpleTextField
import com.app.qaimobile.ui.theme.CustomBoldTextStyle
import com.app.qaimobile.ui.theme.CustomRegularTextStyle
import com.app.qaimobile.util.showToast
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@Destination(Destinations.FORGOT_PASSWORD_ROUTE)
@Composable
fun ForgotPasswordScreen(
    state: ForgotPasswordState,
    onEvent: (ForgotPasswordViewModelEvent) -> Unit = {},
    uiEvent: SharedFlow<ForgotPasswordUiEvent> = MutableSharedFlow(),
    navHostController: NavController? = null
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        uiEvent.collect {
            when (it) {
                is ForgotPasswordUiEvent.ShowError -> {
                    showToast(context, it.message)
                }

                is ForgotPasswordUiEvent.Success -> {

                }

                is ForgotPasswordUiEvent.Navigate -> {

                }
            }
        }
    }

    if (state.isLoading) {
        LoadingDialog()
    }

    Scaffold(topBar = {
        CustomTopAppBar(title = "", showBackButton = true, onBackButtonPressed = {
            navHostController?.navigateUp()
        })
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(
                    rememberScrollState()
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ComposeTextView(
                text = stringResource(R.string.reset_password),
                style = CustomBoldTextStyle.copy(fontSize = 36.sp),
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .align(Alignment.CenterHorizontally)
            )

            SimpleTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.email,
                onValueChange = { onEvent(ForgotPasswordViewModelEvent.UpdateEmail(it)) },
                placeholder = stringResource(R.string.email_address),
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                })
            )

            CustomButton(btnText = stringResource(R.string.reset_password),
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(),
                onClick = {
                    keyboardController?.hide()
                    onEvent(ForgotPasswordViewModelEvent.ResetPassword)
                })

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                Text(
                    text = stringResource(R.string.remember_your_password),
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
                        append(stringResource(R.string.log_in))
                    }
                }

                ClickableText(
                    text = signUpAnnotatedText,
                    style = CustomRegularTextStyle.copy(fontSize = 18.sp)
                ) { _ ->
                    navHostController?.navigateUp()
                }
            }
        }

    }


}