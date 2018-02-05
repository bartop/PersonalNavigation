package pl.polsl.student.personalnavigation.model

interface Marker {
    val name: String
    val position: Position
    val gender: Gender
    val skill: Skill
}