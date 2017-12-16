package pl.polsl.student.personalnavigation

data class DefaultMarker(
        override val id: Long,
        override val name: String,
        override val position: Position
) : Marker