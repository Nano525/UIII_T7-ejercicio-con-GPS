package mx.edu.utez.uiii_t7.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ejemplo.miappgps.viewmodel.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import mx.edu.utez.uiii_t7.viewmodel.MapViewModel

@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel()
) {
    val allPoints by viewModel.allTripPoints.collectAsState()
// Agrupamos puntos por tripId para dibujar líneas separadas
    val pointsByTrip = allPoints.groupBy { it.tripId }
    val cameraPositionState = rememberCameraPositionState {
// Centrar la cámara en el primer punto (si existe)
        allPoints.firstOrNull()?.let {
            position = CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 10f)
        }
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        pointsByTrip.forEach { (tripId, points) ->
            val latLngList = points.map { LatLng(it.latitude, it.longitude) }
            if (latLngList.size >= 2) {
                Polyline(
                    points = latLngList,

// Puedes añadir colores aleatorios si quieres

                )
            }
        }
    }
}