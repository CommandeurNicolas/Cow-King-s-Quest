package com.nicolascommandeur.cowkingsquest.view.components.guide

import com.nicolascommandeur.cowkingsquest.model.guide.GuideImage
import com.nicolascommandeur.cowkingsquest.view.components.image.ViewerImage

internal fun GuideImage.toViewerImage() = ViewerImage(
    id = id,
    title = title,
    resource = resource,
    contentDescription = "$title. $caption",
    caption = caption,
    sourceCredit = sourceCredit
)
