package com.example.raportibukrevisi

import android.app.AlertDialog
import android.content.Context
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ListRequired {
    private var dbReference = DbReference()
    fun listTahun(context:Context):ArrayList<String>{
        var list = ArrayList<String>()

        val layoutinflater = View.inflate(context,R.layout.loading_dialogview,null)
        val builder = AlertDialog.Builder(context)
        builder.setView(layoutinflater)

        val dialog = builder.create()
        dialog.show()
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dbReference.refMain().addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for(item:DataSnapshot in snapshot.children){
                    list.add(item.child("tahun").getValue().toString().replace("_","-"))
                }
                dialog.dismiss()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        return list
    }
    fun listKelas():ArrayList<String>{
        var list = ArrayList<String>()
        list.clear()

        list.add("A")
        list.add("B")

        return list
    }
    fun listNama(tahun:String,kelas:String,context:Context):ArrayList<String>{
        var list = ArrayList<String>()

        val layoutinflater = View.inflate(context,R.layout.loading_dialogview,null)
        val builder = AlertDialog.Builder(context)
        builder.setView(layoutinflater)

        val dialog = builder.create()
        dialog.show()
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dbReference.refMain().child(tahun).child(kelas).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for(item:DataSnapshot in snapshot.children){
                    if(item.child("nama").getValue().toString()!="null") {
                        list.add(item.child("nama").getValue().toString())
                    }
                }
                dialog.dismiss()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        return list
    }
    fun listBidang(context:Context):ArrayList<String>{
        var list = ArrayList<String>()

        val layoutinflater = View.inflate(context,R.layout.loading_dialogview,null)
        val builder = AlertDialog.Builder(context)
        builder.setView(layoutinflater)

        val dialog = builder.create()
        dialog.show()
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dbReference.refKalimat().child("a").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for(item:DataSnapshot in snapshot.children){
                    list.add(item.child("nama bidang").getValue().toString().replace("_","."))
                }
                dialog.dismiss()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        return list
    }
}