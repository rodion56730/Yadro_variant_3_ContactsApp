package com.example.yadro_test_3.data.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.provider.ContactsContract
import android.util.Log
import com.example.yadro_test_3.R
import com.example.yadro_test_3.aidl.IContactService
import com.example.yadro_test_3.aidl.IDeleteCallback
import com.example.yadro_test_3.aidl.IGetContactsCallback
import com.example.yadro_test_3.aidl.Contact

class ContactService : Service() {

    private val contactServiceBinder = object : IContactService.Stub() {

        override fun getContacts(callback: IGetContactsCallback?) {
                try {
                    val contacts = mutableListOf<Contact>()
                    val resolver = contentResolver
                    val cursor = resolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        arrayOf(
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
                        ),
                        null,
                        null,
                        "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
                    )

                    cursor?.use {
                        val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                        val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                        val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val photoIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

                        while (it.moveToNext()) {
                            val id = it.getString(idIndex)
                            val name = it.getString(nameIndex) ?: "no name"
                            val number = it.getString(numberIndex) ?: "no phone"
                            val photo = it.getString(photoIndex)
                            contacts.add(Contact(id, name, number, photo))
                        }
                    }

                    callback?.onResult(contacts)
                } catch (e: Exception) {
                    Log.e("Contacts_yadro_var3",e.message.toString())
                    callback?.onResult(emptyList())
                }
        }

        override fun deleteDuplicateContacts(callback: IDeleteCallback?) {
                try {
                    val resolver = contentResolver
                    val contactMap = mutableMapOf<String, MutableList<Long>>()

                    val cursor = resolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        arrayOf(
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
                        ),
                        null,
                        null,
                        null
                    )

                    cursor?.use {
                        val idIndex = it.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                        val nameIndex = it.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                        val numberIndex = it.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER)

                        while (it.moveToNext()) {
                            val id = it.getLong(idIndex)
                            val name = it.getString(nameIndex)?.trim() ?: ""
                            val number = it.getString(numberIndex)?.replace("\\s".toRegex(), "") ?: ""
                            val key = "$name|$number"
                            contactMap.getOrPut(key) { mutableListOf() }.add(id)
                        }
                    }

                    val duplicates = contactMap.values.filter { it.size > 1 }
                    val context = applicationContext
                    if (duplicates.isEmpty()) {
                        callback?.onComplete(context.getString(R.string.duplicate_contacts_not_found_message))
                    }
                    else {
                        duplicates.forEach { group ->
                            group.drop(1).forEach { contactId ->
                                resolver.delete(
                                    ContactsContract.RawContacts.CONTENT_URI,
                                    "${ContactsContract.RawContacts.CONTACT_ID} = ?",
                                    arrayOf(contactId.toString())
                                )
                            }
                        }
                        callback?.onComplete(context.getString(R.string.successful_message))
                    }
                } catch (e: Exception) {
                    Log.e("Contacts_yadro_var3", e.message.toString())
                    callback?.onComplete(applicationContext.getString(R.string.error_message))
                }
        }
    }

    override fun onBind(intent: Intent?): IBinder = contactServiceBinder
}
