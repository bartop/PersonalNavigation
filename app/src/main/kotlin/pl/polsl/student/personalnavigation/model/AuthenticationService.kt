package pl.polsl.student.personalnavigation.model

interface AuthenticationService {
    fun authenticationHeaders(): Map<String, String>
    fun authentication(): AuthenticationData
    fun invalidateAuthentication()
}