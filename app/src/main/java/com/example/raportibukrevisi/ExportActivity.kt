package com.example.raportibukrevisi

import android.app.AlertDialog
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.File

class ExportActivity:AppCompatActivity() {
    private lateinit var tahunDropdown:AutoCompleteTextView
    private lateinit var kelasDropdown:AutoCompleteTextView
    private lateinit var tanggalEditText: TextInputEditText
    private lateinit var kepsekEditText: TextInputEditText
    private lateinit var semesterEditText: TextInputEditText
    private lateinit var nipkepsekEditText:TextInputEditText
    private lateinit var exportButton:Button

    private lateinit var tahunAdapter:ArrayAdapter<String>
    private lateinit var kelasAdapter:ArrayAdapter<String>

    private var tahunSelected="_"
    private var kelasSelected="_"

    private var listReq = ListRequired()
    private var exportDoc = ExportDocument()
    private var dbRef = DbReference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.export_activity)

        //pertama kali melakukan deklarasi
        callDeklarasi()
        callTahunPreview()
        callKelasPreview()

        tahunDropdown.setOnItemClickListener(object:AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setTahunSelected(parent?.getItemAtPosition(position).toString().replace("-","_"))
                callKelasPreview()
            }

        })
        kelasDropdown.setOnItemClickListener(object:AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setKelasSelected(parent?.getItemAtPosition(position).toString().lowercase())
            }

        })

        exportButton.setOnClickListener {
            export()
        }
    }
    //fungsi spesial
    fun export(){
        //parameter untuk export tahun, kelas, nama, nis, tanggal, kepsek, nilai(ARRAY), semester
        val tahun = getTahunSelected()
        val kelas = getKelasSelected()
        val tanggal = tanggalEditText.text.toString()
        val kepsek = kepsekEditText.text.toString()
        val semester = semesterEditText.text.toString()
        val nilai = ArrayList<String>()
        val ref = dbRef.refMain().child(tahun).child(kelas)
        val nipKepsek = nipkepsekEditText.text.toString()

        //menampilkan dialogalert loading
        val layoutinflater = View.inflate(this@ExportActivity,R.layout.loading_dialogview,null)
        val builder = AlertDialog.Builder(this@ExportActivity)
        builder.setView(layoutinflater)

        val dialog = builder.create()
        dialog.show()
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        //proses export dengan mengambil data dari firebase
        ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val runExport= Runnable {
                    for(item:DataSnapshot in snapshot.getChildren()){
                        nilai.clear()
                        //proses memasukkan semua tugas dalam satu user ke dalam array nilai
                        for(itemNilai:DataSnapshot in item.child("tugas").getChildren()){
                            nilai.add(itemNilai.child("nilai").getValue().toString())
                        }
                        val nama = item.child("nama").getValue().toString()

                        //export karena sudah ada semua, menggunakan handler agar layar tidak freeze
                        if(item.child("nama").getValue().toString()!="null" && item.child("nomor induk").getValue().toString()!="null") {
                            exportDoc.export(
                                tahun,
                                kelas,
                                item.child("nama").getValue().toString(),
                                item.child("nomor induk").getValue().toString(),
                                tanggal,
                                kepsek,
                                nilai,
                                semester,
                                nipKepsek
                            )
                            val parentFolder = File(Environment.getExternalStorageDirectory(),"Raport")
                            val file = File(parentFolder,nama+".docx")

                            if(file.exists()){
                                Toast.makeText(applicationContext,"Raport "+nama+" Berhasil Disimpan",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    dialog.dismiss()
                }

                val thread = Thread(runExport)
                thread.run()

                ref.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //pre call
    fun callDeklarasi(){
        tahunDropdown=findViewById(R.id.tahun_export_dropdown)
        kelasDropdown=findViewById(R.id.kelas_export_dropdown)
        tanggalEditText=findViewById(R.id.tanggal_exportraport)
        kepsekEditText=findViewById(R.id.kepsek_exporttugas)
        semesterEditText=findViewById(R.id.semester_exporttugas)
        exportButton=findViewById(R.id.export_exportraport_btn)
        nipkepsekEditText=findViewById(R.id.nipkepsek_exporttugas)
    }
    fun callTahunPreview(){
        setTahunSelected("_")
        tahunAdapter= ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,listReq.listTahun(this))
        tahunDropdown.setText("pilih tahun")
        tahunDropdown.setAdapter(tahunAdapter)
    }
    fun callKelasPreview(){
        setKelasSelected("_")
        kelasAdapter= ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,listReq.listKelas())
        kelasDropdown.setText("pilih kelas")
        kelasDropdown.setAdapter(kelasAdapter)
    }

    //setter
    fun setTahunSelected(tahun:String){
        tahunSelected=tahun
    }
    fun setKelasSelected(kelas:String){
        kelasSelected=kelas
    }

    //getter
    fun getTahunSelected():String{
        return tahunSelected
    }
    fun getKelasSelected():String{
        return kelasSelected
    }
}