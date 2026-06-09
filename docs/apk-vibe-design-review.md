# APK UX review, Digital Mindfulness Android

## Review framework

1. Permission clarity, users need to know why each permission is requested, what it can see, and what happens if they say no.
2. Trust before action, before asking users to enable a powerful setting, the app must explain what is safe, what is risky, and what stays under user control.
3. Onboarding is the risk point, first run must be calm, step by step, and reversible.
4. Jargon blocks adoption, terms like APK, Accessibility Service, sync endpoint, and versionCode need plain explanations in the main flow.
5. Progressive disclosure, show the simple path first and keep advanced configuration out of the way.
6. Wide user range, the app must work for developers, privacy focused users, and nontechnical testers.
7. Invisible tech, visible state, users need to know what is enabled, what is tracked, what synced, and what changed.
8. Friction must earn its place, warnings and buttons should protect from real mistakes.
9. Education belongs in context, the app should teach at the moment of need.
10. Data control is product design, users need controls for local data, sync, reset, and updates.

## Context reconstructed

Digital Mindfulness is a GrapheneOS and Android APK that counts visits to selected distracting websites by local calendar day. It is for Harrison first, with likely future use by privacy conscious testers. The key first run flow is install APK through Obtainium, open the app, understand the privacy tradeoff, enable Accessibility, then check daily visit counts.

## First impressions

The first MVP worked technically, but the visible copy still read like a developer prototype. It asked the user to enable Accessibility before clearly explaining what that permission can observe, what data stays local, what sync means, or what counts as a visit. For a privacy sensitive APK, that trust gap matters more than visual polish.

## Findings ordered by screen or flow

### Home and first run

Principles applied: permission clarity, trust before action, onboarding is the risk point, data control is product design.

The old home screen said the app logs domains locally via Logcat. That is implementation language and it does not help a phone user decide whether to trust the app. We replaced it with a plain explanation of what the app does, what is enabled, which sites are tracked, whether sync is on, and what the Accessibility permission can observe.

### Accessibility setup

Principles applied: permission clarity, education belongs in context, friction must earn its place.

The app now warns before opening Android Accessibility Settings: Android will say the app can see screen content, and that is true. The copy says Digital Mindfulness uses the access only to look for supported browser pages and count visits to tracked sites. This is intentionally plain rather than falsely reassuring.

### Dashboard state

Principles applied: invisible tech, visible state, progressive disclosure.

The dashboard now shows Accessibility status, sync status, tracked sites, last counted visit, and today's totals. Empty state copy tells the user what to do next instead of leaving a blank screen.

### Data controls

Principles applied: data control is product design, prevention over recovery.

The app now includes a reset button for today's counts. This is still minimal, but it introduces an obvious local data control before sync is added.

### Install and update path

Principles applied: trust before action, jargon blocks adoption.

The docs already separate Obtainium app updates from data sync. The review confirms that split is right. The current release is debug signed, so it must remain labeled as a personal testing build until a stable release signing key is used.

## Priority actions implemented

1. Rewrote the main screen around plain language, trust, and current state.
2. Added explicit Accessibility permission rationale before opening settings.
3. Added visible sync status, tracked sites, last visit, empty state guidance, and reset today's counts.

## Privacy and data control lens

The app now states that sync is off and data stays on device in this version. It also names the powerful permission and explains what Android's warning means. Future sync work should keep this same pattern: local first, opt in sync, clear account and device status, and simple delete or reset controls.

## Non native English speaker lens

Changed developer and technical phrasing into shorter user copy:

Original: MVP: enable Accessibility Service, then browse x.com or youtube.com in Vanadium/Brave/Chrome/Firefox. Current prototype logs detected domains locally via Logcat.

Replacement: Count how often you visit distracting websites. The app counts visits by calendar day, not time spent.

Original concept: Accessibility Service.

Replacement in context: Android will warn that this app can see screen content. That is true.

Original concept: sync backend deferred.

Replacement in UI: Sync: Off, data stays on this device.

## What is working well

The app has a narrow job and the visit semantics are easy to explain. The local first posture fits GrapheneOS users. Obtainium plus GitHub Releases is a good update path for personal testing, as long as debug signing is replaced before the app becomes something Harrison relies on long term.
