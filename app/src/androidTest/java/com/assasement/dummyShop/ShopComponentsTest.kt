package com.assasement.dummyShop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.assasement.dummyShop.ui.theme.DummyShopTheme
import com.assasement.dummyShop.view.components.ErrorState
import com.assasement.dummyShop.view.components.ProductSearchBar
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ShopComponentsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun readOnlySearchBar_invokesSearchCallbackWhenClicked() {
        var clickCount = 0

        composeRule.setContent {
            DummyShopTheme {
                ProductSearchBar(
                    query = "",
                    onQueryChange = {},
                    onSearch = { clickCount++ },
                    readOnly = true,
                )
            }
        }

        composeRule.onNodeWithTag("readOnlySearchBarClickTarget").performClick()

        assertEquals(1, clickCount)
    }

    @Test
    fun editableSearchBar_updatesText() {
        var query by mutableStateOf("")

        composeRule.setContent {
            DummyShopTheme {
                ProductSearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = {},
                )
            }
        }

        composeRule.onNodeWithTag("editableSearchBar").performTextInput("laptop")

        composeRule.onNodeWithTag("editableSearchBar").assertTextContains("laptop")
    }

    @Test
    fun errorState_showsMessageAndRetries() {
        var retryCount by mutableIntStateOf(0)

        composeRule.setContent {
            DummyShopTheme {
                ErrorState(
                    message = "Couldn't load products",
                    onRetry = { retryCount++ },
                )
            }
        }

        composeRule.onNodeWithText("Couldn't load products").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").performClick()

        assertEquals(1, retryCount)
    }
}

