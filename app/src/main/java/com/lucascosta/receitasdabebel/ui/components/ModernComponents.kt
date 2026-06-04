package com.lucascosta.receitasdabebel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val AppCardShape = RoundedCornerShape(22.dp)
val AppContentMaxWidth = 720.dp

@Composable
fun ResponsiveContent(
    modifier: Modifier = Modifier,
    maxWidth: Dp = AppContentMaxWidth,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = maxWidth),
            content = content
        )
    }
}

@Composable
fun ModernCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    val elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)

    if (onClick == null) {
        Card(
            modifier = modifier,
            shape = AppCardShape,
            colors = colors,
            elevation = elevation,
            content = content
        )
    } else {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = AppCardShape,
            colors = colors,
            elevation = elevation,
            content = content
        )
    }
}

@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier
    )
}

@Composable
fun RecipeThumbnail(
    text: String,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.tertiary
) {
    Box(
        modifier = modifier
            .size(66.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.take(1).uppercase(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun MetadataPill(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    ModernCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ScreenTopBar(
    title: String,
    subtitle: String? = null,
    action: (@Composable RowScope.() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val compact = maxWidth < 360.dp && action != null

        if (compact) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ScreenTopBarTitle(title = title, subtitle = subtitle)
                Row(modifier = Modifier.fillMaxWidth()) {
                    action?.invoke(this)
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScreenTopBarTitle(
                    title = title,
                    subtitle = subtitle,
                    modifier = Modifier.weight(1f)
                )
                action?.invoke(this)
            }
        }
    }
}

@Composable
private fun ScreenTopBarTitle(
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
