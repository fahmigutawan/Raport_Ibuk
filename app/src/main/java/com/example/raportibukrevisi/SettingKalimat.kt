package com.example.raportibukrevisi

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SettingKalimat:AppCompatActivity() {
    private lateinit var kelasDropdown:AutoCompleteTextView
    private lateinit var bidangDropdown:AutoCompleteTextView
    private lateinit var btg1Input:TextInputEditText
    private lateinit var btg2Input:TextInputEditText
    private lateinit var btg3Input:TextInputEditText
    private lateinit var btg4Input:TextInputEditText
    private lateinit var setKalimatBtn:Button

    //list dan adapter
    private var bidangList=ArrayList<String>()
    private lateinit var bidangAdapter:ArrayAdapter<String>

    //bidang selected
    private var bidangSelected="_"
    private var kelasSelected="_"
    private var idBidangSelected="_"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setkalimat_activity)

        callDeklarasi()
        callBidangPreview()
        callKelasPreview()

        //click listener dropdown
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
        bidangDropdown.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setBidangSelected(parent?.getItemAtPosition(position).toString())
                setIdBidangSelected(getBidangSelected())
                updateRecentKalimat(getKelasSelected(),getIdBidangSelected())
            }

        })

        //jika klik set kalimat
        setKalimatBtn.setOnClickListener {
            //upload kalimat btg 1
            uploadKalimat(getKelasSelected(),getIdBidangSelected(),btg1Input,"btg1")

            //upload kalimat btg 2
            uploadKalimat(getKelasSelected(),getIdBidangSelected(),btg2Input,"btg2")

            //upload kalimat btg 3
            uploadKalimat(getKelasSelected(),getIdBidangSelected(),btg3Input,"btg3")

            //upload kalimat btg 4
            uploadKalimat(getKelasSelected(),getIdBidangSelected(),btg4Input,"btg4")
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
        var kelasList = ArrayList<String>()
        kelasList.add("A")
        kelasList.add("B")

        var kelasAdapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,kelasList)
        kelasDropdown.setAdapter(kelasAdapter)

    }
    fun callBidangPreview(){
        FirebaseDatabase.getInstance()
            .getReference("KalimatNilaiRaport")
            .child("a")
            .addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                bidangList.clear()
                for(item:DataSnapshot in snapshot.getChildren()){
                    bidangList.add(item.child("nama bidang").getValue().toString().replace("_","."))
                }

                //remove
                FirebaseDatabase.getInstance()
                    .getReference("KalimatNilaiRaport")
                    .child("a").removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        bidangAdapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,bidangList)
        bidangDropdown.setAdapter(bidangAdapter)
    }
    fun updateRecentKalimat(kelas:String,id:String){
        val ref = FirebaseDatabase.getInstance().getReference("KalimatNilaiRaport").child(kelas).child(id)

        ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                btg1Input.setText(snapshot.child("btg1").getValue().toString())
                btg2Input.setText(snapshot.child("btg2").getValue().toString())
                btg3Input.setText(snapshot.child("btg3").getValue().toString())
                btg4Input.setText(snapshot.child("btg4").getValue().toString())

                ref.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    fun uploadKalimat(kelas:String,id: String,btgInput:TextInputEditText,btgName:String){

        FirebaseDatabase.getInstance()
            .getReference("KalimatNilaiRaport")
            .child(kelas)
            .child(id)
            .child(btgName)
            .setValue(btgInput.getText().toString())
    }

    //setter
    fun setBidangSelected(bidang:String){
        bidangSelected = bidang
    }
    fun setKelasSelected(kelas:String){
        kelasSelected=kelas
    }
    fun setIdBidangSelected(bidang:String){
        var tmp = bidang.split(".")
        idBidangSelected = tmp.get(0)
    }

    //getter
    fun getBidangSelected():String{
        return bidangSelected
    }
    fun getKelasSelected():String{
        return kelasSelected
    }
    fun getIdBidangSelected():String{
        return idBidangSelected
    }
}