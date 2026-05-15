package com.example.yadro_test_3.aidl;

import com.example.yadro_test_3.aidl.Contact;

interface IGetContactsCallback {
    void onResult(in List<Contact> contacts);
}
