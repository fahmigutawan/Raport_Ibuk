package com.example.raportibukrevisi

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GetKalimatNilai {
    fun getBintang1(kelas:String,idTugas:String):String{
        val ref = FirebaseDatabase.getInstance().getReference("KalimatNilaiRaport").child(kelas).child(idTugas)
        var kalimat = ""

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                kalimat = snapshot.child("btg1").getValue().toString()

                ref.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return kalimat
    }
    fun getBintang2(kelas:String,idTugas:String):String{
        val ref = FirebaseDatabase.getInstance().getReference("KalimatNilaiRaport").child(kelas).child(idTugas)
        var kalimat = ""

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                kalimat = snapshot.child("btg2").getValue().toString()

                ref.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return kalimat
    }
    fun getBintang3(kelas:String,idTugas:String):String{
        val ref = FirebaseDatabase.getInstance().getReference("KalimatNilaiRaport").child(kelas).child(idTugas)
        var kalimat = ""

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                kalimat = snapshot.child("btg3").getValue().toString()

                ref.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return kalimat
    }
    fun getBintang4(kelas:String,idTugas:String):String{
        val ref = FirebaseDatabase.getInstance().getReference("KalimatNilaiRaport").child(kelas).child(idTugas)
        var kalimat = ""

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                kalimat = snapshot.child("btg4").getValue().toString()

                ref.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return kalimat
    }
}