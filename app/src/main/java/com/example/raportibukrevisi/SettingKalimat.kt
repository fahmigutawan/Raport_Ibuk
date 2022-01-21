package com.example.raportibukrevisi

import android.media.audiofx.EnvironmentalReverb
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.FileOutputStream

class SettingKalimat:AppCompatActivity() {
    private lateinit var kelasDropdown:AutoCompleteTextView
    private lateinit var bidangDropdown:AutoCompleteTextView
    private lateinit var btg1Input:TextInputEditText
    private lateinit var btg2Input:TextInputEditText
    private lateinit var btg3Input:TextInputEditText
    private lateinit var btg4Input:TextInputEditText
    private lateinit var setKalimatBtn:Button

    private lateinit var saveFolder:File

    //list dan adapter
    private var listReq = ListRequired()
    private lateinit var kelasAdapter:ArrayAdapter<String>
    private lateinit var bidangAdapter:ArrayAdapter<String>

    //bidang selected
    private var bidangSelected="_"
    private var kelasSelected="_"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setkalimat_activity)

        callDeklarasi()
        callBidangPreview()
        callKelasPreview()
        setSaveFolder()

        //click listener dropdown
        kelasDropdown.setOnItemClickListener(object:AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setKelasSelected(parent?.getItemAtPosition(position).toString().lowercase())
                if(getBidangSelected()!="_"){
                    loadFile()
                }
            }

        })
        bidangDropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setBidangSelected(parent?.getItemAtPosition(position).toString())
                if(getKelasSelected()!="_"){
                    loadFile()
                }
            }

        })

        //jika klik set kalimat
        setKalimatBtn.setOnClickListener {
            saveFile()
            Toast.makeText(applicationContext,"BERHASIL DISIMPAN",Toast.LENGTH_SHORT).show()
        }
    }

    fun callDeklarasi(){
        kelasDropdown=findViewById(R.id.kelas_setkalimat_dropdown)
        bidangDropdown=findViewById(R.id.bidang_setkalimat_dropdown)
        btg1Input=findViewById(R.id.btg1_setkalimat_input)
        btg2Input=findViewById(R.id.btg2_setkalimat_input)
        btg3Input=findViewById(R.id.btg3_setkalimat_input)
        btg4Input=findViewById(R.id.btg4_setkalimat_input)
        setKalimatBtn=findViewById(R.id.setkalimat_btn_final)
    }
    fun callKelasPreview(){
        setKelasSelected("_")
        kelasAdapter= ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,listReq.listKelas())
        kelasDropdown.setAdapter(kelasAdapter)
    }
    fun callBidangPreview(){
        setBidangSelected("_")
        bidangAdapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,listReq.listBidang(this))
        bidangDropdown.setAdapter(bidangAdapter)
    }
    fun loadFile(){
        val fileBtg1 = File(getSaveFolder(),"/"+namaFile("btg1")+".txt")
        val fileBtg2 = File(getSaveFolder(),"/"+namaFile("btg2")+".txt")
        val fileBtg3 = File(getSaveFolder(),"/"+namaFile("btg3")+".txt")
        val fileBtg4 = File(getSaveFolder(),"/"+namaFile("btg4")+".txt")

        if(fileBtg1.exists()){
            val br = fileBtg1.bufferedReader()
            val txtTmp = br.use { it.readText() }
            btg1Input.setText(txtTmp)
        }
        else{
            btg1Input.setText("BELUM DIATUR")
        }

        if(fileBtg2.exists()){
            val br = fileBtg2.bufferedReader()
            val txtTmp = br.use { it.readText() }
            btg2Input.setText(txtTmp)
        }
        else{
            btg2Input.setText("BELUM DIATUR")
        }

        if(fileBtg3.exists()){
            val br = fileBtg3.bufferedReader()
            val txtTmp = br.use { it.readText() }
            btg3Input.setText(txtTmp)
        }
        else{
            btg3Input.setText("BELUM DIATUR")
        }

        if(fileBtg4.exists()){
            val br = fileBtg4.bufferedReader()
            val txtTmp = br.use { it.readText() }
            btg4Input.setText(txtTmp)
        }
        else{
            btg4Input.setText("BELUM DIATUR")
        }
    }
    fun namaFile(star:String):String{
        //nama bidang dijadikan array terdiri dari 2, index pertama sebagai ID
        var arrBidangTmp = getBidangSelected().split(".")

        //format nama file 1A_btg1 (1 mewaliki kode bidang, star 1 mewakili bintang 1
        var nama=String.format(
            "%s%s_%s"
            ,arrBidangTmp.get(0)
            ,getKelasSelected().uppercase()
            ,star
        )

        return nama
    }
    fun saveFile(){
        val outputBtg1 = File(getSaveFolder(),"/"+namaFile("btg1")+".txt")
        val outputBtg2 = File(getSaveFolder(),"/"+namaFile("btg2")+".txt")
        val outputBtg3 = File(getSaveFolder(),"/"+namaFile("btg3")+".txt")
        val outputBtg4 = File(getSaveFolder(),"/"+namaFile("btg4")+".txt")

        if((
                    btg1Input.getText().toString()!=""
                    && btg2Input.getText().toString()!=""
                    && btg3Input.getText().toString()!=""
                    && btg4Input.getText().toString()!=""
                    )||(
                    btg1Input.getText().toString()!="BELUM DIATUR"
                    && btg1Input.getText().toString()!="BELUM DIATUR"
                    && btg1Input.getText().toString()!="BELUM DIATUR"
                    && btg1Input.getText().toString()!="BELUM DIATUR"
                    )) {
            outputBtg1.writeText(btg1Input.getText().toString())
            outputBtg2.writeText(btg2Input.getText().toString())
            outputBtg3.writeText(btg3Input.getText().toString())
            outputBtg4.writeText(btg4Input.getText().toString())
        }
        else{
            Toast.makeText(applicationContext, "Masukkan Kalimat Dengan Lengkap",Toast.LENGTH_SHORT).show()
        }
    }

    //setter
    fun setBidangSelected(bidang:String){
        bidangSelected = bidang
    }
    fun setKelasSelected(kelas:String){
        kelasSelected=kelas
    }
    fun setSaveFolder(){
        val parentFolder = File(Environment.getExternalStorageDirectory(),"Raport")
        if(!parentFolder.isDirectory){
            parentFolder.mkdirs()
        }

        saveFolder = File(parentFolder, "KalimatRaport")
        if(!saveFolder.isDirectory){
            saveFolder.mkdirs()
        }
    }

    //getter
    fun getBidangSelected():String{
        return bidangSelected
    }
    fun getKelasSelected():String{
        return kelasSelected
    }
    fun getSaveFolder():File{
        return saveFolder
    }
}