package com.example.desafionotas

import adapters.ContactosAdapter
import adapters.TareasAdapter
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
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
    lateinit var btnGuardar: ImageButton
    private val cameraRequest = 1888

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
        btnGuardar = findViewById(R.id.btnGuardarDetalle)
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
            .setCancelable(true)
            .create()
            .show()
    }

    private fun cargarDatos() {
        asignarFuncionBotones()
        edAsunto.append(nota.asunto)
        txtLastMod.text = getUltimaModificacion()
        txtFechaHora.text = "${nota.fecha} - ${nota.hora}"
        when (nota.tipo) {
            TipoNota.TEXTO -> {
                edTexto.append((nota as NotaTexto).texto)
            }
            TipoNota.LISTA_TAREAS -> {
                animator.showNext()
                //btnCompartir.isVisible = false
                listaTareas = Conexion.getTareas(this, nota.id)
                newTareasAdapter(TareasAdapter(this, listaTareas))
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun asignarFuncionBotones() {
        btnGuardar.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> btnGuardar.setBackgroundResource(R.color.itemSelected)
                MotionEvent.ACTION_UP -> {
                    btnGuardar.setBackgroundColor(Color.TRANSPARENT)
                    btnGuardar()
                }
            }
            true
        })
        btnCompartir.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> btnCompartir.setBackgroundResource(R.color.itemSelected)
                MotionEvent.ACTION_UP -> {
                    btnCompartir.setBackgroundColor(Color.TRANSPARENT)
                    compartir()
                }
            }
            true
        })
    }

    fun newTareasAdapter(adaptador: TareasAdapter) {
        adaptadorTareas = adaptador
        rvTareas.adapter = adaptadorTareas
    }

    fun btnGuardar() {
        guardar()
        finish()
    }

    private fun guardar() {
        var newAsunto = edAsunto.text.toString().trim()
        if (newAsunto.isEmpty()) newAsunto = getString(R.string.strSinAsunto)
        Conexion.modAsuntoNota(this, nota, newAsunto)
        when (nota.tipo) {
            TipoNota.TEXTO -> Conexion.modTextoNota(this, nota, edTexto.text.toString().trim())
            TipoNota.LISTA_TAREAS -> Conexion.guardarTareas(
                this,
                nota,
                adaptadorTareas.tareas,
                adaptadorTareas.eliminables
            )
        }
        Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show()
    }

    fun compartir() {
        btnCompartir.isEnabled = false
        enviar()
    }

    private fun enviar() {
        when (nota.tipo) {
            TipoNota.TEXTO -> {
                (nota as NotaTexto).texto = edTexto.text.toString()
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.strTituloCompartir))
                    .setMessage(getString(R.string.strMensajeCompartir))
                    .setPositiveButton("Whatsapp") { view, _ ->
                        enviarPorWhastsapp()
                        view.dismiss()
                    }
                    .setNegativeButton("SMS") { view, _ ->
                        enviarPorSMS()
                        view.dismiss()
                    }
                    .setCancelable(true)
                    .create()
                    .show()
            }
            TipoNota.LISTA_TAREAS -> {
                guardar()
                enviarPorWhastsapp()
            }
        }
        btnCompartir.isEnabled = true
    }

    private fun enviarPorSMS() {
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
    }

    private fun enviarPorWhastsapp() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("com.whatsapp")
        when (nota.tipo) {
            TipoNota.TEXTO -> intent.putExtra(
                Intent.EXTRA_TEXT,
                "*Asunto:* ${nota.asunto}\n*Contenido:*\n${(nota as NotaTexto).texto}"
            )
            TipoNota.LISTA_TAREAS -> intent.putExtra(
                Intent.EXTRA_TEXT,
                "*Asunto:* ${nota.asunto}\n*Contenido:*\n${tareasToString()}"
            )
        }

        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            ex.printStackTrace()
            Toast.makeText(this, getString(R.string.strWhatsappNoInstalado), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun tareasToString(): String {
        val tareas = Conexion.getTareas(this, nota.id)
        var stringTareas = ""
        for (t in tareas) {
            stringTareas += "- $t\n"
        }
        return stringTareas
    }

    private fun openDialogContacts(contactos: ArrayList<Contacto>) {
        lateinit var contacto: Contacto
        val dialogView = layoutInflater.inflate(R.layout.contactos_list, null)
        rvContactos = dialogView.findViewById(R.id.rvContactos)
        rvContactos.setHasFixedSize(true)
        rvContactos.layoutManager = LinearLayoutManager(this)
        val listaContactos = contactos.sortedBy { it.nombre }
        newContactosAdapter(
            ContactosAdapter(
                this,
                listaContactos
            )
        )
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
        val myMsg = "${nota.asunto}:\n${(nota as NotaTexto).texto}"
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
        val txtTarea = dialogView.findViewById<TextView>(R.id.edTarea)
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.strTituloAsunto))
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                tarea = txtTarea.text.toString().trim()
                tarea = if (tarea.isNotEmpty()) tarea
                else getString(R.string.strIndefinida)
                listaTareas.add(Tarea(Auxiliar.getNextIDTarea(), tarea))
                newTareasAdapter(TareasAdapter(this, listaTareas, adaptadorTareas.eliminables))
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == cameraRequest && resultCode == Activity.RESULT_OK) {
            if (adaptadorTareas.tareaChanged != null) {
                var photo = data?.extras?.get("data") as Bitmap
                val tareas = adaptadorTareas.tareas
                val tareaChanged = adaptadorTareas.tareaChanged!!
                val newTarea = tareaChanged
                newTarea.img = photo
                tareas[tareas.indexOf(tareaChanged)] = newTarea
                newTareasAdapter(TareasAdapter(this, tareas, adaptadorTareas.eliminables))
            }
        }
    }
}