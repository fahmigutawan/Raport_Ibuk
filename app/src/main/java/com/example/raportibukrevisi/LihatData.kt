package com.example.raportibukrevisi

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class LihatData:AppCompatActivity() {
    private lateinit var tahun_lihatdata_dropdown: AutoCompleteTextView
    private lateinit var nama_lihatdata_dropdown: AutoCompleteTextView
    private lateinit var kelas_lihatdata_dropdown: AutoCompleteTextView
    private lateinit var lihat_lihatdata_button: Button
    private lateinit var nama_scroll: TextView
    private lateinit var kelas_scroll: TextView
    private lateinit var nomorinduk_scroll: TextView
    private lateinit var tugas_scroll: TextView

    //data selected
    private var tahunSelected:String="_"
    private var namaSelected:String="_"
    private var kelasSelected:String="_"
    private var nomorIndukSelected:String="_"
    private var allTugasSelected:String=""//biar kosong karena akan ditambah tanda "-" saat pertama kali diisi

    //list dan adapter preview
    private lateinit var tahunList:ArrayList<String>
    private lateinit var tahunAdapter: ArrayAdapter<String>
    private lateinit var kelasList:ArrayList<String>
    private lateinit var kelasAdapter: ArrayAdapter<String>
    private lateinit var namaList:ArrayList<String>
    private lateinit var namaAdapter: ArrayAdapter<String>

    //reference
    private lateinit var dbRefMain: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lihatmurid_activity)
        deklarasi()
        setKelasPreview()
        setTahunPreview()

        //dropdown selected
        tahun_lihatdata_dropdown.setOnItemClickListener(object: AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //set tahunselected
                setTahunSelected(
                    parent?.getItemAtPosition(position).toString().replace("-","_")
                )

                if(getKelasSelected()!="_"){
                    resetTextView()
                    resetNamaSelected()
                    setNamaDropdown()
                }
            }

        })
        kelas_lihatdata_dropdown.setOnItemClickListener(object: AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //set kelas selected
                setKelasSelected(
                    parent?.getItemAtPosition(position).toString().lowercase()
                )

                if(getTahunSelected()!="_"){
                    resetTextView()
                    resetNamaSelected()
                    setNamaDropdown()
                }
            }

        })
        nama_lihatdata_dropdown.setOnItemClickListener(object: AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //set nama selected
                resetTextView()
                setNamaSelected(
                    parent?.getItemAtPosition(position).toString()
                )
                setAllTugasSelected()
                setNomorIndukSelected()
            }

        })

        //jika tombol lihat diklik
        lihat_lihatdata_button.setOnClickListener {
            if(tahunSelected!="_" && kelasSelected!="_" && namaSelected!="_"){
                setTextView()
            }
            else{
                resetTextView()
                Toast.makeText(applicationContext,"Harap Masukkan Data Dengan Lengkap", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //fungsi deklarasi
    fun deklarasi(){
        tahun_lihatdata_dropdown = findViewById(R.id.tahun_lihatdata_dropdown)
        nama_lihatdata_dropdown = findViewById(R.id.nama_lihatdata_dropdown)
        kelas_lihatdata_dropdown = findViewById(R.id.kelas_lihatdata_dropdown)
        lihat_lihatdata_button = findViewById(R.id.lihat_lihatdata_button)
        nama_scroll = findViewById(R.id.nama_scroll)
        kelas_scroll = findViewById(R.id.kelas_scroll)
        nomorinduk_scroll = findViewById(R.id.nomorinduk_scroll)
        tugas_scroll = findViewById(R.id.namatugas_scroll)

        dbRefMain = FirebaseDatabase.getInstance().getReference("DB")
        tahunList=ArrayList()
        kelasList=ArrayList()
        namaList= ArrayList()
    }

    //fungsi khusus
    fun setTextView(){
        nama_scroll.setText(getNamaSelected())
        kelas_scroll.setText(getKelasSelected())
        nomorinduk_scroll.setText(getNomorIndukSelected())
        tugas_scroll.setText(getAllTugasSelected())
    }
    fun resetNamaSelected(){
        namaSelected="_"
    }
    fun resetTextView(){
        nama_scroll.setText("")
        kelas_scroll.setText("")
        nomorinduk_scroll.setText("")
        tugas_scroll.setText("")
    }

    //setter
    fun setTahunPreview(){
        dbRefMain.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item: DataSnapshot in snapshot.getChildren()){
                    tahunList.add(item.child("tahun").getValue().toString().replace("_","-"))
                }
                dbRefMain.removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        tahunAdapter = ArrayAdapter(this,
            R.layout.support_simple_spinner_dropdown_item, tahunList)
        tahun_lihatdata_dropdown.setAdapter(tahunAdapter)
    }
    fun setKelasPreview(){
        kelasList.add("A")
        kelasList.add("B")

        kelasAdapter = ArrayAdapter(this,
            R.layout.support_simple_spinner_dropdown_item, kelasList)
        kelas_lihatdata_dropdown.setAdapter(kelasAdapter)
    }
    fun setNamaDropdown(){
        namaList.clear()
        if(tahunSelected=="_" || kelasSelected=="_"){
            nama_lihatdata_dropdown.setText("pilih nama")
        }else{
            nama_lihatdata_dropdown.setText("pilih nama")

            dbRefMain.child(tahunSelected)
                .child(kelasSelected).addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (item: DataSnapshot in snapshot.getChildren()){
                            var tmp = item.child("nama").getValue().toString()
                            if (tmp!="null"){
                                namaList.add(item.child("nama").getValue().toString())
                            }
                        }
                        dbRefMain.child(tahunSelected)
                            .child(kelasSelected).removeEventListener(this)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            namaAdapter = ArrayAdapter(this,
                R.layout.support_simple_spinner_dropdown_item,namaList)
            nama_lihatdata_dropdown.setAdapter(namaAdapter)
        }
    }
    fun setTahunSelected(tahun:String){
        tahunSelected=tahun
    }
    fun setKelasSelected(kelas:String){
        kelasSelected=kelas
    }
    fun setNamaSelected(nama:String){
        namaSelected=nama
    }
    fun setAllTugasSelected(){
        dbRefMain.child(getTahunSelected())
            .child(getKelasSelected())
            .child(getNamaSelected())
            .child("tugas").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    allTugasSelected=""
                    for(item: DataSnapshot in snapshot.getChildren()){
                        var tmp_namatugas = item.child("nama tugas").getValue().toString().replace("_",". ")
                        var tmp_nilai = item.child("nilai").getValue().toString()

                        allTugasSelected=allTugasSelected+tmp_namatugas+"\n"
                        allTugasSelected=allTugasSelected+"\t\tNILAI : "+tmp_nilai+"\n\n"
                    }

                    dbRefMain.child(getTahunSelected())
                        .child(getKelasSelected())
                        .child(getNamaSelected())
                        .child("tugas").removeEventListener(this)
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
    fun setNomorIndukSelected(){
        dbRefMain
            .child(tahunSelected)
            .child(kelasSelected)
            .child(namaSelected).addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    nomorIndukSelected=snapshot.child("nomor induk").getValue().toString()

                    dbRefMain
                        .child(tahunSelected)
                        .child(kelasSelected)
                        .child(namaSelected).removeEventListener(this)
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
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
    fun getAllTugasSelected():String{
        return allTugasSelected
    }
    fun getNomorIndukSelected():String{
        return nomorIndukSelected
    }
}