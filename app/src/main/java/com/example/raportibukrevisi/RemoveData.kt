package com.example.raportibukrevisi

class RemoveData {
    private var dbRef = DbReference()

    fun removeMurid(tahun:String,kelas:String,nama:String){
        dbRef.refMain().child(tahun).child(kelas).child(nama).removeValue()
    }
}