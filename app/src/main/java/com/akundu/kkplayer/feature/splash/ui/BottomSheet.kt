package com.akundu.kkplayer.feature.splash.ui

import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue.Expanded
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.akundu.kkplayer.feature.splash.view.LoginLayout


@ExperimentalMaterialApi
@Composable
private fun BottomSheet() {
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = BottomSheetState(Expanded, LocalDensity.current))
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
