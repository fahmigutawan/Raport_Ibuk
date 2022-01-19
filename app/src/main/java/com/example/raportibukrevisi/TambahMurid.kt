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
    lateinit var tahunAjaran1: TextInputEditText
    lateinit var tahunAjaran2: TextInputEditText
    lateinit var namaLengkap: TextInputEditText
    lateinit var nomorInduk: TextInputEditText
    lateinit var tambah_btn: Button
    lateinit var kelas: TextInputEditText
    lateinit var hapus_kelas: Button
    lateinit var hapus_induk: Button
    lateinit var hapus_nama: Button

    private var isConnected=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tambahmurid_activity)

        //deklarasi
        setDeklarasi()
        setConnectedState()

        //setDatabase saat tombol tambah diklik
        tambah_btn.setOnClickListener {
            if(tahunAjaran1.getText().toString()=="" ||
                tahunAjaran2.getText().toString()=="" ||
                nomorInduk.getText().toString()=="" ||
                namaLengkap.getText().toString()=="" ||
                kelas.getText().toString()=="")
            {
                Toast.makeText(applicationContext,"Harap isi semua form", Toast.LENGTH_SHORT).show()
            }
            else{
                if(isConnected){
                    if(
                        tahunAjaran2.getText().toString().trim().toInt()-tahunAjaran1.getText().toString().trim().toInt()==1
                    ){
                        if(
                            kelas.getText().toString()=="A"||
                            kelas.getText().toString()=="a"||
                            kelas.getText().toString()=="B"||
                            kelas.getText().toString()=="b"
                        ){
                            setNewDatabase(
                                tahunAjaran1.getText().toString().trim(),
                                tahunAjaran2.getText().toString().trim(),
                                namaLengkap.getText().toString().trim(),
                                nomorInduk.getText().toString().trim(),
                                kelas.getText().toString().trim().lowercase()
                            )
                            Toast.makeText(applicationContext,"Data Berhasil Dikirim", Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(applicationContext,"Gagal, Pastikan Masukkan Kelas A atau B",
                                Toast.LENGTH_SHORT).show()
                        }

                    }else{
                        Toast.makeText(applicationContext,"Gagal, Cek Kembali Tahun Ajaran", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(applicationContext,"Harap Pastikan Koneksi Anda Baik", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //setting hapus
        hapus_induk.setOnClickListener {
            nomorInduk.setText("")
        }
        hapus_nama.setOnClickListener {
            namaLengkap.setText("")
        }
        hapus_kelas.setOnClickListener {
            kelas.setText("")
        }

    }

    fun setNewDatabase(tahunAjaran1:String,tahunAjaran2:String,namaLengkap:String,nomorInduk:String,kelas:String){
        val tahunAjaran_temp = tahunAjaran1+"_"+tahunAjaran2

        val dbRef = FirebaseDatabase.getInstance().getReference("DB").child(tahunAjaran_temp)

        //masukkan nomor induk, nama, tahun, kelas
        val dbRefParent = dbRef.child(kelas).child(namaLengkap)

        //informasi child
        dbRef.child("tahun").setValue(tahunAjaran_temp)
        dbRef.child(kelas).child("kelas").setValue(kelas)

        //data
        dbRefParent.child("nama").setValue(namaLengkap)
        dbRefParent.child("nomor induk").setValue(nomorInduk)
        dbRefParent.child("tahun").setValue(tahunAjaran_temp)
        dbRefParent.child("kelas").setValue(kelas)
        dbRefParent.child("kelas").setValue(kelas)

        //masukkan tugas baru
        FirebaseDatabase
            .getInstance()
            .getReference("KalimatNilaiRaport")
            .child("a")
            .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item: DataSnapshot in snapshot.getChildren()){
                    var namatugas_tmp = item.child("nama bidang").getValue().toString()

                    //tambahkan item ke user
                    dbRefParent.child("tugas").child(namatugas_tmp).child("nama tugas").setValue(namatugas_tmp)
                    dbRefParent.child("tugas").child(namatugas_tmp).child("nilai").setValue("0")
                }

                FirebaseDatabase
                    .getInstance()
                    .getReference("KalimatNilaiRaport")
                    .child("a").removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
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
}

