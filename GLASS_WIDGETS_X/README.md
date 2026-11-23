# Glass Widgets X

Glass Widgets X is a native Android 8.0+ home-screen widget customization app.

## Features

- Digital and analog clock widgets
- Battery percentage and charging status widget
- Date and weekday widget
- Open-Meteo weather widget
- Next-calendar-event widget
- Per-widget editable notes
- Configurable photo frame
- Android search widget
- Quick launch widget
- 24 clock design presets
- Live Studio preview
- Accent, text, secondary, background, opacity, font size and corner controls
- Named theme save/load/reset
- Apply current theme to existing supported widgets
- Stopwatch and foreground countdown service
- Android settings and calendar app shortcuts

## Build

Requirements:

- Android Studio or Gradle with JDK 17
- Android SDK Platform 35
- Android 8.0 (API 26) minimum

Build source without an APK:

```bash
gradle assembleDebug
```

The project intentionally does not include a prebuilt APK.

## Widget setup

Use the dashboard buttons to request a pinned widget on Android 8.0+ launchers. If pinning is unavailable, long-press the home screen, open Widgets, and select the desired Glass Widgets X widget.

## Permissions

Permissions are requested only when the corresponding feature is used:

- `READ_CALENDAR` for the calendar widget
- `POST_NOTIFICATIONS` for countdown notifications on Android 13+
- `INTERNET` for weather lookup

The photo widget uses persisted read access only for the image selected by the user.

## Not implemented

- True system-wide icon-pack replacement, which requires launcher-specific APIs
- An external live-wallpaper engine
- In-widget music playback/media-session controls
- Custom search-provider configuration
- iOS widgets or cross-device sync

Weather requires network access. Calendar and photo availability depend on Android permissions and launcher/provider behavior.
