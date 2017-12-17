package pl.polsl.student.personalnavigation

import java8.util.Optional


interface LoginService {
    fun login(): Optional<Exception>
}