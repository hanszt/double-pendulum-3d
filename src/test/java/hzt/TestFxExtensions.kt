package hzt

import javafx.scene.Node
import org.testfx.service.query.NodeQuery

inline fun <reified T : Node> NodeQuery.queryAs(): T = queryAs(T::class.java)
