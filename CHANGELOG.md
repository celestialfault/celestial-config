# Changelog

All notable changes to this project (as of 1.0) will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project (loosely) adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 1.0.1 - 2024-12-29

### Added

- This changelog file

### Changed

- Rewrote a bunch of documentation
- Made `AbstractConfig.dirty` mutable
- `AbstractConfig.load` and `.save` are now `open`
- `AbstractConfig.dirty` can now be manually set, in case you rely on it and use mutable properties (like maps or lists)

### Fixed

- Made `Serializer.number()` actually coerce deserialized values into the supplied range

### 1.0 - 2024-12-15

### Added

- Simple migrations utility

### Changed

- **Breaking:** Rewrote how properties work to use `Serializer`s, instead of requiring separate property types
  for every built-in type

### Deprecated

- The setter on `AbstractConfig.indent` is now deprecated due to its move to the class constructor
