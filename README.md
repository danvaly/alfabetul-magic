<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" alt="Platform"/>
  <img src="https://img.shields.io/badge/Min%20SDK-26-blue" alt="Min SDK"/>
  <img src="https://img.shields.io/badge/Kotlin-1.9.22-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white" alt="Compose"/>
  <img src="https://img.shields.io/badge/License-Apache%202.0-orange" alt="License"/>
</p>

<h1 align="center">Alfabetul Magic</h1>

<p align="center">
  <strong>A fun, Duolingo-inspired Android app that teaches kids the Romanian alphabet through interactive games, handwriting practice, and animated stories.</strong>
</p>

<p align="center">
  <em>Invata alfabetul romanesc prin joaca!</em>
</p>

---

## Overview

**Alfabetul Magic** is a native Android application built entirely with **Jetpack Compose** and **Material 3**, designed to help young children (ages 3-7) learn the Romanian alphabet. The app features a vibrant, playful UI with floating animals, bouncing animations, and a Duolingo-style 3D card design that keeps kids engaged while they learn letters, diacritics, words, and sentences.

Every screen is landscape-optimized for tablet and phone use, with large touch targets, colorful feedback, and Text-to-Speech pronunciation in Romanian.

---

## Features

### Core Learning Modes

| Mode | Description |
|------|-------------|
| **Invata Literele** | Browse all 31 Romanian letters with pronunciation, example words, and emoji illustrations |
| **Scrie Literele** | Handwriting practice with a guided tracing canvas and zone-based scoring (1-3 stars) |
| **Formeaza Silabe** | Combine consonants and vowels to form syllables and simple words |
| **Povesti cu Litere** | Animated letter stories with TTS narration for each letter |

### Games (8 mini-games)

| Game | Description |
|------|-------------|
| **Sparge Baloanele** | Pop floating balloons that contain the target letter |
| **Potriveste Literele** | Memory card game matching uppercase to lowercase letters |
| **Ce Litera Auzi?** | Listen to a letter sound and pick the correct one |
| **Cuvant si Imagine** | Match Romanian words to their emoji picture |
| **Diacritice** | Practice distinguishing diacritical marks (a, a, i, s, t) |
| **Construieste Propozitia** | Arrange scrambled words into correct sentence order |
| **Jocul Rimelor** | Find pairs of rhyming Romanian words |
| **Ordoneaza Alfabetul** | Tap letters in the correct alphabetical sequence |

### Additional Features

- **Provocarea Zilei** - A daily word-of-the-day challenge that rotates through 31 words
- **Progresul Meu** - Track stars earned, letters learned, games played, and streaks
- **Panou Parinti** - Parent dashboard (behind a math-problem gate) with stats overview and difficulty settings
- **3 Difficulty Levels** - Beginner (slower, hints), Intermediate (balanced), Advanced (faster, more options)
- **Confetti Celebrations** - Colorful particle animations on achievements
- **Romanian TTS** - Natural text-to-speech pronunciation for all letters and words

---

## Screenshots

> The app uses a Duolingo-inspired design with 3D card effects, floating animal emojis, and vibrant gradient backgrounds on every screen.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **UI** | Jetpack Compose + Material 3 |
| **Navigation** | Compose Navigation (type-safe routes) |
| **Database** | Room (v2.6.1) with KSP annotation processing |
| **Async** | Kotlin Coroutines + Flow |
| **Audio** | Android TextToSpeech (Romanian locale) |
| **Architecture** | Single-Activity, Repository pattern |
| **Build** | Gradle KTS, AGP 8.2.2 |
| **Language** | Kotlin 1.9.22, Java 17 |
| **CI/CD** | GitHub Actions (build + release) |

---

## Project Structure

```
app/src/main/java/com/danvaly/alfabet/
├── MainActivity.kt                    # App entry point
├── AlfabetApplication.kt             # Application class
├── audio/
│   └── SoundManager.kt               # TTS wrapper for Romanian pronunciation
├── data/
│   ├── RomanianAlphabet.kt            # All 31 letters, words, stories, rhymes, sentences
│   ├── DifficultyManager.kt           # Beginner / Intermediate / Advanced settings
│   ├── local/
│   │   ├── AppDatabase.kt             # Room database (v2)
│   │   └── ProgressDao.kt             # Data access queries
│   ├── model/
│   │   ├── Letter.kt                  # Letter data model
│   │   └── LetterProgress.kt          # Progress + stats entities
│   └── repository/
│       └── ProgressRepository.kt      # Repository layer with Flows
├── navigation/
│   ├── Screen.kt                      # Sealed class route definitions
│   └── NavGraph.kt                    # Navigation graph wiring
└── ui/
    ├── components/
    │   ├── ConfettiAnimation.kt       # 60-particle celebration effect
    │   ├── TracingCanvas.kt           # Handwriting canvas with scoring
    │   ├── LetterCard.kt              # Reusable letter display card
    │   └── StarRating.kt              # Star rating component
    ├── screens/
    │   ├── HomeScreen.kt              # Main menu with 7 mode cards
    │   ├── LearnScreen.kt             # Alphabet grid browser
    │   ├── LetterDetailScreen.kt      # Individual letter deep-dive
    │   ├── TraceScreen.kt             # Handwriting practice
    │   ├── TraceSelectorScreen.kt     # Letter picker for tracing
    │   ├── SyllableScreen.kt          # Syllable formation
    │   ├── GamesMenuScreen.kt         # Games hub (2 rows of 4 games)
    │   ├── BalloonPopScreen.kt        # Balloon popping game
    │   ├── MatchGameScreen.kt         # Memory matching game
    │   ├── QuizGameScreen.kt          # Audio quiz game
    │   ├── WordMatchScreen.kt         # Word-to-picture matching
    │   ├── LetterStoryScreen.kt       # Animated letter stories
    │   ├── DiacriticsScreen.kt        # Diacritics practice
    │   ├── DailyChallengeScreen.kt    # Daily word challenge
    │   ├── SentenceBuildScreen.kt     # Sentence construction
    │   ├── RhymingGameScreen.kt       # Rhyming word game
    │   ├── AlphabetOrderScreen.kt     # Letter ordering game
    │   ├── ParentDashboardScreen.kt   # Parent stats + settings
    │   └── ProgressScreen.kt          # Progress & achievements
    └── theme/
        ├── Color.kt                   # Duolingo-inspired color palette
        ├── Theme.kt                   # Material 3 theme setup
        └── Type.kt                    # Typography definitions
```

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34

### Build & Run

```bash
# Clone the repository
git clone https://github.com/danvaly/alfabet.git
cd alfabet

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew testDebugUnitTest
```

### CI/CD

The project includes two GitHub Actions workflows:

- **`build.yml`** - Runs on every push and PR: lint, unit tests, debug APK build
- **`release.yml`** - Triggered by `v*` tags: builds a signed release APK and creates a GitHub Release

To create a release:
```bash
git tag v1.0.0
git push origin v1.0.0
```

---

## Romanian Alphabet Coverage

The app covers all **31 letters** of the Romanian alphabet:

```
A  a      B  b      C  c      D  d      E  e      F  f      G  g
H  h      I  i      J  j      K  k      L  l      M  m      N  n
O  o      P  p      Q  q      R  r      S  s      T  t      U  u
V  v      W  w      X  x      Y  y      Z  z
```

**Plus diacritical letters:**
```
A  a      A  a      I  i      S  s      T  t
```

Each letter includes:
- Uppercase and lowercase forms
- Phonetic sound description
- 3-4 example words with emoji illustrations
- A mini story for narrative learning
- Vowel / consonant / diacritic classification
- Color coding for visual distinction

---

## Content Data

| Content Type | Count | Description |
|-------------|-------|-------------|
| Letters | 31 | Full Romanian alphabet with diacritics |
| Example Words | 90+ | Words with emoji illustrations per letter |
| Letter Stories | 31 | Short narrative stories for each letter |
| Rhyming Pairs | 15 | Word pairs for the rhyming game |
| Sentences | 15 | Simple sentences for the building game |
| Diacritic Exercises | 10 | Comparison drills for special characters |
| Daily Words | 31 | Rotating word-of-the-day entries |

---

## Architecture

```
                    ┌─────────────┐
                    │ MainActivity │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │  NavGraph   │
                    └──────┬──────┘
                           │
              ┌────────────┼────────────┐
              │            │            │
        ┌─────▼─────┐ ┌───▼────┐ ┌────▼─────┐
        │  Screens   │ │ Theme  │ │Components│
        │ (18 total) │ │        │ │          │
        └─────┬──────┘ └────────┘ └──────────┘
              │
    ┌─────────┼─────────┐
    │         │         │
┌───▼──┐ ┌───▼──────┐ ┌▼───────────┐
│Sound │ │Repository│ │Difficulty  │
│Mngr  │ │          │ │Manager     │
└──────┘ └───┬──────┘ └────────────┘
             │
         ┌───▼──┐
         │ Room │
         │  DB  │
         └──────┘
```

- **Single Activity** with Compose Navigation managing all 18 screens
- **Repository pattern** abstracts Room database access behind Kotlin Flows
- **SoundManager** wraps Android TTS for Romanian pronunciation
- **DifficultyManager** provides speed/options/hint settings per level
- **No ViewModels** - state is managed at the composable level with `remember` and `mutableStateOf` for simplicity appropriate to the app's scope

---

## License

```
Copyright 2024 danvaly

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
