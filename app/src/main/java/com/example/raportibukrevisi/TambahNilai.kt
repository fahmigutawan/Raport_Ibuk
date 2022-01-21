package com.example.raportibukrevisi

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TambahNilai:AppCompatActivity() {
    private lateinit var tahun_dropdown: AutoCompleteTextView
    private lateinit var tugas_dropdown: AutoCompleteTextView
    private lateinit var nama_dropdown: AutoCompleteTextView
    private lateinit var kelas_dropdown: AutoCompleteTextView
    private lateinit var btg0_radio: RadioButton
    private lateinit var btg1_radio: RadioButton
    private lateinit var btg2_radio: RadioButton
    private lateinit var btg3_radio: RadioButton
    private lateinit var btg4_radio: RadioButton
    private lateinit var submit_button: Button

    private lateinit var tahunAdapter:ArrayAdapter<String>
    private lateinit var kelasAdapter:ArrayAdapter<String>
    private lateinit var namaAdapter: ArrayAdapter<String>
    private lateinit var bidangAdapter: ArrayAdapter<String>

    private var tahunSelected="_"
    private var kelasSelected="_"
    private var namaSelected="_"
    private var bidangSelected="_"
    private var nilaiSelected="_"
    private var recentNilai="_"

    private var isConnected=false
    private var listRequired = ListRequired()
    private var uploadData = UploadData()
    private var dbRef = DbReference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tambahnilai_activity)

        //precall
        callDeklarasi()
        setConnectedState()

        refreshTahunDropdown()
        refreshKelasDropdown()
        refreshBidangDropdown()

        //set pilihan terpilih
        tahun_dropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setTahunSelected(parent?.getItemAtPosition(position).toString().replace("-","_"))
                refreshBidangDropdown()
                refreshKelasDropdown()
                refreshNamaDropdown(getTahunSelected(),getKelasSelected())
                refreshRecentBintang()
            }

        })
        kelas_dropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setKelasSelected(parent?.getItemAtPosition(position).toString().lowercase())
                refreshBidangDropdown()
                refreshNamaDropdown(getTahunSelected(),getKelasSelected())
                refreshRecentBintang()
            }
        })
        tugas_dropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setBidangSelected(parent?.getItemAtPosition(position).toString().replace(".","_"))
                refreshRecentBintang()
            }

        })
        nama_dropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setNamaSelected(parent?.getItemAtPosition(position).toString())
                refreshRecentBintang()
            }

        })

        btg0_radio.setOnClickListener {
            setNilaiSelected("0")
        }
        btg1_radio.setOnClickListener {
            setNilaiSelected("1")
        }
        btg2_radio.setOnClickListener {
            setNilaiSelected("2")
        }
        btg3_radio.setOnClickListener {
            setNilaiSelected("3")
        }
        btg4_radio.setOnClickListener {
            setNilaiSelected("4")
        }

        //submit data
        submit_button.setOnClickListener {
            if(isConnected){
                if(getTahunSelected()!="_"&&getKelasSelected()!="_"&&getNamaSelected()!="_"&&getBidangSelected()!="_"){
                    uploadData(
                        getTahunSelected(),
                        getKelasSelected(),
                        getNamaSelected(),
                        getBidangSelected(),
                        getNilaiSelected()
                    )
                    Toast.makeText(applicationContext,"BERHASIL",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(applicationContext,"Masukkan Semua Data dengan Benar",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(applicationContext,"Periksa Koneksi Anda", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun refreshTahunDropdown(){
        tahun_dropdown.setText("pilih tahun")
        setTahunSelected("_")

        val listTmp = listRequired.listTahun(this)
        tahunAdapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,listTmp)
        tahun_dropdown.setAdapter(tahunAdapter)

    }
    fun refreshKelasDropdown(){
        kelas_dropdown.setText("pilih kelas")
        setKelasSelected("_")
        kelasAdapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,listRequired.listKelas())
        kelas_dropdown.setAdapter(kelasAdapter)
    }
    fun refreshNamaDropdown(tahun:String,kelas:String){
        nama_dropdown.setText("pilih nama")
        setNamaSelected("_")
        namaAdapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,listRequired.listNama(tahun, kelas,this))
        nama_dropdown.setAdapter(namaAdapter)
    }
    fun refreshBidangDropdown(){
        tugas_dropdown.setText("pilih bidang")
        setBidangSelected("_")
        bidangAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, listRequired.listBidang(this))
        tugas_dropdown.setAdapter(bidangAdapter)
    }
    fun refreshRecentBintang(){
        recentNilai="_"
        setRecentNilai()
        setNilaiSelected(getRecentNilai())

        val handler = Handler()
        val runnable = Runnable {
            if(getRecentNilai()=="0"){
                setNilaiSelected("0")
                btg0_radio.setChecked(true)
            }else if(getRecentNilai()=="1"){
                setNilaiSelected("1")
                btg1_radio.setChecked(true)
            }else if(getRecentNilai()=="2"){
                setNilaiSelected("2")
                btg2_radio.setChecked(true)
            }else if(getRecentNilai()=="3"){
                setNilaiSelected("3")
                btg3_radio.setChecked(true)
            }else if(getRecentNilai()=="4"){
                setNilaiSelected("4")
                btg4_radio.setChecked(true)
            }
        }

        handler.postDelayed(runnable,500)
    }

    fun callDeklarasi(){
        tahun_dropdown = findViewById(R.id.tahun_nilai_dropdown)
        tugas_dropdown = findViewById(R.id.tugas_nilai_dropdown)
        nama_dropdown = findViewById(R.id.nama_nilai_dropdown)
        kelas_dropdown = findViewById(R.id.kelas_nilai_dropdown)
        btg0_radio = findViewById(R.id.bintang_0)
        btg1_radio = findViewById(R.id.bintang_1)
        btg2_radio = findViewById(R.id.bintang_2)
        btg3_radio = findViewById(R.id.bintang_3)
        btg4_radio = findViewById(R.id.bintang_4)
        submit_button = findViewById(R.id.submitNilai_nilai_btn)
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
    fun uploadData(tahun:String, kelas:String, nama:String, bidang:String, nilai:String){
        uploadData.nilai(tahun, kelas, nama, bidang, nilai)
    }

    //setter dan getter
    fun setTahunSelected(item:String){
        tahunSelected=item
    }
    fun setKelasSelected(item:String){
        kelasSelected=item
    }
    fun setNamaSelected(item:String){
        namaSelected=item
    }
    fun setBidangSelected(item:String){
        bidangSelected=item
    }
    fun setNilaiSelected(nilai:String){
        nilaiSelected=nilai
    }
    fun setRecentNilai(){
        recentNilai="_"
        val ref = dbRef.refMain().child(getTahunSelected()).child(getKelasSelected()).child(getNamaSelected()).child("tugas").child(getBidangSelected())

        ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                recentNilai = snapshot.child("nilai").getValue().toString()
                Log.e("ASDASD",recentNilai)
                ref.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getTahunSelected():String{
        return tahunSelected
    }
    fun getKelasSelected():String{
        return kelasSelected
    }
    fun getNamaSelected():String{
        return namaSelected
    }
    fun getBidangSelected():String{
        return bidangSelected
    }
    fun getNilaiSelected():String{
        return nilaiSelected
    }
    fun getRecentNilai():String{
        return recentNilai
    }
}