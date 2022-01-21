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
    private lateinit var hapus_hapusmurid_btn:Button

    private var listReq = ListRequired()
    private var removeData = RemoveData()

    private lateinit var tahunAdapter:ArrayAdapter<String>
    private lateinit var kelasAdapter:ArrayAdapter<String>
    private lateinit var namaAdapter:ArrayAdapter<String>

    private var tahunSelected="_"
    private var kelasSelected="_"
    private var namaSelected="_"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hapusmurid_activity)

        callDeklarasi()
        refreshTahunDropdown()
        refreshKelasDropdown()

        //dropdown item selected
        tahun_hapusmurid_dropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setTahunSelected(parent?.getItemAtPosition(position).toString().replace("-","_"))
                refreshKelasDropdown()
                refreshnamaDropdown()
            }

        })
        kelas_hapusmurid_dropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setKelasSelected(parent?.getItemAtPosition(position).toString().lowercase())
                refreshnamaDropdown()
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

        //hapus item
        hapus_hapusmurid_btn.setOnClickListener {
            if(getTahunSelected()!="_" && getKelasSelected()!="_" && getNamaSelected()!="_"){
                removeMurid()
            }
            else{
                Toast.makeText(applicationContext,"Harap Masukkan Data Dengan Lengkap",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun callDeklarasi(){
        tahun_hapusmurid_dropdown=findViewById(R.id.tahun_hapusmurid_dropdown)
        kelas_hapusmurid_dropdown=findViewById(R.id.kelas_hapusmurid_dropdown)
        nama_hapusmurid_dropdown=findViewById(R.id.nama_hapusmurid_dropdown)
        hapus_hapusmurid_btn=findViewById(R.id.hapus_hapusmurid_btn)
    }

    fun refreshTahunDropdown(){
        tahun_hapusmurid_dropdown.setText("pilih tahun")
        setTahunSelected("_")
        tahunAdapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,listReq.listTahun(this))
        tahun_hapusmurid_dropdown.setAdapter(tahunAdapter)
    }
    fun refreshKelasDropdown(){
        kelas_hapusmurid_dropdown.setText("pilih kelas")
        setKelasSelected("_")
        kelasAdapter= ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,listReq.listKelas())
        kelas_hapusmurid_dropdown.setAdapter(kelasAdapter)
    }
    fun refreshnamaDropdown(){
        nama_hapusmurid_dropdown.setText("pilih nama")
        setNamaSelected("_")
        namaAdapter= ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,listReq.listNama(getTahunSelected(),getKelasSelected(),this))
        nama_hapusmurid_dropdown.setAdapter(namaAdapter)
    }

    fun setTahunSelected(tahun:String){tahunSelected=tahun}
    fun setKelasSelected(kelas:String){kelasSelected=kelas}
    fun setNamaSelected(nama:String){namaSelected=nama}

    fun getTahunSelected():String{return tahunSelected}
    fun getKelasSelected():String{return kelasSelected}
    fun getNamaSelected():String{return namaSelected}

    fun removeMurid(){
        removeData.removeMurid(getTahunSelected(),getKelasSelected(),getNamaSelected())
    }
}