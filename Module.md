# Module celestial-config

A simple wrapper around GSON for use with configuration files, written in Kotlin.

# Package me.celestialfault.celestialconfig

Core configuration classes

# Package me.celestialfault.celestialconfig.properties

Commonly used built-in property types

Note that most of these classes aren't intended to be used directly, but should instead be created through the `Property.type()` methods,
such as `Property#int(key: String, default: Int? = null)`
