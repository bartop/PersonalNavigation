package pl.polsl.student.personalnavigation.model


class DistanceMarker(
        override val name: String,
        override val id: Long,
        override val position: Position,
        override val gender: Gender,
        override val skill: Skill,
        val distance: Double
) : IdentifiableMarker