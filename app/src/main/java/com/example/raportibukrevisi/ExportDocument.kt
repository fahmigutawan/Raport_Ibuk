package com.example.raportibukrevisi

import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.apache.poi.xwpf.usermodel.*
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*
import java.io.File
import java.io.FileOutputStream
import java.math.BigInteger

class ExportDocument {
    private lateinit var document:XWPFDocument
    private lateinit var outputDestination:FileOutputStream

    //kalimat
    private var kalimatAgamaMoral="_"
    private var kalimatFisikMotorik="_"
    private var kalimatKognitif="_"
    private var kalimatBahasa="_"
    private var kalimatSosialEmosi="_"
    private var kalimatSeni="_"
    private var kalimatCatatanGuru="_"

    fun export(tahun:String,kelas:String,nama:String,nomorinduk:String,tanggal:String,kepsek:String) {
        val parentfolder = File(Environment.getExternalStorageDirectory(), "Raport")
        if (!parentfolder.isDirectory) {
            parentfolder.mkdirs()
        }
        val path = File(parentfolder, String.format("%s %s", kelas.uppercase(), tahun))
        if (!path.isDirectory) {
            path.mkdirs()
        }

        //deklarasi item yang dibutuhkan
        while(kalimatAgamaMoral=="_"){
        setKalimatAgamaMoral(tahun, kelas, nama)
        }
//        setKalimatFisikMotorik(tahun, kelas, nama)
//        setKalimatKognitif(tahun, kelas, nama)
//        setKalimatBahasa(tahun, kelas, nama)
//        setKalimatSosialEmosi(tahun, kelas, nama)
//        setKalimatSeni(tahun, kelas, nama)
//        setKalimatCatatanGuru(tahun, kelas, nama)

        //proses export satu halaman
        setDocument()
        setOutputDestination(path, nama, tahun, kelas)

        //fungsi pembuatan tabel 1 (Identitas)
        viewTabel1Identitas(nama, nomorinduk)

        addVerticalSpace(3)
        viewTabel2Title()

        addVerticalSpace(1)
        viewTabelTitleNilai("(NAM) Nilai Agama dan Moral", "A")
        addVerticalSpace(1)
        viewTabelNilai("(NAM) Nilai Agama dan Moral", getKalimatAgamaMoral())

//        while(getKalimatFisikMotorik()=="_") {
//            addVerticalSpace(1)
//            viewTabelTitleNilai("(FM) Fisik Motorik", "B")
//            addVerticalSpace(1)
//            viewTabelNilai("(FM) Fisik Motorik", getKalimatFisikMotorik())
//        }
//        while(getKalimatKognitif()=="_") {
//            addVerticalSpace(1)
//            viewTabelTitleNilai("(KOG) Kognitif", "C")
//            addVerticalSpace(1)
//            viewTabelNilai("(KOG) Kognitif", getKalimatKognitif())
//        }
//        while(getKalimatBahasa()=="_") {
//            addVerticalSpace(1)
//            viewTabelTitleNilai("(Bahasa) BAHASA", "D")
//            addVerticalSpace(1)
//            viewTabelNilai("(Bahasa) BAHASA", getKalimatBahasa())
//        }
//        while (getKalimatSosialEmosi()=="_") {
//            addVerticalSpace(1)
//            viewTabelTitleNilai("Sosial Emosional", "E")
//            addVerticalSpace(1)
//            viewTabelNilai("Sosial Emosional", getKalimatSosialEmosi())
//        }
//        while(getKalimatSeni()=="_") {
//            addVerticalSpace(1)
//            viewTabelTitleNilai("Seni", "F")
//            addVerticalSpace(1)
//            viewTabelNilai("Seni", getKalimatSeni())
//        }
//
//        //tabel tidak hadir
//        addVerticalSpace(1)
//        viewTabelTitleNilai("Ketidakhadiran", "G")
//        addVerticalSpace(1)
//        viewTabelAbsen()
//
//        while(getKalimatCatatanGuru()=="_") {
//            addVerticalSpace(1)
//            viewTabelTitleNilai("Catatan Guru Kelas", "H")
//            addVerticalSpace(1)
//            viewTabelNilai("Catatan Guru Kelas", getKalimatCatatanGuru())
//        }
//
//        //tabel tanggapan ortu
//        addVerticalSpace(1)
//        viewTabelTitleNilai("Tanggapan Orang Tua", "I")
//        addVerticalSpace(1)
//        viewTabelNilai("Tanggapan Orang Tua","")
//
//        //tabel ttd
//        addVerticalSpace(3)
//        viewTabelTTDGuruDanOrtu(tanggal)
//        addVerticalSpace(1)
//        viewTabelTTDKepsek(kepsek)

            getDocument().write(getOutputDestination())
    }

    //fungsi pembuatan tabel
    fun viewTabel1Identitas(nama:String,nomorinduk:String){
        val tableIdentitas = getDocument().createTable(2,3)
        setFixedTableLayout(tableIdentitas)

        //kolom 1 (nama dan nis)
        setColumnWidth(tableIdentitas,0,2950)

        val cellTxtNama = tableIdentitas.getRow(0).getCell(0)
        cellTxtNama.addParagraph()//agar bagian atas bertambah satu kali enter
        setCellStyle(cellTxtNama,"Times New Roman",14,"000000",false,"    Nama",ParagraphAlignment.LEFT)
        hideAllBorder(cellTxtNama)
        addTopBorder(cellTxtNama)

        val cellTxtNis = tableIdentitas.getRow(1).getCell(0)
        setCellStyle(cellTxtNis,"Times New Roman",14,"000000",false,"    NIS",ParagraphAlignment.LEFT)
        cellTxtNis.addParagraph()//agar bagian bawah bertambah satu kali enter
        hideAllBorder(cellTxtNis)
        addBottomBorder(cellTxtNis)

        //kolom 2(:)
        val ttk2_1 = tableIdentitas.getRow(0).getCell(1)
        ttk2_1.addParagraph()//agar bagian atas bertambah satu kali enter
        setCellStyle(ttk2_1,"Times New Roman",14,"000000",false,":",ParagraphAlignment.LEFT)
        hideAllBorder(ttk2_1)
        addTopBorder(ttk2_1)

        val ttk2_2 = tableIdentitas.getRow(1).getCell(1)
        setCellStyle(ttk2_2,"Times New Roman",14,"000000",false,":",ParagraphAlignment.LEFT)
        ttk2_2.addParagraph()//agar bagian bawah bertambah satu kali enter
        hideAllBorder(ttk2_2)
        addBottomBorder(ttk2_2)

        //kolom 3(nama murid dn nis)
        setColumnWidth(tableIdentitas,2,5065)

        val cellNama = tableIdentitas.getRow(0).getCell(2)
        cellNama.addParagraph()//agar bagian atas bertambah satu kali enter
        setCellStyle(cellNama,"Times New Roman",14,"000000",false,nama,ParagraphAlignment.LEFT)
        hideAllBorder(cellNama)
        addTopBorder(cellNama)

        val cellNis = tableIdentitas.getRow(1).getCell(2)
        setCellStyle(cellNis,"Times New Roman",14,"000000",false,nomorinduk,ParagraphAlignment.LEFT)
        cellNis.addParagraph()//agar bagian bawah bertambah satu kali enter
        hideAllBorder(cellNis)
        addBottomBorder(cellNis)
    }
    fun viewTabel2Title(){
        val tabel = getDocument().createTable(3,1)
        setColumnWidth(tabel,0,8190)
        setFixedTableLayout(tabel)

        val cellTxtLaporan = tabel.getRow(0).getCell(0)
        hideAllBorder(cellTxtLaporan)
        setCellStyle(cellTxtLaporan,"Times New Roman", 18, "000000", true, "LAPORAN",ParagraphAlignment.CENTER)

        val cellTxtPerkembangan = tabel.getRow(1).getCell(0)
        hideAllBorder(cellTxtPerkembangan)
        setCellStyle(cellTxtPerkembangan,"Times New Roman", 18, "000000", true, "PERKEMBANGAN ANAK DIDIK",ParagraphAlignment.CENTER)

        val cellTxtSem1 = tabel.getRow(2).getCell(0)
        hideAllBorder(cellTxtSem1)
        setCellStyle(cellTxtSem1,"Times New Roman", 18, "000000", true, "SEMESTER",ParagraphAlignment.CENTER)
    }
    fun viewTabelTitleNilai(bidang:String,huruf:String){
        val title = getDocument().createTable(1,1)
        setColumnWidth(title,0,8190)
        setFixedTableLayout(title)
        val cellTitle = title.getRow(0).getCell(0)
        hideAllBorder(cellTitle)
        setCellStyle(cellTitle,"Times New Roman", 16, "000000", true, String.format("%s. %s",huruf,bidang),ParagraphAlignment.LEFT)
    }
    fun viewTabelNilai(bidang:String,kalimat:String){
        val tabel = getDocument().createTable(2,1)
        setTableAlign(tabel,ParagraphAlignment.RIGHT)
        setColumnWidth(tabel,0,7750)
        setFixedTableLayout(tabel)

        //cell baris 1, kolom 1
        val cellHeader = tabel.getRow(0).getCell(0)
        setCellStyle(cellHeader,"Times New Roman", 12, "000000", false, bidang, ParagraphAlignment.CENTER)

        //cell baris 2, kolom 1
        val cellKalimat = tabel.getRow(1).getCell(0)
        setCellStyle(cellKalimat,"Times New Roman", 12, "000000", false, kalimat, ParagraphAlignment.BOTH)
    }
    fun viewTabelAbsen(){
        val tabel = getDocument().createTable(3,2)
        setTableAlign(tabel,ParagraphAlignment.RIGHT)

        //set setting tabel
        setColumnWidth(tabel,0,1680)
        setColumnWidth(tabel,1,6070)
        setFixedTableLayout(tabel)

        val cellTxtSakit=tabel.getRow(0).getCell(0)
        setCellStyle(cellTxtSakit,"Times New Roman", 12, "000000", false, "Sakit", ParagraphAlignment.LEFT)

        val cellTxtIzin = tabel.getRow(1).getCell(0)
        setCellStyle(cellTxtIzin,"Times New Roman", 12, "000000", false, "Izin", ParagraphAlignment.LEFT)

        val cellTxtTanpaKet = tabel.getRow(2).getCell(0)
        setCellStyle(cellTxtTanpaKet,"Times New Roman", 12, "000000", false, "Tanpa keterangan", ParagraphAlignment.LEFT)

    }
    fun viewTabelTTDGuruDanOrtu(tanggal:String){
        val tabel = getDocument().createTable(5,2)
        setFixedTableLayout(tabel)

        //settign kolom
        setColumnWidth(tabel,0,4095)
        setColumnWidth(tabel,1,4095)

        //cell kosong
        hideAllBorder(tabel.getRow(0).getCell(0))
        hideAllBorder(tabel.getRow(4).getCell(0))

        //tanggal
        val cellTanggal = tabel.getRow(0).getCell(1)
        setCellStyle(cellTanggal,"Times New Roman", 12, "000000", true, tanggal, ParagraphAlignment.RIGHT)
        hideAllBorder(cellTanggal)

        //guru dan wali (kolom 1 dan 2, baris 2)
        val cellTxtOrtu = tabel.getRow(1).getCell(0)
        setCellStyle(cellTxtOrtu,"Times New Roman", 12, "000000", true, "Orangtua/Wali", ParagraphAlignment.LEFT)
        hideAllBorder(cellTxtOrtu)

        val cellTxtGuru = tabel.getRow(1).getCell(1)
        setCellStyle(cellTxtGuru,"Times New Roman", 12, "000000", true, "Guru Kelas", ParagraphAlignment.RIGHT)
        hideAllBorder(cellTxtGuru)

        //set tempat tanda tangan
        tabel.getRow(2).setHeight(1050)
        hideAllBorder(tabel.getRow(2).getCell(0))
        hideAllBorder(tabel.getRow(2).getCell(1))

        //tanda tangan ortu
        val cellNamaOrtu = tabel.getRow(3).getCell(0)
        setCellStyle(cellNamaOrtu,"Times New Roman", 12, "000000", false, "(                     )", ParagraphAlignment.LEFT)
        hideAllBorder(cellNamaOrtu)

        //tanda tangan guru
        val cellNamaGuru = tabel.getRow(3).getCell(1)
        setCellStyle(cellNamaGuru,"Times New Roman", 12, "000000", false, "(Nur Hidayati,S.Pd I)", ParagraphAlignment.RIGHT)
        hideAllBorder(cellNamaGuru)

        val cellNipGuru = tabel.getRow(4).getCell(1)
        setCellStyle(cellNipGuru,"Times New Roman", 12, "000000", false, "NIP.197005052005012001", ParagraphAlignment.RIGHT)
        hideAllBorder(cellNipGuru)

    }
    fun viewTabelTTDKepsek(kepsek:String){
        val tabel = getDocument().createTable(4,1)
        setFixedTableLayout(tabel)
        setColumnWidth(tabel,0,8190)

        //set tempat tanda tangan
        tabel.getRow(2).setHeight(1050)

        //mengetahui
        setCellStyle(
            tabel.getRow(0).getCell(0)
            ,"Times New Roman"
            ,12
            ,"000000"
            ,false
            ,"MENGETAHUI"
            ,ParagraphAlignment.CENTER)
        hideAllBorder(tabel.getRow(0).getCell(0))

        setCellStyle(
            tabel.getRow(1).getCell(0)
            ,"Times New Roman"
            ,12
            ,"000000"
            ,false
            ,"Kepala Raudhatul Athfal"
            ,ParagraphAlignment.CENTER)
        hideAllBorder(tabel.getRow(1).getCell(0))

        setCellStyle(
            tabel.getRow(3).getCell(0)
            ,"Times New Roman"
            ,12
            ,"000000"
            ,false
            ,"("+kepsek+")"
            ,ParagraphAlignment.CENTER)
        hideAllBorder(tabel.getRow(2).getCell(0))
        hideAllBorder(tabel.getRow(3).getCell(0))
    }

    //fungsi khusus
    fun permission():Array<String>{
        return arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
    fun setTableAlign(table:XWPFTable,align:ParagraphAlignment){
        val tblPr = table.ctTbl.tblPr
        var jc = tblPr.getJc()

        if(jc==null){
            jc = tblPr.addNewJc()
        }
        val en = STJc.Enum.forInt(align.getValue())

        jc.`val` = en
    }
    fun setColumnWidth(table:XWPFTable,col:Int,width:Long){
        //setiap 210 mewakili 1 ruler
        val tblWidth = CTTblWidth.Factory.newInstance()
        tblWidth.setW(BigInteger.valueOf(width))
        tblWidth.setType(STTblWidth.DXA)

        val tableRow:List<XWPFTableRow> = table.getRows()

        //memaanggil tiap anggota row, sehingga setiap column pada row tsb akan dirubah ukurannya
        var index=0
        while(index<tableRow.size){
            var tcPr = tableRow.get(index).getCell(col).ctTc.tcPr
            if(tcPr!=null){
                tcPr.setTcW(tblWidth)
            }else{
                tcPr = CTTcPr.Factory.newInstance()
                tcPr.setTcW(tblWidth)
                tableRow.get(index).getCell(col).getCTTc().setTcPr(tcPr)
            }
            index+=1
        }
    }
    fun setIndividuWidth(cell:XWPFTableCell){
        val cellwidth = cell.ctTc.addNewTcPr().addNewTcW()

        val pr = cell.ctTc.addNewTcPr()
        pr.addNewNoWrap()
        cellwidth.setW(BigInteger.valueOf(2876))
    }
    fun setRowHeight(tabel:XWPFTable,row:Int,height:Int){
        //333 mewakili 1 ruler
        tabel.getRow(row).setHeight(height)
    }
    fun setFixedTableLayout(table:XWPFTable){
        val type = table.ctTbl.tblPr.addNewTblLayout()
        type.type = STTblLayoutType.FIXED
    }
    fun setParagraphStyle(run:XWPFRun,font:String, fontSize:Int, colorRGB:String, bold:Boolean, text:String,){
        run.setFontFamily(font)
        run.setFontSize(fontSize)
        run.setColor(colorRGB)
        run.isBold = bold
        run.setText(text)
    }
    fun setCellStyle(cell: XWPFTableCell, font:String, fontSize:Int, colorRGB:String, bold:Boolean, text:String,aligntment:ParagraphAlignment){
        //setting paragraf
        val paragraph = cell.addParagraph()
        paragraph.setAlignment(aligntment)

        //setting text style
        val run = paragraph.createRun()
        cell.removeParagraph(0)
        run.setFontFamily(font)
        run.setFontSize(fontSize)
        run.setColor(colorRGB)
        run.isBold = bold
        run.setText(text)
    }
    fun addVerticalSpace(jumlah:Int){
        var param = 0
        while(param<jumlah){
            getDocument().createParagraph()
            param+=1
        }
    }

    //fungsi hide border
    fun hideRightBorder(cell:XWPFTableCell){
        var tcPr = cell.getCTTc().getTcPr()
        if(tcPr==null){
            tcPr = cell.getCTTc().addNewTcPr()
        }

        var tcBorder = tcPr.getTcBorders()
        if(tcBorder==null){
            tcBorder = tcPr.addNewTcBorders()
        }

        tcBorder.addNewRight().setVal(STBorder.NIL)
    }
    fun hideLeftBorder(cell:XWPFTableCell){
        var tcPr = cell.getCTTc().getTcPr()
        if(tcPr==null){
            tcPr = cell.getCTTc().addNewTcPr()
        }

        var tcBorder = tcPr.getTcBorders()
        if(tcBorder==null){
            tcBorder = tcPr.addNewTcBorders()
        }

        tcBorder.addNewLeft().setVal(STBorder.NIL)
    }
    fun hideTopBorder(cell:XWPFTableCell){
        var tcPr = cell.getCTTc().getTcPr()
        if(tcPr==null){
            tcPr = cell.getCTTc().addNewTcPr()
        }

        var tcBorder = tcPr.getTcBorders()
        if(tcBorder==null){
            tcBorder = tcPr.addNewTcBorders()
        }

        tcBorder.addNewTop().setVal(STBorder.NIL)
    }
    fun hideBottomBorder(cell:XWPFTableCell){
        var tcPr = cell.getCTTc().getTcPr()
        if(tcPr==null){
            tcPr = cell.getCTTc().addNewTcPr()
        }

        var tcBorder = tcPr.getTcBorders()
        if(tcBorder==null){
            tcBorder = tcPr.addNewTcBorders()
        }

        tcBorder.addNewBottom().setVal(STBorder.NIL)
    }
    fun hideAllBorder(cell:XWPFTableCell){
        var tcPr = cell.getCTTc().getTcPr()
        if(tcPr==null){
            tcPr = cell.getCTTc().addNewTcPr()
        }

        var tcBorder = tcPr.getTcBorders()
        if(tcBorder==null){
            tcBorder = tcPr.addNewTcBorders()
        }

        tcBorder.addNewBottom().setVal(STBorder.NIL)
        tcBorder.addNewTop().setVal(STBorder.NIL)
        tcBorder.addNewRight().setVal(STBorder.NIL)
        tcBorder.addNewLeft().setVal(STBorder.NIL)
    }

    //fungsi setborder
    fun addRightBorder(cell:XWPFTableCell){
        var tcPr = cell.getCTTc().getTcPr()
        if(tcPr==null){
            tcPr = cell.getCTTc().addNewTcPr()
        }

        var tcBorder = tcPr.getTcBorders()
        if(tcBorder==null){
            tcBorder = tcPr.addNewTcBorders()
        }

        tcBorder.addNewRight().setVal(STBorder.THICK)
    }
    fun addLeftBorder(cell:XWPFTableCell){
        var tcPr = cell.getCTTc().getTcPr()
        if(tcPr==null){
            tcPr = cell.getCTTc().addNewTcPr()
        }

        var tcBorder = tcPr.getTcBorders()
        if(tcBorder==null){
            tcBorder = tcPr.addNewTcBorders()
        }

        tcBorder.addNewLeft().setVal(STBorder.THICK)
    }
    fun addTopBorder(cell:XWPFTableCell){
        var tcPr = cell.getCTTc().getTcPr()
        if(tcPr==null){
            tcPr = cell.getCTTc().addNewTcPr()
        }

        var tcBorder = tcPr.getTcBorders()
        if(tcBorder==null){
            tcBorder = tcPr.addNewTcBorders()
        }

        tcBorder.addNewTop().setVal(STBorder.THICK)
    }
    fun addBottomBorder(cell:XWPFTableCell){
        var tcPr = cell.getCTTc().getTcPr()
        if(tcPr==null){
            tcPr = cell.getCTTc().addNewTcPr()
        }

        var tcBorder = tcPr.getTcBorders()
        if(tcBorder==null){
            tcBorder = tcPr.addNewTcBorders()
        }

        tcBorder.addNewBottom().setVal(STBorder.THICK)
    }

    //fungsi setter nilai dan kalimat
    fun setKalimatAgamaMoral(tahun:String,kelas:String,nama:String){
        var nilai = ""
        //ambil nilai dulu dari db
        val refKelas = FirebaseDatabase.getInstance()
            .getReference("DB")
            .child(tahun)
            .child(kelas).child(nama)
            .child("tugas")
            .child("1_Agama dan Moral")

        refKelas.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                nilai = snapshot.child("nilai").getValue().toString()
                refKelas.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //sesuaikan nilai dengan kalimat, dimasukkan dalam variabel class
        val refKalimat = FirebaseDatabase.getInstance()
            .getReference("KalimatNilaiRaport")
            .child(kelas)
            .child("1")
        refKalimat.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(nilai=="0"){
                    kalimatAgamaMoral="Belum Dinilai"
                }else if(nilai=="1"){
                    kalimatAgamaMoral=snapshot.child("btg1").getValue().toString()
                }else if(nilai=="2"){
                    kalimatAgamaMoral=snapshot.child("btg2").getValue().toString()
                }else if(nilai=="3"){
                    kalimatAgamaMoral=snapshot.child("btg3").getValue().toString()
                }else if(nilai=="4"){
                    kalimatAgamaMoral=snapshot.child("btg4").getValue().toString()
                }
                refKalimat.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    fun setKalimatFisikMotorik(tahun:String,kelas:String,nama:String){
        var nilai = ""
        //ambil nilai dulu dari db
        val refKelas = FirebaseDatabase.getInstance()
            .getReference("DB")
            .child(tahun)
            .child(kelas).child(nama)
            .child("tugas")
            .child("2_Fisik Motorik")

        refKelas.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                nilai = snapshot.child("nilai").getValue().toString()
                refKelas.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //sesuaikan nilai dengan kalimat, dimasukkan dalam variabel class
        val refKalimat = FirebaseDatabase.getInstance()
            .getReference("KalimatNilaiRaport")
            .child(kelas)
            .child("2")
        refKalimat.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(nilai=="0"){
                    kalimatAgamaMoral="Belum Dinilai"
                }else if(nilai=="1"){
                    kalimatAgamaMoral=snapshot.child("btg1").getValue().toString()
                }else if(nilai=="2"){
                    kalimatAgamaMoral=snapshot.child("btg2").getValue().toString()
                }else if(nilai=="3"){
                    kalimatAgamaMoral=snapshot.child("btg3").getValue().toString()
                }else if(nilai=="4"){
                    kalimatAgamaMoral=snapshot.child("btg4").getValue().toString()
                }
                refKalimat.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    fun setKalimatKognitif(tahun:String,kelas:String,nama:String){
        var nilai = ""
        //ambil nilai dulu dari db
        val refKelas = FirebaseDatabase.getInstance()
            .getReference("DB")
            .child(tahun)
            .child(kelas).child(nama)
            .child("tugas")
            .child("3_Kognitif")

        refKelas.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                nilai = snapshot.child("nilai").getValue().toString()
                refKelas.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //sesuaikan nilai dengan kalimat, dimasukkan dalam variabel class
        val refKalimat = FirebaseDatabase.getInstance()
            .getReference("KalimatNilaiRaport")
            .child(kelas)
            .child("3")
        refKalimat.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(nilai=="0"){
                    kalimatAgamaMoral="Belum Dinilai"
                }else if(nilai=="1"){
                    kalimatAgamaMoral=snapshot.child("btg1").getValue().toString()
                }else if(nilai=="2"){
                    kalimatAgamaMoral=snapshot.child("btg2").getValue().toString()
                }else if(nilai=="3"){
                    kalimatAgamaMoral=snapshot.child("btg3").getValue().toString()
                }else if(nilai=="4"){
                    kalimatAgamaMoral=snapshot.child("btg4").getValue().toString()
                }
                refKalimat.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    fun setKalimatBahasa(tahun:String,kelas:String,nama:String){
        var nilai = ""
        //ambil nilai dulu dari db
        val refKelas = FirebaseDatabase.getInstance()
            .getReference("DB")
            .child(tahun)
            .child(kelas).child(nama)
            .child("tugas")
            .child("4_Bahasa")

        refKelas.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                nilai = snapshot.child("nilai").getValue().toString()
                refKelas.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //sesuaikan nilai dengan kalimat, dimasukkan dalam variabel class
        val refKalimat = FirebaseDatabase.getInstance()
            .getReference("KalimatNilaiRaport")
            .child(kelas)
            .child("1")
        refKalimat.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(nilai=="0"){
                    kalimatAgamaMoral="Belum Dinilai"
                }else if(nilai=="1"){
                    kalimatAgamaMoral=snapshot.child("btg1").getValue().toString()
                }else if(nilai=="2"){
                    kalimatAgamaMoral=snapshot.child("btg2").getValue().toString()
                }else if(nilai=="3"){
                    kalimatAgamaMoral=snapshot.child("btg3").getValue().toString()
                }else if(nilai=="4"){
                    kalimatAgamaMoral=snapshot.child("btg4").getValue().toString()
                }
                refKalimat.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    fun setKalimatSosialEmosi(tahun:String,kelas:String,nama:String){
        var nilai = ""
        //ambil nilai dulu dari db
        val refKelas = FirebaseDatabase.getInstance()
            .getReference("DB")
            .child(tahun)
            .child(kelas).child(nama)
            .child("tugas")
            .child("5_Sosial Emosional")

        refKelas.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                nilai = snapshot.child("nilai").getValue().toString()
                refKelas.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //sesuaikan nilai dengan kalimat, dimasukkan dalam variabel class
        val refKalimat = FirebaseDatabase.getInstance()
            .getReference("KalimatNilaiRaport")
            .child(kelas)
            .child("5")
        refKalimat.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(nilai=="0"){
                    kalimatAgamaMoral="Belum Dinilai"
                }else if(nilai=="1"){
                    kalimatAgamaMoral=snapshot.child("btg1").getValue().toString()
                }else if(nilai=="2"){
                    kalimatAgamaMoral=snapshot.child("btg2").getValue().toString()
                }else if(nilai=="3"){
                    kalimatAgamaMoral=snapshot.child("btg3").getValue().toString()
                }else if(nilai=="4"){
                    kalimatAgamaMoral=snapshot.child("btg4").getValue().toString()
                }
                refKalimat.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    fun setKalimatSeni(tahun:String,kelas:String,nama:String){
        var nilai = ""
        //ambil nilai dulu dari db
        val refKelas = FirebaseDatabase.getInstance()
            .getReference("DB")
            .child(tahun)
            .child(kelas).child(nama)
            .child("tugas")
            .child("6_Seni")

        refKelas.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                nilai = snapshot.child("nilai").getValue().toString()
                refKelas.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //sesuaikan nilai dengan kalimat, dimasukkan dalam variabel class
        val refKalimat = FirebaseDatabase.getInstance()
            .getReference("KalimatNilaiRaport")
            .child(kelas)
            .child("6")
        refKalimat.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(nilai=="0"){
                    kalimatAgamaMoral="Belum Dinilai"
                }else if(nilai=="1"){
                    kalimatAgamaMoral=snapshot.child("btg1").getValue().toString()
                }else if(nilai=="2"){
                    kalimatAgamaMoral=snapshot.child("btg2").getValue().toString()
                }else if(nilai=="3"){
                    kalimatAgamaMoral=snapshot.child("btg3").getValue().toString()
                }else if(nilai=="4"){
                    kalimatAgamaMoral=snapshot.child("btg4").getValue().toString()
                }
                refKalimat.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    fun setKalimatCatatanGuru(tahun:String,kelas:String,nama:String){
        var nilai = 0
        var nilaiList=ArrayList<Int>()

        //ambil nilai dulu dari db
        val refKelas = FirebaseDatabase.getInstance()
            .getReference("DB")
            .child(tahun)
            .child(kelas).child(nama)
            .child("tugas")

        refKelas.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item:DataSnapshot in snapshot.getChildren()){
                    nilai += (item.child("nilai").getValue().toString()).toInt()
                }
                refKelas.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        val tmp = nilai.toDouble()/6
        nilai = Math.floor(tmp).toInt()

        //sesuaikan nilai dengan kalimat, dimasukkan dalam variabel class
        val refKalimat = FirebaseDatabase.getInstance()
            .getReference("KalimatNilaiRaport")
            .child(kelas)
            .child("7")
        refKalimat.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(nilai==0){
                    kalimatAgamaMoral="Belum Dinilai"
                }else if(nilai==1){
                    kalimatAgamaMoral=snapshot.child("btg1").getValue().toString()
                }else if(nilai==2){
                    kalimatAgamaMoral=snapshot.child("btg2").getValue().toString()
                }else if(nilai==3){
                    kalimatAgamaMoral=snapshot.child("btg3").getValue().toString()
                }else if(nilai==4){
                    kalimatAgamaMoral=snapshot.child("btg4").getValue().toString()
                }
                refKalimat.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //fungsi getter nilai dan kalimat
    fun getKalimatAgamaMoral():String{return kalimatAgamaMoral}
    fun getKalimatFisikMotorik():String{return kalimatFisikMotorik}
    fun getKalimatKognitif():String{return kalimatKognitif}
    fun getKalimatBahasa():String{return kalimatBahasa}
    fun getKalimatSosialEmosi():String{return kalimatSosialEmosi}
    fun getKalimatSeni():String{return kalimatSeni}
    fun getKalimatCatatanGuru():String{return kalimatCatatanGuru}

    //setter
    fun setOutputDestination(path:File,namaFile:String,tahun:String,kelas:String){
        outputDestination= FileOutputStream(File(path,"/"+namaFile+".docx"))
    }
    fun setDocument(){
        document = XWPFDocument()
    }

    //getter
    fun getOutputDestination():FileOutputStream{
        return outputDestination
    }
    fun getDocument():XWPFDocument{
        return document
    }
}