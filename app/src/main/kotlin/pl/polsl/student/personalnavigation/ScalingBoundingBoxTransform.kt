package pl.polsl.student.personalnavigation

import org.osmdroid.util.BoundingBox


class ScalingBoundingBoxTransform(
        private val scale: Float
): (BoundingBox) -> BoundingBox {
    override fun invoke(boundingBox: BoundingBox): BoundingBox {
        return boundingBox.clone().increaseByScale(scale)
    }
}