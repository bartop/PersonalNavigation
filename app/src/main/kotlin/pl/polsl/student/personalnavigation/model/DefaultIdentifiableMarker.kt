package pl.polsl.student.personalnavigation.model

data class DefaultIdentifiableMarker(
        override val id: Long,
        override val name: String,
        override val position: Position
) : IdentifiableMarker