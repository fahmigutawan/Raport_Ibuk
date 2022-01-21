package com.example.raportibukrevisi.notUsed

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.raportibukrevisi.HapusMurid
import com.example.raportibukrevisi.R

class PreHapus:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.prehapus_activity)

        val goto_hapusmurid_btn = findViewById<Button>(R.id.goto_hapusmurid)
        val goto_hapustugas_btn = findViewById<Button>(R.id.goto_hapustugas)

        goto_hapusmurid_btn.setOnClickListener {
            startActivity(Intent(this, HapusMurid::class.java))
        }
        goto_hapustugas_btn.setOnClickListener {
            startActivity(Intent(this, HapusTugas::class.java))
        }
    }
}