package com.kitabeli.ae.utils

import android.content.Context
import android.util.AttributeSet
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.ModelView.Size
import com.google.android.flexbox.FlexboxLayoutManager

@ModelView(saveViewState = true, autoLayout = Size.MATCH_WIDTH_WRAP_HEIGHT)
class FlexboxView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0,
) : Carousel(context, attr, defStyle) {

    init {
        setHasFixedSize(true)
        setPaddingDp(0)
        layoutManager = FlexboxLayoutManager(context)
    }


}
