package com.example.raportibukrevisi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var tambahmurid_btn:Button
    private lateinit var tambahnilai_btn:Button
    private lateinit var lihatdata_btn:Button
    private lateinit var hapusdata_btn:Button
    private lateinit var exporttugas_btn:FloatingActionButton
    private lateinit var setting_btn:FloatingActionButton
    private var exportDocument=ExportDocument()
    private var dbRef = FirebaseDatabase.getInstance().getReference("DB")

    //preview dan list
    private lateinit var tahunList:ArrayList<String>
    private lateinit var tahunListAdapter:ArrayAdapter<String>
    private lateinit var kelasList:ArrayList<String>
    private lateinit var kelasListAdapter:ArrayAdapter<String>

    //selected item
    private var tahunExportSelected="_"
    private var kelasExportSelected="_"

    private lateinit var inputKepsekDialog:TextInputEditText
    private lateinit var inputTanggalDialog:TextInputEditText
    private lateinit var btnCloseDialog:Button
    private lateinit var btnExportDialog:Button
    private lateinit var kelasDropdown:AutoCompleteTextView
    private lateinit var tahunDropdown:AutoCompleteTextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        callDeklarasi()
        ActivityCompat.requestPermissions(this,exportDocument.permission(),0)

        //permission
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory","com.fasterxml.aalto.stax.InputFactoryImpl")
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory","com.fasterxml.aalto.stax.OutputFactoryImpl")
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory","com.fasterxml.aalto.stax.EventFactoryImpl")

        tambahmurid_btn.setOnClickListener {
            startActivity(Intent(this,TambahMurid::class.java))
        }
        tambahnilai_btn.setOnClickListener {
            startActivity(Intent(this,TambahNilai::class.java))
        }
        lihatdata_btn.setOnClickListener {
            startActivity(Intent(this,LihatData::class.java))
        }
        hapusdata_btn.setOnClickListener {
            startActivity(Intent(this,PreHapus::class.java))
        }
        exporttugas_btn.setOnClickListener {
            openDialogExport()
        }
        setting_btn.setOnClickListener {
            startActivity(Intent(this,SettingKalimat::class.java))
        }
    }

    fun callDeklarasi(){
        tambahmurid_btn = findViewById(R.id.btnTambahMurid)
        tambahnilai_btn = findViewById(R.id.btnTambahNilai)
        lihatdata_btn = findViewById(R.id.btnLihatMurid)
        hapusdata_btn = findViewById(R.id.btnHapusMurid)
        exporttugas_btn = findViewById(R.id.export_button)
        setting_btn = findViewById(R.id.setting_button)
        tahunList= ArrayList()
        kelasList= ArrayList()
    }
    fun openDialogExport(){
        val myLayoutInflater = View.inflate(this,R.layout.export_raport,null)
        val builder = AlertDialog.Builder(this)
        builder.setView(myLayoutInflater)

        val dialog = builder.create()
        dialog.show()
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        //deklarasi
        inputKepsekDialog = myLayoutInflater.findViewById<TextInputEditText>(R.id.kepsek_exporttugas)
        inputTanggalDialog = myLayoutInflater.findViewById<TextInputEditText>(R.id.tanggal_exportraport)
        btnCloseDialog = myLayoutInflater.findViewById<Button>(R.id.close_button_exportraport)
        btnExportDialog = myLayoutInflater.findViewById<Button>(R.id.export_exportraport_btn)
        kelasDropdown = myLayoutInflater.findViewById<AutoCompleteTextView>(R.id.kelas_export_dropdown)
        tahunDropdown = myLayoutInflater.findViewById<AutoCompleteTextView>(R.id.tahun_export_dropdown)

        setTahunPreview()

        //kondisi saat tahun dipilih
        tahunDropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setTahunSelected(parent?.getItemAtPosition(position).toString().replace("-","_"))
                setKelasPreview(kelasDropdown)
            }

        })
        kelasDropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setKelasSelected(parent?.getItemAtPosition(position).toString().lowercase())
            }

        })

        btnCloseDialog.setOnClickListener {
            dialog.dismiss()
        }
        btnExportDialog.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().getReference("DB").child(getTahunSelected()).child(getKelasSelected())
            var namaTmp=""
            var nomorIndukTmp=""

            ref.addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(item:DataSnapshot in snapshot.getChildren()){
                        //mengambil nama dan nomor induk untuk ditampilkan di depan
                        namaTmp = item.child("nama").getValue().toString()
                        nomorIndukTmp = item.child("nomor induk").getValue().toString()

                        if(namaTmp!="null"){
                            exportDocument.export(
                                getTahunSelected()
                                ,getKelasSelected()
                                ,namaTmp
                                ,nomorIndukTmp
                                ,inputTanggalDialog.getText().toString()
                                ,inputKepsekDialog.getText().toString())

                            Toast.makeText(applicationContext,"BERHASIL : Raport "+namaTmp+" Telah disimpan", Toast.LENGTH_SHORT).show()
                        }
                    }
                    ref.removeEventListener(this)
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    //fungsi khusus
    fun setTahunPreview(){
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tahunList.clear()
                for(item: DataSnapshot in snapshot.getChildren()){
                    tahunList.add(item.child("tahun").getValue().toString().replace("_","-"))
                }
                dbRef.removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        tahunListAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, tahunList)
        tahunDropdown.setAdapter(tahunListAdapter)
    }
    fun setKelasPreview(dropdown:AutoCompleteTextView){
        dropdown.setText("pilih kelas")
        dbRef.child(tahunExportSelected).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                kelasList.clear()
                for(item: DataSnapshot in snapshot.getChildren()){
                    if(item.child("kelas").getValue().toString()!="null") {
                        kelasList.add(item.child("kelas").getValue().toString().uppercase())
                    }
                }
                dbRef.child(tahunExportSelected).removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        kelasListAdapter = ArrayAdapter(this,
            R.layout.support_simple_spinner_dropdown_item,kelasList)
        dropdown.setAdapter(kelasListAdapter)
    }

    //setter
    fun setTahunSelected(tahun:String){
        tahunExportSelected=tahun
    }
    fun setKelasSelected(kelas:String){
        kelasExportSelected=kelas
    }

    //getter
    fun getTahunSelected():String{
        return tahunExportSelected
    }
    fun getKelasSelected():String{
        return kelasExportSelected
    }
}