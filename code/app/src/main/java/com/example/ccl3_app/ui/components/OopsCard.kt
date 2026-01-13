package com.example.ccl3_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.ccl3_app.ui.theme.CardBackground

@Composable
fun OopsCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .background(CardBackground, RoundedCornerShape(20.dp))
            .padding(16.dp),
        content = content
    )
}

