package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LocationItem

@Composable
fun QuickLocationSelector(
    selectedLocation: LocationItem,
    favoriteLocations: List<LocationItem>,
    popularLocations: List<LocationItem>,
    onSelectLocation: (LocationItem) -> Unit,
    onAddLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Combine favorites first, followed by popular locations not already in favorites
    val displayedLocations = buildList {
        addAll(favoriteLocations)
        popularLocations.forEach { pop ->
            if (none { it.name.equals(pop.name, ignoreCase = true) && it.countryCode.equals(pop.countryCode, ignoreCase = true) }) {
                add(pop)
            }
        }
    }

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(displayedLocations, key = { "${it.name}_${it.countryCode}" }) { location ->
            val isSelected = (location.name.equals(selectedLocation.name, ignoreCase = true) &&
                    location.countryCode.equals(selectedLocation.countryCode, ignoreCase = true))

            val isFav = favoriteLocations.any {
                it.name.equals(location.name, ignoreCase = true) && it.countryCode.equals(location.countryCode, ignoreCase = true)
            }

            val bgColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.18f),
                label = "chip_bg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color(0xFF0F172A) else Color.White,
                label = "chip_text"
            )

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onSelectLocation(location) }
                    .testTag("location_chip_${location.name}"),
                shape = RoundedCornerShape(20.dp),
                color = bgColor,
                shadowElevation = if (isSelected) 4.dp else 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = location.flagEmoji,
                        fontSize = 16.sp
                    )
                    Text(
                        text = location.name,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = textColor
                        )
                    )
                    if (isFav) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Saved location",
                            tint = if (isSelected) Color(0xFFF59E0B) else Color(0xFFFFD166),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // "+ Add City" button chip
        item {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onAddLocationClick() }
                    .testTag("quick_add_city_chip"),
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Search more cities",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "More Cities",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}
