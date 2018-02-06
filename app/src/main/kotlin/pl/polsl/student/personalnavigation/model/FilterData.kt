package pl.polsl.student.personalnavigation.model

import java.util.*

class FilterData(
        val genders: EnumSet<Gender> = EnumSet.allOf(Gender::class.java),
        val skills: EnumSet<Skill> = EnumSet.allOf(Skill::class.java)
)