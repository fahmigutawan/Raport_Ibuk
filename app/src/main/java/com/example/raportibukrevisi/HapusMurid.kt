package com.example.raportibukrevisi

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HapusMurid:AppCompatActivity() {
    private lateinit var tahun_hapusmurid_dropdown: AutoCompleteTextView
    private lateinit var kelas_hapusmurid_dropdown: AutoCompleteTextView
    private lateinit var nama_hapusmurid_dropdown: AutoCompleteTextView
    private lateinit var hapus_hapusmurid_btn: Button

    //list dan adapter
    private lateinit var tahunList:ArrayList<String>
    private lateinit var tahunAdapter: ArrayAdapter<String>
    private lateinit var kelasList:ArrayList<String>
    private lateinit var kelasAdapter: ArrayAdapter<String>
    private lateinit var namaList:ArrayList<String>
    private lateinit var namaAdapter: ArrayAdapter<String>

    //dbref
    private lateinit var dbRefMain: DatabaseReference

    //tahun & kelas selected
    private var tahunSelected="_"
    private var kelasSelected="_"
    private var namaSelected="_"

    //connection state
    private var isConnected=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hapusmurid_activity)

        callDeklarasi()
        callUpdateTahunDropdown(dbRefMain)
        callListenerConnectionState()

        //dropdown item selected
        tahun_hapusmurid_dropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //set tahunselected
                setTahunSelected(parent?.getItemAtPosition(position).toString())

                //update kelas
                callUpdateKelasDropdown(dbRefMain.child(getTahunSelected()))

                //update nama
                if(getKelasSelected()!="_"){
                    callUpdateNamaDropdown(dbRefMain.child(getTahunSelected()).child(getKelasSelected()))
                }
            }

        })
        kelas_hapusmurid_dropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //set kelasselected
                setKelasSelected(parent?.getItemAtPosition(position).toString())

                //update nama
                if(tahunSelected!="_"){
                    callUpdateNamaDropdown(dbRefMain.child(getTahunSelected()).child(getKelasSelected()))
                }
            }

        })
        nama_hapusmurid_dropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setNamaSelected(parent?.getItemAtPosition(position).toString())
            }

        })

        //hapus murid
        hapus_hapusmurid_btn.setOnClickListener {
            if(isConnected){
                if(getTahunSelected()!="_" && getKelasSelected()!="_" && getNamaSelected()!="_"){
                    removeData(dbRefMain
                        .child(getTahunSelected())
                        .child(getKelasSelected()))
                    refreshNamaDropdown()
                    Toast.makeText(applicationContext,"Data Murid Berhasil Dihapus", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext,"Harap Masukkan Data Dengan lengkap", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(applicationContext,"GAGAL, Pastikan Koneksi Anda Baik", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //fungsi penting
    fun callDeklarasi(){
        tahun_hapusmurid_dropdown = findViewById(R.id.tahun_hapusmurid_dropdown)
        kelas_hapusmurid_dropdown = findViewById(R.id.kelas_hapusmurid_dropdown)
        nama_hapusmurid_dropdown = findViewById(R.id.nama_hapusmurid_dropdown)
        hapus_hapusmurid_btn = findViewById(R.id.hapus_hapusmurid_btn)

        //list
        tahunList = ArrayList()
        kelasList = ArrayList()
        namaList = ArrayList()
        dbRefMain = FirebaseDatabase.getInstance().getReference("DB")
    }
    fun callUpdateTahunDropdown(ref:DatabaseReference){
        tahun_hapusmurid_dropdown.setText("pilih tahun")
        resetTahunSelected()
        resetKelasSelected()
        resetNamaSelected()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tahunList.clear()
                for(item: DataSnapshot in snapshot.getChildren()){
                    tahunList.add(item.child("tahun").getValue().toString().replace("_","-"))
                }
                ref.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        tahunAdapter = ArrayAdapter(this,
            R.layout.support_simple_spinner_dropdown_item,tahunList)
        tahun_hapusmurid_dropdown.setAdapter(tahunAdapter)
    }
    fun callUpdateKelasDropdown(ref:DatabaseReference){
        kelas_hapusmurid_dropdown.setText("pilih kelas")
        resetKelasSelected()
        resetNamaSelected()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                kelasList.clear()
                for(item: DataSnapshot in snapshot.getChildren()){
                    if(item.child("kelas").getValue().toString()!="null") {
                        kelasList.add(item.child("kelas").getValue().toString().uppercase())
                    }
                }
                ref.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        kelasAdapter = ArrayAdapter(this,
            R.layout.support_simple_spinner_dropdown_item,kelasList)
        kelas_hapusmurid_dropdown.setAdapter(kelasAdapter)
    }
    fun callUpdateNamaDropdown(ref:DatabaseReference){
        resetNamaSelected()
        nama_hapusmurid_dropdown.setText("pilih nama")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                namaList.clear()
                for(item: DataSnapshot in snapshot.getChildren()){
                    if(item.child("nama").getValue().toString()!="null"){
                        namaList.add(item.child("nama").getValue().toString())
                    }
                }
                ref.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        namaAdapter = ArrayAdapter(this,
            R.layout.support_simple_spinner_dropdown_item,namaList)
        nama_hapusmurid_dropdown.setAdapter(namaAdapter)
        namaList.clear()
    }
    fun callListenerConnectionState(){
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
    fun removeData(ref:DatabaseReference){
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //remove data
                ref.child(getNamaSelected()).removeValue()

                ref.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //setter
    fun setTahunSelected(tahun:String){
        tahunSelected=tahun.replace("-","_")
    }
    fun setKelasSelected(kelas:String){
        kelasSelected=kelas.lowercase()
    }
    fun setNamaSelected(nama:String){
        namaSelected=nama
    }

    //getter
    fun getTahunSelected():String{
        return tahunSelected
    }
    fun getKelasSelected():String{
        return kelasSelected
    }
    fun getNamaSelected():String{
        return namaSelected
    }

    //resetter
    fun resetTahunSelected(){
        tahunSelected="_"
    }
    fun resetKelasSelected(){
        kelasSelected="_"
    }
    fun resetNamaSelected(){
        namaSelected="_"
    }

    //refresher
    fun refreshNamaDropdown(){
        val handler= Handler()
        val runnable = Runnable {
            callUpdateNamaDropdown(dbRefMain.child(getTahunSelected()).child(getKelasSelected()))
        }
        handler.postDelayed(runnable,500)
    }
}