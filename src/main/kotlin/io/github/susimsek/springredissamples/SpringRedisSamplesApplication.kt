package io.github.susimsek.springredissamples

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class SpringRedisSamplesApplication

fun main(args: Array<String>) {
    runApplication<SpringRedisSamplesApplication>(*args)
}