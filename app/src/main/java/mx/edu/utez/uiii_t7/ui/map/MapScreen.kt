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
import kotlin.math.abs

@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel()
) {
    val allPoints by viewModel.allTripPoints.collectAsState()
    val context = LocalContext.current
    val pointsByTrip = allPoints.groupBy { it.tripId }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
            }
        },
        update = { map ->
            map.overlays.clear()

            val validPoints = allPoints.filter { it.latitude != 0.0 && it.longitude != 0.0 }

            if (validPoints.isNotEmpty()) {
                // 📍 Calcular centro
                val avgLat = validPoints.map { it.latitude }.average()
                val avgLon = validPoints.map { it.longitude }.average()
                val center = GeoPoint(avgLat, avgLon)

                // 📏 Calcular dispersión de puntos
                val minLat = validPoints.minOf { it.latitude }
                val maxLat = validPoints.maxOf { it.latitude }
                val minLon = validPoints.minOf { it.longitude }
                val maxLon = validPoints.maxOf { it.longitude }
                val latDiff = abs(maxLat - minLat)
                val lonDiff = abs(maxLon - minLon)

                // 🔍 Zoom más cercano por defecto (más detalle)
                val zoom = when {
                    latDiff < 0.002 && lonDiff < 0.002 -> 18.5   // muy cerca, edificios visibles
                    latDiff < 0.01 && lonDiff < 0.01 -> 17.5     // zona pequeña como la UTEZ
                    else -> 15.0                                 // un poco más amplio
                }

                map.controller.setZoom(zoom)
                map.controller.setCenter(center)

                // 🟦 Dibujar las líneas
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
            } else {
                // 🌍 Sin puntos → mostrar zona de la UTEZ más de cerca
                val utez = GeoPoint(18.8516, -99.1802)
                map.controller.setCenter(utez)
                map.controller.setZoom(17.8) // 👈 Más zoom
            }

            map.invalidate()
        }
    )
}
