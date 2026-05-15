package com.example.yadro_test_3.domain.usecase

import com.example.yadro_test_3.aidl.Contact
import com.example.yadro_test_3.domain.repository.ContactRepository

class GetContactsUseCase(private val repository: ContactRepository) {
    operator fun invoke(callback: (List<Contact>) -> Unit) {
        repository.getContacts(callback)
    }
}
