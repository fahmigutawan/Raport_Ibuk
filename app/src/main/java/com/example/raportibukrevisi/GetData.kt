package com.example.raportibukrevisi

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class GetData {
    private var dbRef = DbReference()

    fun getNilai(tahun:String,kelas:String,nama:String,bidang:String):String{
        val ref = dbRef.refMain().child(tahun).child(kelas).child(nama).child("tugas").child(bidang)
        var nilai = ""

        ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                nilai = snapshot.child("nilai").getValue().toString()

                ref.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        return nilai
    }
}