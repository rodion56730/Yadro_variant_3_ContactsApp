package com.example.yadro_test_3.aidl;

import com.example.yadro_test_3.aidl.IDeleteCallback;
import com.example.yadro_test_3.aidl.IGetContactsCallback;

interface IContactService {
    void getContacts(IGetContactsCallback callback);
    void deleteDuplicateContacts(IDeleteCallback callback);
}
