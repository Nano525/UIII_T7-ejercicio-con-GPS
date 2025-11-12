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

    // Agrupar los puntos por viaje, como en tu versión original
    val pointsByTrip = allPoints.groupBy { it.tripId }

    // AndroidView te permite usar vistas clásicas (como MapView) dentro de Compose
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)

                // Si hay puntos, centramos y dibujamos
                if (allPoints.isNotEmpty()) {
                    val first = allPoints.first()
                    controller.setCenter(GeoPoint(first.latitude, first.longitude))
                    controller.setZoom(10.0)

                    // Dibujar las rutas (una línea por viaje)
                    pointsByTrip.values.forEach { tripPoints ->
                        val polyline = Polyline().apply {
                            setPoints(tripPoints.map { GeoPoint(it.latitude, it.longitude) })
                        }
                        overlays.add(polyline)
                    }
                }
            }
        },
        update = { mapView ->
            // Actualizar rutas si cambian los puntos
            mapView.overlays.clear()
            pointsByTrip.values.forEach { tripPoints ->
                val polyline = Polyline().apply {
                    setPoints(tripPoints.map { GeoPoint(it.latitude, it.longitude) })
                }
                mapView.overlays.add(polyline)
            }
            mapView.invalidate()
        }
    )
}
