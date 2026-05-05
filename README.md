# LibreDesktop

A simple desktop app (Kotlin + Compose Multiplatform) for viewing LibreView glucose data.

Only supported and tested on Windows currently.

## What it does

- Authenticates against the LibreView API
- Fetches the current glucose reading
- Fetches graph/history readings
- Displays trend arrows and a glucose graph with target-range shading
- Refreshes readings on a repeating 1-minute interval

## Prerequisites

- JDK 17+ (recommended)

## Features

- [x] Authentication with LibreView API
- [x] Fetch and display current glucose reading
- [x] Fetch and display glucose graph/history
- [x] Notifications when glucose is out of target range
- [ ] Can view multiple patients
- [ ] Customizable taskbar widget with current glucose and trend arrow

Potential future features:

- [ ] Allow for nightscout integration