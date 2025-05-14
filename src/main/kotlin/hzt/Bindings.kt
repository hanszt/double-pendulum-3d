package hzt

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.ObjectProperty

fun <T> ObjectProperty<T>.booleanBinding(predicate: (T) -> Boolean): BooleanBinding = Bindings
    .createBooleanBinding({ predicate(get()) }, this)