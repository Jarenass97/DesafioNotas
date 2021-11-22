package com.example.desafionotas

import adapters.ContactosAdapter
import adapters.TareasAdapter
import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import assistant.Auxiliar
import assistant.TipoNota
import connection.Conexion
import model.*

class DetalleNotasActivity : AppCompatActivity() {
    lateinit var nota: Nota
    lateinit var edAsunto: EditText
    lateinit var txtLastMod: TextView
    lateinit var txtFechaHora: TextView
    lateinit var animator: ViewAnimator
    lateinit var edTexto: EditText
    lateinit var listaTareas: ArrayList<Tarea>
    lateinit var rvTareas: RecyclerView
    lateinit var adaptadorTareas: TareasAdapter
    lateinit var adaptadorContactos: ContactosAdapter
    lateinit var rvContactos: RecyclerView
    lateinit var btnCompartir: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_notas)
        supportActionBar?.hide()

        val bun: Bundle? = intent.extras
        nota = bun?.getSerializable("nota") as Nota
        edAsunto = findViewById(R.id.edAsuntoDetalle)
        txtLastMod = findViewById(R.id.txtUltimaEdicionDetalle)
        txtFechaHora = findViewById(R.id.txtFechaHoraDetalle)
        edTexto = findViewById(R.id.edTextoDetalle)
        animator = findViewById(R.id.vaTiposNota)
        btnCompartir = findViewById(R.id.btnCompartirNota)
        rvTareas = findViewById(R.id.rvTareas)
        rvTareas.setHasFixedSize(true)
        rvTareas.layoutManager = LinearLayoutManager(this)
        cargarDatos()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.strTituloGuardar))
            .setMessage(getString(R.string.strMensajeGuardar))
            .setPositiveButton(getString(R.string.strConfirmacion)) { view, _ ->
                guardar()
                super.onBackPressed()
                view.dismiss()
            }
            .setNegativeButton(getString(R.string.strNegacion)) { view, _ ->
                super.onBackPressed()
                view.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun cargarDatos() {
        edAsunto.append(nota.asunto)
        txtLastMod.text = getUltimaModificacion()
        txtFechaHora.text = "${nota.fecha} - ${nota.hora}"
        when (nota.tipo) {
            TipoNota.TEXTO -> {
                edTexto.append((nota as NotaTexto).texto)
            }
            TipoNota.LISTA_TAREAS -> {
                animator.showNext()
                btnCompartir.isVisible = false
                listaTareas = Conexion.getTareas(this, nota.id)
                newTareasAdapter(TareasAdapter(this, listaTareas))
            }
        }
    }

    private fun newTareasAdapter(adaptador: TareasAdapter) {
        adaptadorTareas = adaptador
        rvTareas.adapter = adaptadorTareas
    }

    fun btnGuardar(view: View) {
        guardar()
        finish()
    }

    private fun guardar() {
        Conexion.modAsuntoNota(this, nota, edAsunto.text.toString().trim())
        when (nota.tipo) {
            TipoNota.TEXTO -> Conexion.modTextoNota(this, nota, edTexto.text.toString().trim())
            TipoNota.LISTA_TAREAS -> Conexion.guardarTareas(this, nota, adaptadorTareas.tareas)
        }
        Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show()
    }

    fun compartir(view: View) {
        btnCompartir.isEnabled = false
        var contactos: ArrayList<Contacto>
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            contactos = obtenerContactos()
        } else {
            requestPermission();
            contactos = obtenerContactos()
        }
        openDialogContacts(contactos)
        btnCompartir.isEnabled = true
    }

    private fun openDialogContacts(contactos: ArrayList<Contacto>) {
        lateinit var contacto: Contacto
        val dialogView = layoutInflater.inflate(R.layout.contactos_list, null)
        rvContactos = dialogView.findViewById(R.id.rvContactos)
        rvContactos.setHasFixedSize(true)
        rvContactos.layoutManager = LinearLayoutManager(this)
        newContactosAdapter(ContactosAdapter(this, contactos))
        if (contactos.size > 0) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.strContactos))
                .setView(dialogView)
                .setPositiveButton("OK") { dialog, _ ->
                    if (adaptadorContactos.isSelected()) {
                        contacto = adaptadorContactos.getSelected()
                        enviarSMS(contacto)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(
                            this,
                            getString(R.string.strContactoNoSeleccionado),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setCancelable(true)
                .create()
                .show()
        } else Toast.makeText(this, "No existen contactos", Toast.LENGTH_SHORT).show()
    }

    private fun enviarSMS(contacto: Contacto) {
        val pm = this.packageManager
        //Esta es una comprobación previa para ver si mi dispositivo puede enviar sms o no.
        if (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) || pm.hasSystemFeature(
                PackageManager.FEATURE_TELEPHONY_CDMA
            )
        ) {
            val permissionCheck =
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                sendSMS(contacto)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.SEND_SMS),
                    101
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSMS(adaptadorContactos.getSelected())
            } else {
                Toast.makeText(
                    this, "No tienes los permisos requeridos...",
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    private fun sendSMS(contacto: Contacto) {
        var myNumber: String = contacto.numero
        myNumber = myNumber.replace(" ", "")
        myNumber = myNumber.replace("+34", "")
        val myMsg = "${nota.asunto}:\n ${(nota as NotaTexto).texto}"
        if (TextUtils.isDigitsOnly(myNumber)) {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(myNumber, null, myMsg, null, null)
            Toast.makeText(this, getString(R.string.strMensajeEnviado), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "El número no es correcto...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun newContactosAdapter(adaptador: ContactosAdapter) {
        adaptadorContactos = adaptador
        rvContactos.adapter = adaptadorContactos
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_CONTACTS
            )
        ) {
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                79
            )
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_CONTACTS
            )
        ) {
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                79
            )
        }
    }

    fun addTarea(view: View) {
        var tarea = ""
        val dialogView = layoutInflater.inflate(R.layout.tarea_creater, null)
        val txtTarea = dialogView.findViewById<EditText>(R.id.edTarea)
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.strTituloAsunto))
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                tarea = txtTarea.text.toString().trim()
                tarea = if (tarea.isNotEmpty()) tarea
                else getString(R.string.strIndefinida)
                listaTareas.add(Tarea(Auxiliar.getNextIDTarea(), tarea))
                newTareasAdapter(TareasAdapter(this, listaTareas))
                dialog.dismiss()
            }
            .setCancelable(true)
            .create()
            .show()
    }

    private fun obtenerContactos(): ArrayList<Contacto> {
        var contactos: ArrayList<Contacto> = ArrayList(0)
        var cr = this.contentResolver
        var cur: Cursor? =
            cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null, null)
        if (cur != null) {
            if (cur.count > 0) {
                while (cur != null && cur.moveToNext()) {
                    var id =
                        cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID).toInt())
                    var nombre = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME).toInt()
                    )
                    if (cur.getInt(
                            cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER).toInt()
                        ) > 0
                    ) {
                        val pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )
                        //Sacamos todos los números de ese contacto.
                        if (pCur!!.moveToFirst()) {
                            val phoneNo = pCur!!.getString(
                                pCur!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    .toInt()
                            )
                            contactos.add(Contacto(nombre, phoneNo))
                            //Esto son los números asociados a ese contacto. Ahora mismo no hacemos nada con ellos.
                        }
                        pCur!!.close()
                    }
                }
            }
        }
        cur?.close()
        return contactos
    }

    private fun getUltimaModificacion(): String {
        var last = ""
        val horaMod = nota.hora
        val fechaMod = nota.fecha
        val horaActual = Auxiliar.horaActual()
        val fechaActual = Auxiliar.fechaActual()
        val hora_minutoMod = horaMod.split(":")
        val dia_mes_yearMod = fechaMod.split("/")
        val hora_minutoAct = horaActual.split(":")
        val dia_mes_yearAct = fechaActual.split("/")
        val difYears = difyears(dia_mes_yearMod[2], dia_mes_yearAct[2])
        if (difYears > 0) {
            last = "$difYears ${getString(R.string.strHaceYears)}"
        } else {
            val difMonths = difMonth(dia_mes_yearMod[1], dia_mes_yearAct[1])
            if (difMonths > 0) {
                last = "$difMonths ${getString(R.string.strHaceMeses)}"
            } else {
                val difDays = difDay(dia_mes_yearMod[0], dia_mes_yearAct[0])
                if (difDays > 0) {
                    last = "$difDays ${getString(R.string.strHaceDias)}"
                } else {
                    val difHours = difHour(hora_minutoMod[0], hora_minutoAct[0])
                    if (difHours > 0) {
                        last = "$difHours ${getString(R.string.strHaceHoras)}"
                    } else {
                        val difMinutes = difMin(hora_minutoMod[1], hora_minutoAct[1])
                        if (difMinutes > 0)
                            last = "$difMinutes ${getString(R.string.strHaceMinutos)}"
                        else last = getString(R.string.strMenosUnMin)
                    }
                }
            }
        }
        return last
    }

    private fun difMin(minMod: String, minAct: String): Int {
        return minAct.toInt() - minMod.toInt()
    }

    private fun difHour(hourMod: String, hourAct: String): Int {
        return hourAct.toInt() - hourMod.toInt()
    }

    private fun difDay(dayMod: String, dayAct: String): Int {
        return dayAct.toInt() - dayMod.toInt()
    }

    private fun difMonth(monthMod: String, monthAct: String): Int {
        return monthAct.toInt() - monthMod.toInt()
    }

    private fun difyears(yearMod: String, yearAct: String): Int {
        return yearAct.toInt() - yearMod.toInt()
    }

    fun delTarea(tarea: Tarea) {
        listaTareas.remove(tarea)
        newTareasAdapter(TareasAdapter(this, listaTareas))
    }
}