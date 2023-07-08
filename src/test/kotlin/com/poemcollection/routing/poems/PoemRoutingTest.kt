package com.poemcollection.routing.poems

import com.poemcollection.modules.poems.PoemController
import com.poemcollection.modules.poems.poemRouting
import com.poemcollection.routing.BaseRoutingTest
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.mockk.mockk
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.koin.dsl.module

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PoemRoutingTest : BaseRoutingTest() {

    private val poemController: PoemController = mockk()

    @BeforeAll
    fun setup() {
        koinModules = module {
            single { poemController }
        }

        moduleList = {
            install(Routing) {
                poemRouting()
            }
        }
    }

    @BeforeEach
    fun clearMocks() {
        io.mockk.clearMocks(poemController)
    }
}