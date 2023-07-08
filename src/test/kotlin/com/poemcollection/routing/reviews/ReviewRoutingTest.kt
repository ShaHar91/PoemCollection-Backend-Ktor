package com.poemcollection.routing.reviews

import com.poemcollection.modules.reviews.ReviewController
import com.poemcollection.modules.reviews.reviewRouting
import com.poemcollection.routing.BaseRoutingTest
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.mockk.mockk
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.koin.dsl.module

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReviewRoutingTest : BaseRoutingTest() {

    private val reviewController: ReviewController = mockk()

    @BeforeAll
    fun setup() {
        koinModules = module {
            single { reviewController }
        }

        moduleList = {
            install(Routing) {
                reviewRouting()
            }
        }
    }

    @BeforeEach
    fun clearMocks() {
        io.mockk.clearMocks(reviewController)
    }
}