package com.childrenbooks.core.designsystem

import com.childrenbooks.data.content.WorldId

/** Each learning world has one colour family, so children can tell them apart without reading. */
fun WorldId.palette(): WorldPalette = when (this) {
    WorldId.Letters -> WorldPalettes.Letters
    WorldId.Numbers -> WorldPalettes.Numbers
    WorldId.Time -> WorldPalettes.Time
    WorldId.Colours -> WorldPalettes.Colours
    WorldId.Nature -> WorldPalettes.Nature
    WorldId.Food -> WorldPalettes.Food
    WorldId.Things -> WorldPalettes.Things
}
