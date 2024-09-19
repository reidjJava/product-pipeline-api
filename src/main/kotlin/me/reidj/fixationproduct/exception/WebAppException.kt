package me.reidj.fixationproduct.exception

import org.springframework.http.HttpStatus

class WebAppException(val httpStatus: HttpStatus, override val message: String) : RuntimeException(message)
