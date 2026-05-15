package com.example.yadro_test_3.presentation

import android.Manifest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yadro_test_3.aidl.Contact
import com.example.yadro_test_3.domain.usecase.DeleteDuplicateContactsUseCase
import com.example.yadro_test_3.domain.usecase.GetContactsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ContactViewModel(
    private val getContactsUseCase: GetContactsUseCase,
    private val deleteDuplicatesUseCase: DeleteDuplicateContactsUseCase
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val groupedContacts = _contacts.map { contacts ->
        contacts.groupBy {
            it.name.firstOrNull()?.uppercase() ?: "#"
        }.toSortedMap()
    }

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> get() = _status

    private val _requestPermissionsEvent = MutableStateFlow<Array<String>?>(null)
    val requestPermissionsEvent: StateFlow<Array<String>?> = _requestPermissionsEvent.asStateFlow()

    fun checkPermissionsAndLoadContacts(hasReadPermission: Boolean, hasWritePermission: Boolean) {
        if (hasReadPermission && hasWritePermission) {
            loadContacts()
        } else {
            _requestPermissionsEvent.value = arrayOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
            )
        }
    }

    fun onPermissionResult(results: Map<String, Boolean>) {
        _requestPermissionsEvent.value = null

        val allGranted = results.values.all { it }
        if (allGranted) {
            loadContacts()
        } else {
            _status.value = "Разрешение на доступ к контактам не предоставлено"
        }
    }

    fun loadContacts() {
        getContactsUseCase { contacts ->
            viewModelScope.launch {
                _contacts.value = contacts
            }
        }
    }

    fun deleteDuplicates() {
        deleteDuplicatesUseCase.deleteDuplicates { result ->
            _status.value = result
            loadContacts()
        }
    }

    fun clearStatusMessage() {
        _status.value = null
    }
}
