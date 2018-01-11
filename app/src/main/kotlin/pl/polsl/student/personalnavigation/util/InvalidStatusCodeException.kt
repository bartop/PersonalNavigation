package pl.polsl.student.personalnavigation.util

class InvalidStatusCodeException(
        status: Int,
        validStatusRange: ClosedRange<Int>,
        url: String
): Exception(
        "Invalid HTTP status code for request to $url! Status: $status; Accepted range: $validStatusRange"
)