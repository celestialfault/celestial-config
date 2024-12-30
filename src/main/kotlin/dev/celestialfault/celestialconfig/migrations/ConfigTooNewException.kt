package dev.celestialfault.celestialconfig.migrations

/**
 * Thrown when loading an [dev.celestialfault.celestialconfig.AbstractConfig] with an attached
 * [Migrations] if the loaded version is newer than the version [Migrations] knows about
 */
class ConfigTooNewException internal constructor(message: String? = null) : RuntimeException(message)
