package pl.polsl.student.personalnavigation.model

interface LoginService {
    fun login(): AuthenticationData
}