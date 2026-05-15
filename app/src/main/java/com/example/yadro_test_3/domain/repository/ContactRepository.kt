package com.example.yadro_test_3.domain.repository

import com.example.yadro_test_3.aidl.Contact

interface ContactRepository {
    fun getContacts(callback: (List<Contact>) -> Unit)
    fun deleteDuplicates(callback: (String) -> Unit)
}
