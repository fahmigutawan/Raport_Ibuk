package com.example.raportibukrevisi

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HapusTugas:AppCompatActivity() {
    private lateinit var tahun_hapustugas_dropdown: AutoCompleteTextView
    private lateinit var kelas_hapustugas_dropdown: AutoCompleteTextView
    private lateinit var tugas_hapustugas_dropdown: AutoCompleteTextView
    private lateinit var hapus_hapustugas_btn: Button

    //list dan adapter
    private lateinit var tahunList:ArrayList<String>
    private lateinit var tahunAdapter: ArrayAdapter<String>
    private lateinit var kelasList:ArrayList<String>
    private lateinit var kelasAdapter: ArrayAdapter<String>
    private lateinit var tugasList:ArrayList<String>
    private lateinit var tugasAdapter: ArrayAdapter<String>

    //dbref
    private lateinit var dbRefMain: DatabaseReference

    //tahun & kelas selected
    private var tahunSelected="_"
    private var kelasSelected="_"
    private var tugasSelected="_"
    private var idTugas="_"

    //connection state
    private var isConnected=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hapustugas_activity)

        callDeklarasi()
        callUpdateTahunDropdown(dbRefMain)
        callListenerConnectionState()

        //cek jika item dipilih
        tahun_hapustugas_dropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener {
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
                    callUpdateTugasDropdown(dbRefMain.child(getTahunSelected()).child(getKelasSelected()).child("tugas kelas ini"))
                }
            }

        })
        kelas_hapustugas_dropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //set kelasselected
                setKelasSelected(parent?.getItemAtPosition(position).toString())
                if(getTahunSelected()!="_"){
                    callUpdateTugasDropdown(dbRefMain.child(getTahunSelected()).child(getKelasSelected()).child("tugas kelas ini"))
                }
            }

        })
        tugas_hapustugas_dropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setTugasSelected(parent?.getItemAtPosition(position).toString().replace(".","_"))
                Log.d("ppp",getTugasSelected())
                setIdTugas()
            }

        })

        hapus_hapustugas_btn.setOnClickListener {
            if(isConnected){
                if(getTahunSelected()!="_" && getKelasSelected()!="_" && getTugasSelected()!="_"){
                    removeData(dbRefMain.child(getTahunSelected()).child(getKelasSelected()))
                    refreshTugasDropdown()
                    Toast.makeText(applicationContext,"Tugas Berhasil Dihapus", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext,"Harap Masukkan Data Dengan lengkap", Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(applicationContext,"Pastikan Koneksi Anda Baik", Toast.LENGTH_SHORT).show()
            }



        }
    }

    //fungsi khusus
    fun callDeklarasi(){
        tahun_hapustugas_dropdown = findViewById(R.id.tahun_hapustugas_dropdown)
        kelas_hapustugas_dropdown = findViewById(R.id.kelas_hapustugas_dropdown)
        tugas_hapustugas_dropdown = findViewById(R.id.tugas_hapustugas_dropdown)
        hapus_hapustugas_btn = findViewById(R.id.hapus_hapustugas_btn)

        //list
        tahunList = ArrayList()
        kelasList = ArrayList()
        tugasList = ArrayList()
        dbRefMain = FirebaseDatabase.getInstance().getReference("DB")
    }
    fun callUpdateTahunDropdown(ref:DatabaseReference){
        tahun_hapustugas_dropdown.setText("pilih tahun")
        resetKelasSelected()
        resetTahunSelected()
        resetTugasSelected()

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
        tahun_hapustugas_dropdown.setAdapter(tahunAdapter)
    }
    fun callUpdateKelasDropdown(ref:DatabaseReference){
        kelas_hapustugas_dropdown.setText("pilih kelas")
        resetKelasSelected()
        resetTugasSelected()

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
        kelas_hapustugas_dropdown.setAdapter(kelasAdapter)
    }
    fun callUpdateTugasDropdown(ref:DatabaseReference){
        tugas_hapustugas_dropdown.setText("pilih tugas")
        resetTugasSelected()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tugasList.clear()
                for(item: DataSnapshot in snapshot.getChildren()){
                    if(item.child("nama tugas").getValue().toString()!="null"){
                        tugasList.add(item.child("nama tugas").getValue().toString().replace("_","."))
                    }
                }
                ref.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        tugasAdapter = ArrayAdapter(this,
            R.layout.support_simple_spinner_dropdown_item,tugasList)
        tugas_hapustugas_dropdown.setAdapter(tugasAdapter)
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
    fun removeData(refKelas:DatabaseReference){
        refKelas.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item: DataSnapshot in snapshot.getChildren()){
                    var tmp_nama = item.child("nama").getValue().toString()

                    //hapus tugas dalam child nama yang terpilih
                    refKelas.child(tmp_nama).child("tugas").child(getTugasSelected()).removeValue()
                }
                //hapus idtugas dalam child tugas kelas ini
                refKelas.child("tugas kelas ini").child(getIdTugas()).removeValue()
                FirebaseDatabase.getInstance().getReference("KalimatNilaiRaport").child(getKelasSelected()).child(getIdTugas()).removeValue()
                refKelas.removeEventListener(this)
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
    fun setTugasSelected(nama:String){
        tugasSelected=nama
    }
    fun setIdTugas(){
        var tmp = getTugasSelected().split("_")
        idTugas = tmp.get(0)
    }

    //getter
    fun getTahunSelected():String{
        return tahunSelected
    }
    fun getKelasSelected():String{
        return kelasSelected
    }
    fun getTugasSelected():String{
        return tugasSelected
    }
    fun getIdTugas():String{
        return idTugas
    }

    //resertter
    fun resetTahunSelected(){
        tahunSelected="_"
    }
    fun resetKelasSelected(){
        kelasSelected="_"
    }
    fun resetTugasSelected(){
        tugasSelected="_"
    }

    //refresher
    fun refreshTugasDropdown(){
        val handler = Handler()
        val runnable= Runnable {
            callUpdateTugasDropdown(dbRefMain.child(getTahunSelected()).child(getKelasSelected()).child("tugas kelas ini"))
        }
        handler.postDelayed(runnable,500)
    }

}