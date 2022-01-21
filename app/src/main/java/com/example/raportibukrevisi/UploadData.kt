package com.example.raportibukrevisi

class UploadData {
    private var dbRef = DbReference()

    fun nilai(tahun:String,kelas:String,nama:String,bidang:String,nilai:String){
        dbRef.refMain().child(tahun).child(kelas).child(nama).child("tugas").child(bidang).child("nilai").setValue(nilai)
    }
}