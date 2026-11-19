# Geo-Utils (legacy)

> **This project is not active.** It is kept for reference only and gets no
> further development, fixes or releases.

This is an early prototype of the Geo-Utils Android app, built with Views,
Fragments and XML layouts. Development continues in a rewrite:

**→ [chlewey/geoutils](https://github.com/chlewey/geoutils)**
(Jetpack Compose, Hilt, Room; landing page at <https://geoutils.ilecto.app>)

## What moves to geoutils

The ideas being developed here will be carried over to geoutils, where they
are tracked in its [ROADMAP](https://github.com/chlewey/geoutils/blob/main/ROADMAP.md)
and marked *(from geoutils-legacy)*:

- **Visual identity:** parts of the launcher icon artwork, and the file-format
  and folder icons (`app/src/main/res/drawable/ic_fileformat_*`,
  `ic_foldertype_*`). The PNG, JPEG and TIFF icons are badges meant to be
  overlaid on an image's thumbnail.
- **File management:** the project gallery, the file browser, and the file
  index in Room (files known to the app and which projects use them).
- **Project files (`.gup`):** a single file bundling a project's resources, a
  thumbnail and its list of actions (gzipped JSON).

The KML helpers in `lib/fix` (commented out here) are covered by the
geoutils roadmap as well, ported from the original kml2svg tools.
