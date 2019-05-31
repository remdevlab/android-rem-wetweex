package org.remdev.wetweexapp

import org.remdev.wetweex.component.Coordinator
import org.remdev.wetweex.component.CoordinatorsContainer

object DemoAppCoordinatorsContainer : CoordinatorsContainer {

    private val container = mutableMapOf<String, Coordinator<*>>()

    override fun save(id: String, coordinator: Coordinator<*>) {
        container[id] = coordinator
    }

    override fun restore(id: String): Coordinator<*>? = container[id]

    override fun remove(id: String) {
        container.remove(id)
    }

    override fun clear() {
        container.clear()
    }
}