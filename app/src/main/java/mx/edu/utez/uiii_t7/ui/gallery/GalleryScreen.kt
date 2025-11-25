package mx.edu.utez.uiii_t7.ui.gallery

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import mx.edu.utez.uiii_t7.data.db.Trip
import mx.edu.utez.uiii_t7.utils.PhotoExporter   // 👈 IMPORTANTE
import mx.edu.utez.uiii_t7.viewmodel.GalleryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = viewModel()
) {
    val trips by viewModel.completedTrips.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {

        // 🔵 BOTÓN PARA EXPORTAR FOTOS
        Button(
            onClick = {
                val copied = PhotoExporter.copyAllPhotosToGallery(context)
                Toast.makeText(
                    context,
                    "Se exportaron $copied fotos a la galería",
                    Toast.LENGTH_LONG
                ).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Exportar fotos a galería")
        }

        // 🔽 LISTA DE VIAJES (Tu código original)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(trips) { trip ->
                TripPhotoCard(trip = trip)
            }
        }
    }
}

@Composable
fun TripPhotoCard(trip: Trip) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column {
            AsyncImage(
                model = trip.photoUri?.let { Uri.parse(it) },
                contentDescription = "Foto del viaje ${trip.id}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = "Viaje #${trip.id}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Finalizado: ${trip.endTime?.toFormattedDate()}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

fun Long.toFormattedDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return format.format(date)
}
