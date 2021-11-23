package adapters

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Paint
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
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

class TareasAdapter(
    var context: DetalleNotasActivity,
    var tareas: ArrayList<Tarea>
) :
    RecyclerView.Adapter<TareasAdapter.ViewHolder>() {

    var eliminables = ArrayList<Tarea>(0)
    var tareaChanged: Tarea? = null

    companion object {
        var seleccionado: Int = -1
        var haciendoFoto: Boolean = false
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
        val cameraRequest = 1888

        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(tarea: Tarea, context: AppCompatActivity, pos: Int, tareasAdapter: TareasAdapter) {
            descTarea.text = tarea.tarea
            checked.isVisible = tarea.realizada
            if (tarea.img != null) imagen.setImageBitmap(tarea.img)
            if (tarea.realizada) descTarea.apply {
                paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                setTextAppearance(R.style.txtTareaRealizada)
            }
            else descTarea.apply {
                paintFlags = Paint.ANTI_ALIAS_FLAG
                setTextAppearance(R.style.txtTareaNoRealizada)
            }
            imagen.setOnClickListener(View.OnClickListener {
                tareasAdapter.tareaChanged = tarea
                hacerFoto(tareasAdapter, tarea)
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

        private fun hacerFoto(tareasAdapter: TareasAdapter, tarea: Tarea) {
            if (ContextCompat.checkSelfPermission(
                    ventana,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_DENIED
            )
                ActivityCompat.requestPermissions(
                    ventana,
                    arrayOf(Manifest.permission.CAMERA),
                    cameraRequest
                )
            Log.e("jorge", "haciendo afoto")
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            ventana.startActivityForResult(intent, cameraRequest)
        }

        private fun marcarSeleccion(tareasAdapter: TareasAdapter, pos: Int) {
            seleccionado = if (pos == seleccionado) -1
            else pos
            tareasAdapter.notifyDataSetChanged()
        }

    }

    private fun replace(tarea: Tarea, newTarea: Tarea) {
        tareas.remove(tarea)
        tareas.add(newTarea)
    }
}