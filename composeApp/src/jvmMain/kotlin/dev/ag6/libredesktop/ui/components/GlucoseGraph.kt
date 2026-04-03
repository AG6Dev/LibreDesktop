package dev.ag6.libredesktop.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.*
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.compose.cartesian.decoration.HorizontalBox
import com.patrykandpatrick.vico.compose.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun GlucoseGraphView(
    chartModel: CartesianChartModel,
    lowTarget: Double? = null,
    highTarget: Double? = null,
    modifier: Modifier = Modifier
) {
    ProvideVicoTheme(
        theme = rememberM3VicoTheme(
            columnCartesianLayerColors = listOf(MaterialTheme.colorScheme.primary),
            lineCartesianLayerColors = listOf(MaterialTheme.colorScheme.primary)
        )
    ) {
        val timeFormatter = remember {
            DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())
        }
        val rangeFill = rememberShapeComponent(
            fill = Fill(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
        )
        val rangeLine = rememberLineComponent(
            fill = Fill(MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
            thickness = 1.dp
        )
        val scrollState = rememberVicoScrollState(scrollEnabled = false)
        val zoomState = rememberVicoZoomState(
            zoomEnabled = false,
            initialZoom = Zoom.Content,
            minZoom = Zoom.Content,
            maxZoom = Zoom.Content
        )
        val chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(guideline = rememberLineComponent()),
            bottomAxis = HorizontalAxis.rememberBottom(
                guideline = null,
                valueFormatter = { _, value, _ ->
                    timeFormatter.format(Instant.ofEpochMilli(value.toLong()))
                }
            ),
            fadingEdges = rememberFadingEdges(),
            decorations = listOfNotNull(
                if (lowTarget != null && highTarget != null) {
                    HorizontalBox(
                        y = { lowTarget..highTarget },
                        box = rangeFill
                    )
                } else {
                    null
                },
                lowTarget?.let { target ->
                    HorizontalLine(
                        y = { target },
                        line = rangeLine
                    )
                },
                highTarget?.let { target ->
                    HorizontalLine(
                        y = { target },
                        line = rangeLine
                    )
                }
            )
        )

        CartesianChartHost(
            chart = chart,
            model = chartModel,
            modifier = modifier
                .fillMaxWidth()
                .height(260.dp),
            scrollState = scrollState,
            zoomState = zoomState
        )
    }
}
