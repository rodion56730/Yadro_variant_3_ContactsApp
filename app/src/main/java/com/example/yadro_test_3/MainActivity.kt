package com.example.yadro_test_3

import com.example.yadro_test_3.presentation.view.ContactListScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yadro_test_3.data.repository.ContactRepositoryImpl
import com.example.yadro_test_3.domain.usecase.DeleteDuplicateContactsUseCase
import com.example.yadro_test_3.domain.usecase.GetContactsUseCase
import com.example.yadro_test_3.presentation.ContactViewModel

class MainActivity : ComponentActivity() {

    private lateinit var repository: ContactRepositoryImpl
    private lateinit var viewModel: ContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = ContactRepositoryImpl(applicationContext)
        viewModel = ViewModelProvider(
            this,
            ContactViewModelFactory(repository)
        )[ContactViewModel::class.java]

        setContent {
            MaterialTheme {
                ContactListScreen(viewModel = viewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        repository.disconnect()
    }

    private class ContactViewModelFactory(
        private val repository: ContactRepositoryImpl
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ContactViewModel(
                GetContactsUseCase(repository),
                DeleteDuplicateContactsUseCase(repository)
            ) as T
        }
    }
}
