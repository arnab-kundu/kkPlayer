package com.akundu.kkplayer.feature.splash.view

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue.Expanded
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akundu.kkplayer.R
import com.akundu.kkplayer.feature.splash.model.SplashUiState
import com.akundu.kkplayer.feature.splash.viewModel.SplashViewModel
import com.akundu.kkplayer.ui.theme.AppTextFieldColors

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun SplashPage(
    viewModel: SplashViewModel = SplashViewModel(),
    version: String = "v1.0.0",
    isAnimationEndFlow: Boolean = false,
    uiState: SplashUiState = SplashUiState(),
    loginButtonClick: () -> Unit = {}
) {
    Surface {
        Image(painter = painterResource(id = R.drawable.background), contentDescription = null, contentScale = ContentScale.FillBounds)
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(60.dp))

            val drawable = AnimatedImageVector.animatedVectorResource(R.drawable.avd_splash_icon)
            Image(
                painter = rememberAnimatedVectorPainter(animatedImageVector = drawable, atEnd = isAnimationEndFlow),
                contentDescription = "Logo",
                modifier = Modifier.clip(CircleShape)
            )
            // Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "Logo", modifier = Modifier.clip(CircleShape))
            Spacer(modifier = Modifier.height(15.dp))
            Text(text = "Powered by @k music industries", color = Color.Gray)
            Spacer(modifier = Modifier.height(21.dp))
            if (uiState.isLoadingDotsVisible) {
                DotsFlashing()
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(15.dp))
            Text(text = version, color = Color.DarkGray)

            Spacer(modifier = Modifier.weight(1F))
            Text(text = "©2024 @k music industries. All rights reserved.", color = Color.DarkGray, modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 20.dp), fontSize = 12.sp)
        }
        LoginLayout(viewModel = viewModel, uiState = uiState, loginButtonClick = loginButtonClick)
    }
}

@Preview
@Composable
fun SplashPagePreview() {
    SplashPage()
}

@Preview
@Composable
fun LoginLayout(viewModel: SplashViewModel = SplashViewModel(), uiState: SplashUiState = SplashUiState(), loginButtonClick: () -> Unit = {}) {

    val email = remember { mutableStateOf(TextFieldValue()) }
    val password = remember { mutableStateOf(TextFieldValue()) }
    val biometric = remember { mutableStateOf(false) }

    if (uiState.isLoginLayoutVisible) {
        Column {
            Spacer(modifier = Modifier.weight(1F))
            Column(
                modifier = Modifier
                    .background(color = Color.White)
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(0.dp))
                Text(text = "PLEASE LOGIN:", color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    label = { Text(text = "Email Address", color = Color(0xFF999999)) },
                    // value = email.value,
                    value = uiState.email,
                    // onValueChange = { email.value = it },
                    onValueChange = { viewModel.typingEmail(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.White),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    colors = AppTextFieldColors,
                    isError = uiState.emailError
                )
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    label = { Text(text = "Password", color = Color(0xFF999999)) },
                    // value = password.value,
                    value = uiState.password,
                    // onValueChange = { password.value = it },
                    onValueChange = { viewModel.typingPassword(it) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    colors = AppTextFieldColors,
                    isError = uiState.passwordError,

                    )
                Spacer(modifier = Modifier.height(26.dp))


                // Biometric
                Row(modifier = Modifier.height(48.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(id = R.drawable.ic_fingerprint), contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Set up Biometric", color = Color(0xFF2C2C2C))
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(id = android.R.drawable.ic_dialog_info),
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { /*TODO*/ },
                        colorFilter = ColorFilter.tint(
                            Color(
                                0xFF2196F3
                            )
                        )
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    Switch(checked = biometric.value,
                        colors = SwitchColors(
                            checkedThumbColor = Color.DarkGray,
                            checkedTrackColor = Color.White,
                            checkedBorderColor = Color.Gray,
                            checkedIconColor = Color.Black,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.Black,
                            uncheckedBorderColor = Color.Transparent,
                            uncheckedIconColor = Color.Black,
                            disabledCheckedThumbColor = Color.Black,
                            disabledCheckedTrackColor = Color.Black,
                            disabledCheckedBorderColor = Color.Black,
                            disabledCheckedIconColor = Color.Black,
                            disabledUncheckedThumbColor = Color.Black,
                            disabledUncheckedTrackColor = Color.Black,
                            disabledUncheckedBorderColor = Color.Black,
                            disabledUncheckedIconColor = Color.Black
                        ),
                        onCheckedChange = { biometric.value = it })
                }
                Spacer(modifier = Modifier.height(16.dp))


                Button(
                    onClick = {
                        Log.d("SplashPage", "login onClick = { ... } ")
                        loginButtonClick.invoke()
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .width(180.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF2C2C2C))
                ) {
                    Text(text = "LOGIN", modifier = Modifier, color = Color.White, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Forgot password?",
                    color = Color(0xFF999999),
                    modifier = Modifier
                        .clickable { /*TODO*/ }
                        .align(Alignment.CenterHorizontally),
                    fontSize = 13.sp)
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun BottomSheet() {
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = BottomSheetState(Expanded))
    val scope = rememberCoroutineScope()
    BottomSheetScaffold(
        modifier = Modifier.height(380.dp),
        scaffoldState = scaffoldState,
        sheetContent = {
            // Sheet content
            LoginLayout()
        }
    ) {
        // Screen content
    }
}

val dotSize = 6.dp // made it bigger for demo
val delayUnit = 300 // you can change delay to change animation speed

@Composable
fun DotsPulsing() {

    @Composable
    fun Dot(
        scale: Float
    ) = Spacer(
        Modifier
            .size(dotSize)
            .scale(scale)
            .background(
                color = Color.White,
                shape = CircleShape
            )
    )

    val infiniteTransition = rememberInfiniteTransition()

    @Composable
    fun animateScaleWithDelay(delay: Int) = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 4
                0f at delay with LinearEasing
                1f at delay + delayUnit with LinearEasing
                0f at delay + delayUnit * 2
            }
        )
    )

    val scale1 by animateScaleWithDelay(0)
    val scale2 by animateScaleWithDelay(delayUnit)
    val scale3 by animateScaleWithDelay(delayUnit * 2)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val spaceSize = 2.dp

        Dot(scale1)
        Spacer(Modifier.width(spaceSize))
        Dot(scale2)
        Spacer(Modifier.width(spaceSize))
        Dot(scale3)
    }
}


@Composable
fun DotsElastic() {
    val minScale = 0.6f

    @Composable
    fun Dot(
        scale: Float
    ) = Spacer(
        Modifier
            .size(dotSize)
            .scale(scaleX = minScale, scaleY = scale)
            .background(
                color = Color.White,
                shape = CircleShape
            )
    )

    val infiniteTransition = rememberInfiniteTransition()

    @Composable
    fun animateScaleWithDelay(delay: Int) = infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = minScale,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 4
                minScale at delay with LinearEasing
                1f at delay + delayUnit with LinearEasing
                minScale at delay + delayUnit * 2
            }
        )
    )

    val scale1 by animateScaleWithDelay(0)
    val scale2 by animateScaleWithDelay(delayUnit)
    val scale3 by animateScaleWithDelay(delayUnit * 2)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val spaceSize = 2.dp

        Dot(scale1)
        Spacer(Modifier.width(spaceSize))
        Dot(scale2)
        Spacer(Modifier.width(spaceSize))
        Dot(scale3)
    }
}

@Composable
fun DotsFlashing() {
    val minAlpha = 0.2f

    @Composable
    fun Dot(
        alpha: Float
    ) = Spacer(
        Modifier
            .size(dotSize)
            .alpha(alpha)
            .background(
                color = Color.White,
                shape = CircleShape
            )
    )

    val infiniteTransition = rememberInfiniteTransition()

    @Composable
    fun animateAlphaWithDelay(delay: Int) = infiniteTransition.animateFloat(
        initialValue = minAlpha,
        targetValue = minAlpha,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 4
                minAlpha at delay with LinearEasing
                1f at delay + delayUnit with LinearEasing
                minAlpha at delay + delayUnit * 2
            }
        )
    )

    val alpha1 by animateAlphaWithDelay(0)
    val alpha2 by animateAlphaWithDelay(delayUnit)
    val alpha3 by animateAlphaWithDelay(delayUnit * 2)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val spaceSize = 6.dp

        Dot(alpha1)
        Spacer(Modifier.width(spaceSize))
        Dot(alpha2)
        Spacer(Modifier.width(spaceSize))
        Dot(alpha3)
    }
}

@Composable
fun DotsTyping() {
    val maxOffset = 10f

    @Composable
    fun Dot(
        offset: Float
    ) = Spacer(
        Modifier
            .size(dotSize)
            .offset(y = -offset.dp)
            .background(
                color = Color.White,
                shape = CircleShape
            )
    )

    val infiniteTransition = rememberInfiniteTransition()

    @Composable
    fun animateOffsetWithDelay(delay: Int) = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 4
                0f at delay with LinearEasing
                maxOffset at delay + delayUnit with LinearEasing
                0f at delay + delayUnit * 2
            }
        )
    )

    val offset1 by animateOffsetWithDelay(0)
    val offset2 by animateOffsetWithDelay(delayUnit)
    val offset3 by animateOffsetWithDelay(delayUnit * 2)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = maxOffset.dp)
    ) {
        val spaceSize = 2.dp

        Dot(offset1)
        Spacer(Modifier.width(spaceSize))
        Dot(offset2)
        Spacer(Modifier.width(spaceSize))
        Dot(offset3)
    }
}

@Composable
fun DotsCollision() {
    val maxOffset = 30f
    val delayUnit = 500 // it's better to use longer delay for this animation

    @Composable
    fun Dot(
        offset: Float
    ) = Spacer(
        Modifier
            .size(dotSize)
            .offset(x = offset.dp)
            .background(
                color = Color.White,
                shape = CircleShape
            )
    )

    val infiniteTransition = rememberInfiniteTransition()

    val offsetLeft by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 3
                0f at 0 with LinearEasing
                -maxOffset at delayUnit / 2 with LinearEasing
                0f at delayUnit
            }
        )
    )
    val offsetRight by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 3
                0f at delayUnit with LinearEasing
                maxOffset at delayUnit + delayUnit / 2 with LinearEasing
                0f at delayUnit * 2
            }
        )
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = maxOffset.dp)
    ) {
        val spaceSize = 2.dp

        Dot(offsetLeft)
        Spacer(Modifier.width(spaceSize))
        Dot(0f)
        Spacer(Modifier.width(spaceSize))
        Dot(offsetRight)
    }
}


@Preview(showBackground = true)
@Composable
fun DotsPreview() = MaterialTheme {
    Column(modifier = Modifier.padding(4.dp)) {
        val spaceSize = 16.dp

        Text(
            text = "Dots pulsing",
            style = MaterialTheme.typography.bodyMedium
        )
        DotsPulsing()

        Spacer(Modifier.height(spaceSize))

        Text(
            text = "Dots elastic",
            style = MaterialTheme.typography.bodyMedium
        )
        DotsElastic()

        Spacer(Modifier.height(spaceSize))

        Text(
            text = "Dots flashing",
            style = MaterialTheme.typography.bodyMedium
        )
        DotsFlashing()

        Spacer(Modifier.height(spaceSize))

        Text(
            text = "Dots typing",
            style = MaterialTheme.typography.bodyMedium
        )
        DotsTyping()

        Spacer(Modifier.height(spaceSize))

        Text(
            text = "Dots collision",
            style = MaterialTheme.typography.bodyMedium
        )
        DotsCollision()
    }
}