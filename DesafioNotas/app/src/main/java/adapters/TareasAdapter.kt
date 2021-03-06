package adapters

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.desafionotas.DetalleNotasActivity
import com.example.desafionotas.R
import model.Tarea
import androidx.core.app.ActivityCompat.startActivityForResult
import assistant.Auxiliar


class TareasAdapter(
    var context: DetalleNotasActivity,
    var tareas: ArrayList<Tarea>,
    var eliminables: ArrayList<Tarea> = ArrayList<Tarea>(0)
) :
    RecyclerView.Adapter<TareasAdapter.ViewHolder>() {

    var tareaChanged: Tarea? = null

    companion object {
        var seleccionado: Int = -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.tareas_list, parent, false), context)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = tareas.get(position)
        holder.bind(item, context, position, this)
    }

    override fun getItemCount(): Int {
        return tareas.size
    }

    private fun delTarea(tarea: Tarea) {
        eliminables.add(tarea)
        tareas.remove(tarea)
    }

    class ViewHolder(view: View, val ventana: DetalleNotasActivity) :
        RecyclerView.ViewHolder(view) {
        val imagen = view.findViewById<ImageButton>(R.id.imgTarea)
        val descTarea = view.findViewById<TextView>(R.id.txtTarea)
        val checked = view.findViewById<ImageView>(R.id.imgCheck)

        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(tarea: Tarea, context: AppCompatActivity, pos: Int, tareasAdapter: TareasAdapter) {
            descTarea.text = tarea.tarea
            checked.isVisible = tarea.realizada
            if (tarea.img != null) imagen.setImageBitmap(tarea.img)
            else imagen.setImageResource(R.drawable.tarea_default)
            if (tarea.realizada) descTarea.apply {
                paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                setTextAppearance(R.style.txtTareaRealizada)
            }
            else descTarea.apply {
                paintFlags = Paint.ANTI_ALIAS_FLAG
                setTextAppearance(R.style.txtTareaNoRealizada)
            }
            imagen.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> imagen.setBackgroundColor(Color.DKGRAY)
                    MotionEvent.ACTION_UP -> {
                        imagen.setBackgroundColor(Color.TRANSPARENT)
                        tareasAdapter.tareaChanged = tarea
                        CambiarFoto()
                    }
                }
                true
            })
            itemView.setOnClickListener(View.OnClickListener {
                tarea.realizada = !tarea.realizada
                tareasAdapter.notifyDataSetChanged()
            })
            itemView.setOnLongClickListener(View.OnLongClickListener {
                marcarSeleccion(tareasAdapter, pos)
                AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.strTituloBorrar))
                    .setMessage(context.getString(R.string.strMensajeBorrar))
                    .setPositiveButton(context.getString(R.string.strConfirmacion)) { view, _ ->
                        tareasAdapter.delTarea(tarea)
                        Toast.makeText(
                            context,
                            context.getString(R.string.strEliminando),
                            Toast.LENGTH_SHORT
                        ).show()
                        tareasAdapter.notifyDataSetChanged()
                        view.dismiss()
                    }
                    .setNegativeButton(context.getString(R.string.strNegacion)) { view, _ ->
                        view.dismiss()
                    }
                    .setCancelable(false)
                    .create()
                    .show()
                true
            })
        }

        private fun CambiarFoto() {
            AlertDialog.Builder(ventana)
                .setTitle(ventana.getString(R.string.strElegirFoto))
                .setMessage(ventana.getString(R.string.strMensajeElegirFoto))
                .setPositiveButton(ventana.getString(R.string.strCamara)) { view, _ ->
                    hacerFoto()
                    view.dismiss()
                }
                .setNegativeButton(ventana.getString(R.string.strGaleria)) { view, _ ->
                    elegirDeGaleria()
                    view.dismiss()
                }
                .setCancelable(true)
                .create()
                .show()
        }

        private fun elegirDeGaleria() {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            ventana.startActivityForResult(
                Intent.createChooser(intent, "Seleccione una imagen"),
                Auxiliar.CODE_GALLERY
            )
        }

        private fun hacerFoto() {
            if (ContextCompat.checkSelfPermission(
                    ventana,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_DENIED
            )
                ActivityCompat.requestPermissions(
                    ventana,
                    arrayOf(Manifest.permission.CAMERA),
                    Auxiliar.CODE_CAMERA
                )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            ventana.startActivityForResult(intent, Auxiliar.CODE_CAMERA)
        }

        private fun marcarSeleccion(tareasAdapter: TareasAdapter, pos: Int) {
            seleccionado = if (pos == seleccionado) -1
            else pos
            tareasAdapter.notifyDataSetChanged()
        }

    }
}