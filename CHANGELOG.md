# 0.2.1 (WIP)

* Add icons
* Allow textures to be recoloured
* Try to re-use the current syncid when showing a new screen
* Fix `TexturedScreenHandler#transferSlot()` causing infinite loop
* Fix `Widget`s not always getting the parent `ScreenBuilder` instance set
* Allow `Widget`s to register themselves
* Fix `TextureWidget`s adding themselves to a `TextBuilder` multiple times
* Fix render issues by using at least 1 splitting character
* Add the `space` as a hardcoded 4px-wide whitespace character
* Improve getting a slot's coordinates