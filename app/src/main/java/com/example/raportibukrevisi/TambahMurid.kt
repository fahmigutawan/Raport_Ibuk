package com.example.raportibukrevisi

import android.os.Bundle
import android.os.VibrationAttributes
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TambahMurid:AppCompatActivity() {
    private lateinit var tahunAjaran1: TextInputEditText
    private lateinit var tahunAjaran2: TextInputEditText
    private lateinit var namaLengkap: TextInputEditText
    private lateinit var nomorInduk: TextInputEditText
    private lateinit var tambah_btn: Button
    private lateinit var kelas: TextInputEditText
    private lateinit var hapus_kelas: Button
    private lateinit var hapus_induk: Button
    private lateinit var hapus_nama: Button

    private var isConnected=false

    private var dbReference = DbReference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tambahmurid_activity)

        //deklarasi
        setDeklarasi()
        setConnectedState()

        tambah_btn.setOnClickListener {
            if(
                tahunAjaran2.text.toString()!=""
                && tahunAjaran1.text.toString()!=""
                && namaLengkap.text.toString()!=""
                && nomorInduk.text.toString()!=""
                && kelas.text.toString()!=""
                && (kelas.text.toString() == "A" || kelas.text.toString()=="B" || kelas.text.toString() == "a" || kelas.text.toString()=="b")
                && (tahunAjaran2.text.toString().toInt() - tahunAjaran1.text.toString().toInt()==1)
            ) {
                if(isConnected) {
                    uploadDataMurid()
                    Toast.makeText(applicationContext, "BERHASIL", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext,"Masalah Koneksi", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(applicationContext,"CEK KEMBALI DATA ANDA", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setDeklarasi(){
        tahunAjaran1 = findViewById(R.id.tahun1_tambah_input)
        tahunAjaran2 = findViewById(R.id.tahun2_tambah_input)
        namaLengkap = findViewById(R.id.nama_tambah_input)
        nomorInduk = findViewById(R.id.nomorInduk_tambah_input)
        tambah_btn = findViewById(R.id.tambahMurid_btn)
        kelas = findViewById(R.id.kelas_tambah_input)
        hapus_induk = findViewById(R.id.hapusinduk_tambah_kelas)
        hapus_nama = findViewById(R.id.hapusnama_tambah_button)
        hapus_kelas = findViewById(R.id.hapuskelas_tambah_button)
    }
    fun setConnectedState(){
        val connectedRef = Firebase.database.getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    isConnected=true
                } else {
                    isConnected=false
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    fun uploadDataMurid(){
        var tahunAjaran1 = tahunAjaran1.text.toString()
        var tahunAjaran2 = tahunAjaran2.text.toString()
        var kelas = kelas.text.toString()
        kelas = kelas.lowercase()
        var nomorInduk = nomorInduk.text.toString()
        var nama = namaLengkap.text.toString()
        var tahunTmp = tahunAjaran1+"_"+tahunAjaran2

        //masukkan informasi child
        dbReference.refMain().child(tahunTmp).child("tahun").setValue(tahunTmp)
        dbReference.refMain().child(tahunTmp).child(kelas).child("kelas").setValue(kelas)

        //masukkan identitas
        dbReference.refMain().child(tahunTmp).child(kelas).child(nama).child("nama").setValue(nama)
        dbReference.refMain().child(tahunTmp).child(kelas).child(nama).child("kelas").setValue(kelas)
        dbReference.refMain().child(tahunTmp).child(kelas).child(nama).child("nomor induk").setValue(nomorInduk)
        dbReference.refMain().child(tahunTmp).child(kelas).child(nama).child("tahun").setValue(tahunTmp)

        //masukkan tugas yang sudah ada ke dalam child murid
        dbReference.refKalimat().child("a").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item:DataSnapshot in snapshot.children){
                    dbReference.refMain()
                        .child(tahunTmp)
                        .child(kelas)
                        .child(nama)
                        .child("tugas")
                        .child(item.child("nama bidang").getValue().toString())
                        .child("nama bidang")
                        .setValue(item.child("nama bidang").getValue().toString())
                    dbReference.refMain()
                        .child(tahunTmp)
                        .child(kelas)
                        .child(nama)
                        .child("tugas")
                        .child(item.child("nama bidang").getValue().toString())
                        .child("nilai")
                        .setValue("0")

                    dbReference.refKalimat().child("a").removeEventListener(this)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}