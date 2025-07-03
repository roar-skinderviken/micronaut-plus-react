package no.javatec

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller(value = "/test")
class TestController {

    @Get
    fun getSomeTestData(): String = "Hello World"
}