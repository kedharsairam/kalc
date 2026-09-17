package com.kraft.calculator.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.Button
import com.kraft.calculator.MainActivity

class KalcWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }

    @Composable
    private fun WidgetContent() {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .cornerRadius(16.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically,
        ) {
            Text(
                text = "Kalc",
                style = TextStyle(color = GlanceTheme.colors.onSurface),
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = "Tap to calculate",
                style = TextStyle(color = GlanceTheme.colors.onSurfaceVariant),
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Button(
                text = "Open",
                onClick = actionStartActivity<MainActivity>(),
            )
        }
    }
}

class KalcWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = KalcWidget()
}
