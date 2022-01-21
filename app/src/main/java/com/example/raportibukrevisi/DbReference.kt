package com.example.raportibukrevisi

import com.google.firebase.database.*

class DbReference {
    private val database=FirebaseDatabase.getInstance()

    fun refMain():DatabaseReference{
        return database.getReference("DB")
    }
    fun refKalimat():DatabaseReference{
        return database.getReference("KalimatNilaiRaport")
    }
}