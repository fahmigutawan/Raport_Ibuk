package com.example.raportibukrevisi

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TambahNilai:AppCompatActivity() {
    private lateinit var tahun_dropdown: AutoCompleteTextView
    private lateinit var tugas_dropdown: AutoCompleteTextView
    private lateinit var nama_dropdown: AutoCompleteTextView
    private lateinit var kelas_dropdown: AutoCompleteTextView
    private lateinit var tambahtugasbaru_btn: Button
    private lateinit var btg0_radio: RadioButton
    private lateinit var btg1_radio: RadioButton
    private lateinit var btg2_radio: RadioButton
    private lateinit var btg3_radio: RadioButton
    private lateinit var btg4_radio: RadioButton
    private lateinit var submit_button: Button

    private lateinit var dbRef: DatabaseReference
    private lateinit var tahunList:ArrayList<String>
    private lateinit var tahunListAdapter: ArrayAdapter<String>
    private  var tahunSelected:String="_"
    private lateinit var kelasList:ArrayList<String>
    private lateinit var kelasListAdapter: ArrayAdapter<String>
    private  var kelasSelected:String="_"
    private lateinit var tugasList:ArrayList<String>
    private lateinit var tugasListAdapter: ArrayAdapter<String>
    private var tugasSelected:String="_"
    private lateinit var namaList:ArrayList<String>
    private lateinit var namaListAdapter: ArrayAdapter<String>
    private var namaSelected:String="_"
    private var bintangSelected:String="0"
    private var namaTugas:String="_"
    private lateinit var tugasBaru_uploaded:ArrayList<Boolean>
    private var isConnected=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tambahnilai_activity)

        deklarasi()
        setTahunPreview()
        setConnectedState()
        setRecentBintang()

        //set pilihan terpilih
        tahun_dropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                resetTahunSelecter()
                resetKelasSelected()
                if(isConnected){
                    tahunSelected = parent?.getItemAtPosition(position).toString().replace("-","_")
                    setKelasPreview()
                    if(kelasSelected!="_"){
                        refresherTugasDropdown()
                        refresherNamaDropdown()
                    }

                }else{
                    Toast.makeText(applicationContext,"Harap Cek Kembali Koneksi Anda", Toast.LENGTH_SHORT).show()
                }

            }

        })
        kelas_dropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(isConnected){
                    kelasSelected = parent?.getItemAtPosition(position).toString().lowercase()
                    if(tahunSelected!="_"){
                        refresherTugasDropdown()
                        refresherNamaDropdown()
                    }
                }else{
                    Toast.makeText(applicationContext,"Harap Cek Kembali Koneksi Anda", Toast.LENGTH_SHORT).show()
                }

            }
        })
        tugas_dropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                if(nama_dropdown.getText().toString()=="pilih nama"){
//                    resetNamaSelected()
//                }
                if(isConnected){
                    var tmp = parent?.getItemAtPosition(position).toString().replace(".","_")
                    //var arrTmp = tmp.split(".")
                    tugasSelected=tmp
                    refresherNilaiBintang()
                }else{
                    Toast.makeText(applicationContext,"Harap Cek Kembali Koneksi Anda", Toast.LENGTH_SHORT).show()
                }

            }

        })
        nama_dropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                if(tugas_dropdown.getText().toString()=="pilih nama"){
//                    resetTugasSelected()
//                }
                if(isConnected){
                    namaSelected=parent?.getItemAtPosition(position).toString()
                    refresherNilaiBintang()
                    //tidak perlu refresher tugas, karena bisa jadi user ingin menambahkan nilai murid lain pada tugas yang sama
                }else{
                    Toast.makeText(applicationContext,"Harap Cek Kembali Koneksi Anda", Toast.LENGTH_SHORT).show()
                }
            }

        })

//        //fab tambah tugas baru
//        tambahtugasbaru_btn.setOnClickListener {
//            if(kelasSelected!="_" && tahunSelected!="_"){
//                val myLayoutInflater = View.inflate(this@TambahNilai, R.layout.tambah_tugas_baru,null)
//
//                val builder = AlertDialog.Builder(this@TambahNilai)
//                builder.setView(myLayoutInflater)
//
//                val dialog = builder.create()
//                dialog.show()
//                dialog.setCancelable(false)
//                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//
//                //deklarasi
//                val btnTambah_dialog = myLayoutInflater.findViewById<Button>(R.id.tambahtugasbaru_button)
//                val btnClose_dialog = myLayoutInflater.findViewById<Button>(R.id.close_button_tambahtugas)
//                val inputUrutan_dialog = myLayoutInflater.findViewById<TextInputEditText>(R.id.urutan_tambahtugasbaru_input)
//                val inputNamaTugas_dialog = myLayoutInflater.findViewById<TextInputEditText>(R.id.tambahtugasbaru_input)
//
//                //submit tugas
//                btnTambah_dialog.setOnClickListener {
//                    if(inputUrutan_dialog.getText().toString()!="" && inputNamaTugas_dialog.getText().toString()!=""){
//
//                        setNamaTugas(inputNamaTugas_dialog.getText().toString())
//
//                        //cek apakah id ada
//                        dbRef.child(tahunSelected).child(kelasSelected).child("tugas kelas ini")
//                            .addValueEventListener(object : ValueEventListener
//                            {
//                                override fun onDataChange(snapshot: DataSnapshot) {
//                                    if(snapshot.hasChild(inputUrutan_dialog.getText().toString())){
//                                        Toast.makeText(applicationContext,"Nomor Urut Telah Digunakan, Gunakan Nomor Lain",
//                                            Toast.LENGTH_SHORT).show()
//                                    }
//                                    else{
//                                        if(isConnected){
//                                            //masukkan ke bagian reference DB
//                                            masukkanNamaTugasUser(
//                                                dbRef.child(tahunSelected).child(kelasSelected)
//                                                ,String.format("%s_%s",inputUrutan_dialog.getText().toString(),namaTugas)
//                                                ,inputUrutan_dialog.getText().toString())
//
//                                            //masukkan ke bagian reference KalimatNilaiRaport
//                                            masukkanNamaBidang(
//                                                FirebaseDatabase.getInstance().getReference("KalimatNilaiRaport")
//                                                ,String.format("%s_%s",inputUrutan_dialog.getText().toString(),namaTugas)
//                                                ,inputUrutan_dialog.getText().toString()
//                                                ,kelasSelected)
//
//                                            //tanda kalau berhasil
//                                            Toast.makeText(applicationContext,"Tugas Berhasil Ditambahkan",
//                                                Toast.LENGTH_SHORT).show()
//                                        }else{
//                                            Toast.makeText(applicationContext,"Harap Cek Kembali Koneksi Anda",
//                                                Toast.LENGTH_SHORT).show()
//                                        }
//
//                                    }
//                                    dbRef.child(tahunSelected).child(kelasSelected).child("tugas kelas ini").removeEventListener(this)
//                                }
//                                override fun onCancelled(error: DatabaseError) {
//                                    TODO("Not yet implemented")
//                                }
//                            })
//                    }
//                    else{
//                        Toast.makeText(applicationContext,"Masukkan Nama Tugas dan Urutan Dengan Benar",
//                            Toast.LENGTH_SHORT).show()
//                    }
//                }
//                btnClose_dialog.setOnClickListener {
//                    dialog.dismiss()
//                    refresherTugasDropdown()
//                    refresherNilaiBintang()
//                }
//            }
//            else{
//                Toast.makeText(applicationContext,"Harap Isi Kelas dan Tahun Dengan Benar", Toast.LENGTH_SHORT).show()
//            }
//        }

        //cek bintang dipilih
        btg0_radio.setOnClickListener {
            setBintangSelected("0")
        }
        btg1_radio.setOnClickListener {
            setBintangSelected("1")
        }
        btg2_radio.setOnClickListener {
            setBintangSelected("2")
        }
        btg3_radio.setOnClickListener {
            setBintangSelected("3")
        }
        btg4_radio.setOnClickListener {
            setBintangSelected("4")
        }

        //upload nilai
        submit_button.setOnClickListener {
            if(isConnected){
                uploadNilai()
            }else{
                Toast.makeText(applicationContext,"Pastikan Koneksi Anda Baik", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //fungsi setter
    fun setTahunPreview(){
        tahunSelected="_"
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tahunList.clear()
                for(item: DataSnapshot in snapshot.getChildren()){
                    tahunList.add(item.child("tahun").getValue().toString().replace("_","-"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        tahunListAdapter = ArrayAdapter(this,
            R.layout.support_simple_spinner_dropdown_item, tahunList)
        tahun_dropdown.setAdapter(tahunListAdapter)
    }
    fun setKelasPreview(){
        kelasSelected="_"
        kelas_dropdown.setText("pilih kelas")
        dbRef.child(tahunSelected).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                kelasList.clear()
                for(item: DataSnapshot in snapshot.getChildren()){
                    if(item.child("kelas").getValue().toString()!="null") {
                        kelasList.add(item.child("kelas").getValue().toString().uppercase())
                    }
                }
                dbRef.child(tahunSelected).removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        kelasListAdapter = ArrayAdapter(this,
            R.layout.support_simple_spinner_dropdown_item,kelasList)
        kelas_dropdown.setAdapter(kelasListAdapter)
    }
    fun setTugasDropdown(){
        //set tugas dropdown list
        tugasSelected="_"
        if (tahunSelected=="_" || kelasSelected=="_"){
            tugas_dropdown.setText("pilih bidang")
        }
        else{
            tugas_dropdown.setText("pilih bidang")
                FirebaseDatabase.getInstance()
                    .getReference("KalimatNilaiRaport")
                    .child(kelasSelected)
                    .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        tugasList.clear()
                        for(item: DataSnapshot in snapshot.getChildren()){
                            var tmp = item.child("nama bidang").getValue().toString().replace("_",".")

                            tugasList.add(tmp)
                        }
                        FirebaseDatabase.getInstance()
                            .getReference("KalimatNilaiRaport")
                            .child(kelasSelected).removeEventListener(this)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            tugasListAdapter = ArrayAdapter(this,
                R.layout.support_simple_spinner_dropdown_item,tugasList)
            tugas_dropdown.setAdapter(tugasListAdapter)
        }
    }
    fun setNamaDropdown(){
        namaSelected="_"
        namaList.clear()
        if(tahunSelected=="_" || kelasSelected=="_"){
            nama_dropdown.setText("pilih nama")
        }else{
            nama_dropdown.setText("pilih nama")

            dbRef.child(tahunSelected)
                .child(kelasSelected)
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (item: DataSnapshot in snapshot.getChildren()){
                            var tmp = item.child("nama").getValue().toString()
                            if (tmp!="null"){
                                namaList.add(item.child("nama").getValue().toString())
                            }
                        }
                        dbRef.child(tahunSelected)
                            .child(kelasSelected)
                            .removeEventListener(this)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            namaListAdapter = ArrayAdapter(this,
                R.layout.support_simple_spinner_dropdown_item,namaList)
            nama_dropdown.setAdapter(namaListAdapter)
        }
    }
    fun setNamaTugas(namatugas:String){
        namaTugas = namatugas
    }
    fun setRecentBintang(){
        dbRef
            .child(tahunSelected)
            .child(kelasSelected)
            .child(namaSelected)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child("tugas").child(tugasSelected).child("nilai").getValue().toString()=="0"){
                        btg0_radio.isChecked=true
                    }
                    if(snapshot.child("tugas").child(tugasSelected).child("nilai").getValue().toString()=="1"){
                        btg1_radio.isChecked=true
                    }
                    if(snapshot.child("tugas").child(tugasSelected).child("nilai").getValue().toString()=="2"){
                        btg2_radio.isChecked=true
                    }
                    if(snapshot.child("tugas").child(tugasSelected).child("nilai").getValue().toString()=="3"){
                        btg3_radio.isChecked=true
                    }
                    if(snapshot.child("tugas").child(tugasSelected).child("nilai").getValue().toString()=="4"){
                        btg4_radio.isChecked=true
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
    fun setBintangSelected(bintang:String){
        bintangSelected=bintang
    }

    //fungsi penting
    fun masukkanNamaTugasUser(ref:DatabaseReference,namaTugas:String,idTugas:String){
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item: DataSnapshot in snapshot.getChildren()){
                    var nama = item.child("nama").getValue().toString()
                    if(nama!="null"){
                        ref.child(nama).child("tugas").child(namaTugas).child("nama tugas").setValue(namaTugas)
                        ref.child(nama).child("tugas").child(namaTugas).child("nilai").setValue("0")
                    }
                }

                //set preview
                ref.child("tugas kelas ini").child(idTugas).child("nama tugas").setValue(namaTugas)
                ref.child("tugas kelas ini").child(idTugas).child("id tugas").setValue(idTugas)
                ref.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    fun masukkanNamaBidang(ref:DatabaseReference, namaBidang:String, idTugas:String, kelas:String){
        ref.child(kelas).child(idTugas).child("nama bidang").setValue(namaBidang)
        ref.child(kelas).child(idTugas).child("btg1").setValue("-")
        ref.child(kelas).child(idTugas).child("btg2").setValue("-")
        ref.child(kelas).child(idTugas).child("btg3").setValue("-")
        ref.child(kelas).child(idTugas).child("btg4").setValue("-")
    }
    fun uploadNilai(){
        if(
            tahunSelected=="_"||
            kelasSelected=="_"||
            tugasSelected=="_"||
            namaSelected=="_"||
            bintangSelected=="_"
        ){
            Toast.makeText(applicationContext,"Harap Isi Semua Data Dengan Lengkap", Toast.LENGTH_SHORT).show()
        }else{
            dbRef.child(tahunSelected).child(kelasSelected).child(namaSelected).child("tugas").child(tugasSelected).child("nilai")
                .setValue(bintangSelected).addOnSuccessListener {
                    Toast.makeText(applicationContext,"Nilai Berhasil Diupload", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(applicationContext,"Terjadi Kesalahan, Coba Lagi Nanti", Toast.LENGTH_SHORT).show()
                }
        }
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

    //refresher
    fun refresherTugasDropdown(){
        val timerHandler: Handler = Handler()

        tugas_dropdown.isClickable=false
        setTugasDropdown()
        val runnable:Runnable = Runnable{
            tugas_dropdown.isClickable=true
        }
        timerHandler.postDelayed(runnable,100)
    }
    fun refresherNamaDropdown(){
        //deklarasi refresher
        val timerHandler: Handler = Handler()
        val runnable:Runnable = Runnable{
            setNamaDropdown()
        }
        timerHandler.postDelayed(runnable,200)
    }
    fun refresherNilaiBintang(){
        val handler = Handler()
        val runnable = Runnable {
            setRecentBintang()
        }

        handler.postDelayed(runnable,200)
    }

    //fungsi deklarasi
    private fun deklarasi(){
        tahun_dropdown = findViewById(R.id.tahun_nilai_dropdown)
        tugas_dropdown = findViewById(R.id.tugas_nilai_dropdown)
        nama_dropdown = findViewById(R.id.nama_nilai_dropdown)
        kelas_dropdown = findViewById(R.id.kelas_nilai_dropdown)
        tambahtugasbaru_btn = findViewById(R.id.addtambahtugas_btn)
        submit_button = findViewById(R.id.submitNilai_nilai_btn)
        btg0_radio = findViewById(R.id.bintang_0)
        btg1_radio = findViewById(R.id.bintang_1)
        btg2_radio = findViewById(R.id.bintang_2)
        btg3_radio = findViewById(R.id.bintang_3)
        btg4_radio = findViewById(R.id.bintang_4)

        dbRef = FirebaseDatabase.getInstance().getReference("DB")
        tahunList = ArrayList()
        kelasList = ArrayList()
        tugasList = ArrayList()
        namaList = ArrayList()
        tugasBaru_uploaded = ArrayList()
        tugasBaru_uploaded.add(true)
    }

    //resetter
    fun resetTahunSelecter(){
        tahunSelected="_"
    }
    fun resetKelasSelected(){
        kelasSelected="_"
    }
    fun resetTugasSelected(){
        tugasSelected="_"
    }
    fun resetNamaSelected(){
        namaSelected="_"
    }
}