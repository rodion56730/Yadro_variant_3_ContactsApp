package com.example.yadro_test_3.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.example.yadro_test_3.aidl.IContactService
import com.example.yadro_test_3.aidl.IDeleteCallback
import com.example.yadro_test_3.aidl.IGetContactsCallback
import com.example.yadro_test_3.data.service.ContactService
import com.example.yadro_test_3.aidl.Contact
import com.example.yadro_test_3.aidl.Contact as AidlContact
import com.example.yadro_test_3.domain.repository.ContactRepository
import java.util.concurrent.ConcurrentLinkedQueue

class ContactRepositoryImpl(private val context: Context) : ContactRepository {

    private var contactService: IContactService? = null
    private var isConnected = false
    private val pendCallbacks = ConcurrentLinkedQueue<() -> Unit>()

    private val serviceConnect = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            contactService = IContactService.Stub.asInterface(service)
            isConnected = true

            while (true) {
                val callback = pendCallbacks.poll() ?: break
                callback.invoke()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isConnected = false
            contactService = null
        }
    }

    init {
        connectToService()
    }

    private fun connectToService() {
        if (isConnected || contactService != null) return
        val intent = Intent(context, ContactService::class.java)
        context.bindService(intent, serviceConnect, Context.BIND_AUTO_CREATE)
    }

    override fun getContacts(callback: (List<Contact>) -> Unit) {
        fun executeGetContacts() {
            contactService?.getContacts(object : IGetContactsCallback.Stub() {
                override fun onResult(contacts: MutableList<AidlContact>?) {
                    callback(contacts ?: emptyList())
                }
            })
        }

        if (isConnected && contactService != null) {
            executeGetContacts()
        } else {
            pendCallbacks.add { executeGetContacts() }
            connectToService()
        }
    }

    override fun deleteDuplicates(callback: (String) -> Unit) {
        fun execDeleteDuplicates() {
            contactService?.deleteDuplicateContacts(object : IDeleteCallback.Stub() {
                override fun onComplete(status: String?) {
                    callback(status ?: "no result")
                }
            })
        }

        if (isConnected && contactService != null) {
            execDeleteDuplicates()
        } else {
            pendCallbacks.add { execDeleteDuplicates() }
            connectToService()
        }
    }

    fun disconnect() {
        try {
            context.unbindService(serviceConnect)
        } catch (e: Exception) {
            Log.e("Contacts_yadro_var3", e.message.toString())
        }
        contactService = null
        isConnected = false
    }
}
