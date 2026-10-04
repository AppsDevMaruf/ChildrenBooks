package com.childrenbooks.core.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.childrenbooks.R
import com.childrenbooks.core.designsystem.KidColors
import com.childrenbooks.core.designsystem.KidShapes
import com.childrenbooks.core.designsystem.KidType

/** A collected sticker: a die-cut look with a white border and a slight tilt. */
@Composable
fun StickerImage(
    @DrawableRes image: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    rotation: Float = 0f,
    isNew: Boolean = false,
    corner: Dp = 22.dp,
) {
    Box(modifier.rotate(rotation)) {
        KidCard(
            onClick = null,
            modifier = Modifier.fillMaxSize(),
            color = KidColors.Surface,
            shape = RoundedCornerShape(corner),
            role = null,
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
                    .clip(RoundedCornerShape(corner - 5.dp)),
            )
        }
        if (isNew) {
            Text(
                text = stringResource(R.string.sticker_new),
                style = KidType.Small,
                color = KidColors.Surface,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-6).dp, y = (-10).dp)
                    .background(KidColors.Sticker, KidShapes.Pill)
                    .padding(horizontal = 10.dp, vertical = 2.dp),
            )
        }
    }
}

/** A sticker slot the child has not earned yet. */
@Composable
fun StickerSlot(
    modifier: Modifier = Modifier,
    isNext: Boolean = false,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(if (isNext) KidColors.PrimaryContainer else KidColors.SurfaceMuted),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = stringResource(if (isNext) R.string.cd_sticker_next else R.string.cd_sticker_locked),
            tint = if (isNext) KidColors.Primary else KidColors.Track,
            modifier = Modifier.size(36.dp),
        )
    }
}
