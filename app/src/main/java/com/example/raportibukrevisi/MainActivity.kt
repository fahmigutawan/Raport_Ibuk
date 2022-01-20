package com.example.raportibukrevisi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
    private lateinit var tambahmurid_btn: Button
    private lateinit var tambahnilai_btn: Button
    private lateinit var lihatdata_btn: Button
    private lateinit var hapusdata_btn: Button
    private lateinit var exporttugas_btn: FloatingActionButton
    private lateinit var setting_btn: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        callDeklarasi()
        ActivityCompat.requestPermissions(this, permission(), 0)
        callSetProperty()

        tambahmurid_btn.setOnClickListener {
            startActivity(Intent(this, TambahMurid::class.java))
        }
        tambahnilai_btn.setOnClickListener {
            startActivity(Intent(this, TambahNilai::class.java))
        }
        lihatdata_btn.setOnClickListener {
            startActivity(Intent(this, LihatData::class.java))
        }
        hapusdata_btn.setOnClickListener {
            startActivity(Intent(this, HapusMurid::class.java))
        }
        exporttugas_btn.setOnClickListener {
            startActivity(Intent(this,ExportActivity::class.java))
        }
        setting_btn.setOnClickListener {
            startActivity(Intent(this, SettingKalimat::class.java))
        }
    }

    fun callDeklarasi() {
        tambahmurid_btn = findViewById(R.id.btnTambahMurid)
        tambahnilai_btn = findViewById(R.id.btnTambahNilai)
        lihatdata_btn = findViewById(R.id.btnLihatMurid)
        hapusdata_btn = findViewById(R.id.btnHapusMurid)
        exporttugas_btn = findViewById(R.id.export_button)
        setting_btn = findViewById(R.id.setting_button)
    }
    fun callSetProperty(){
        //permission
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLInputFactory",
            "com.fasterxml.aalto.stax.InputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLOutputFactory",
            "com.fasterxml.aalto.stax.OutputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLEventFactory",
            "com.fasterxml.aalto.stax.EventFactoryImpl"
        )
    }
    fun permission():Array<String>{
        return arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}
