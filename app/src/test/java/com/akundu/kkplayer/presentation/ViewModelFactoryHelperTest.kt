package com.akundu.kkplayer.presentation

import androidx.lifecycle.ViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Test

class ViewModelFactoryHelperTest {
    private class CountingViewModel(
        val id: Int,
    ) : ViewModel()

    @Test
    fun `builds the view model from the initializer`() {
        val factory = viewModelFactory { CountingViewModel(7) }

        assertEquals(7, factory.create(CountingViewModel::class.java).id)
    }

    @Test
    fun `invokes the initializer for every creation`() {
        var created = 0
        val factory = viewModelFactory { CountingViewModel(created++) }

        val first = factory.create(CountingViewModel::class.java)
        val second = factory.create(CountingViewModel::class.java)

        assertNotSame(first, second)
        assertEquals(2, created)
    }
}
