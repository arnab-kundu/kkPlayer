package com.akundu.kkplayer.feature.onboarding.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akundu.kkplayer.ui.theme.KkPlayerTheme
import com.google.accompanist.pager.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Preview(showBackground = false)
@Composable
fun SetupGuideViewPagerPreview() {
    KkPlayerTheme() {
        SetupGuideViewPager(fontColor = Color.White)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SetupGuideViewPager(fontColor: Color) {
    Column(modifier = Modifier.padding(30.dp)) {
        val items = createItems()
        val pagerState = rememberPagerState()
        val coroutineScope = rememberCoroutineScope()

        HorizontalPager(
            count = items.size,
            state = pagerState,
            modifier = Modifier
        ) { currentPage ->
            Column(
                modifier = Modifier
            ) {
                Text(
                    text = items[currentPage].title,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    fontSize = 32.sp,
                    color = fontColor,
                    modifier = Modifier
                        .fillMaxWidth(1F)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = items[currentPage].subtitle,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                    color = fontColor,
                    modifier = Modifier
                        .fillMaxWidth(1F)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = items[currentPage].description,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                    color = fontColor,
                    modifier = Modifier
                        .fillMaxWidth(1F)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            activeColor = fontColor,
            inactiveColor = Color(0xFF26C9EB)
        )

        Text(
            text = "OUR SETUP GUIDE",
            color = Color.White, fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth(1F),
            textAlign = TextAlign.Center,
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HorizontalTabs(
    items: List<ViewPagerContent>,
    pagerState: PagerState,
    scope: CoroutineScope
) {
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        }
    ) {
        items.forEachIndexed { index, item ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(page = index)
                    }
                }
            ) {
                Text(text = item.title)
            }
        }
    }
}

private fun createItems() = listOf(
    ViewPagerContent(title = "Title1", subtitle = "Subtitle1", description = "Description1"),
    ViewPagerContent(title = "Title2", subtitle = "Subtitle2", description = "Description2"),
    ViewPagerContent(title = "Title3", subtitle = "Subtitle3", description = "Description3"),
    ViewPagerContent(title = "Title4", subtitle = "Subtitle4", description = "Description4"),
    ViewPagerContent(title = "Title5", subtitle = "Subtitle5", description = "Description5")
)

data class ViewPagerContent(
    val title: String,
    val subtitle: String,
    val description: String
)