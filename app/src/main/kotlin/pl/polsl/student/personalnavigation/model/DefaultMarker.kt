package pl.polsl.student.personalnavigation.model


class DefaultMarker(
        override val name: String,
        override val position: Position,
        override val gender: Gender,
        override val skill: Skill
) : Marker