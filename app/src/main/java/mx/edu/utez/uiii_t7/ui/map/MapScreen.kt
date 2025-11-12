package mx.edu.utez.uiii_t7.ui.map

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.utez.uiii_t7.viewmodel.MapViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel()
) {
    val allPoints by viewModel.allTripPoints.collectAsState()
    val context = LocalContext.current

    // Agrupar los puntos por viaje
    val pointsByTrip = allPoints.groupBy { it.tripId }

    // Mantener el controlador del mapa
    var mapView by remember { mutableStateOf<MapView?>(null) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                mapView = this
            }
        },
        update = { map ->
            // Limpiar líneas anteriores
            map.overlays.clear()

            if (allPoints.isNotEmpty()) {
                // Centrar en el primer punto
                val first = allPoints.first()
                val center = GeoPoint(first.latitude, first.longitude)
                map.controller.setZoom(15.0)
                map.controller.setCenter(center)
            }

            // Dibujar una línea (Polyline) por cada viaje
            pointsByTrip.values.forEach { tripPoints ->
                if (tripPoints.size >= 2) {
                    val line = Polyline().apply {
                        setPoints(tripPoints.map { GeoPoint(it.latitude, it.longitude) })
                        outlinePaint.color = android.graphics.Color.BLUE
                        outlinePaint.strokeWidth = 6f
                    }
                    map.overlays.add(line)
                }
            }

            map.invalidate()
        }
    )
}
