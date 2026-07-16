# 🐍 Snake Game (Java Swing)

A polished, modern take on the classic arcade Snake game, built entirely
in **Java 21** using **Java Swing** and **Graphics2D** — no external
libraries, no JavaFX, no assets required. Runs anywhere a JDK does.

<img width="1080" height="804" alt="image" src="https://github.com/user-attachments/assets/87667f76-ce3b-4595-9fe4-fe5884836026" />


## Description

This project recreates the classic Snake game with a clean, dark
"retro-arcade" aesthetic: a bright green rounded snake, a glowing red
(and occasionally golden) apple, a smooth background grid, a start
menu, a "3...2...1...Go!" countdown, pause/resume, and a persistent
local high score. The rendering loop runs at a stable 60 FPS while the
snake's actual movement speed increases gradually as you score points,
giving the game a satisfying difficulty curve without any stutter or
flicker.

It's written with beginners in mind — every class has a single clear
responsibility and the code is heavily commented — while still being
clean enough to showcase as a portfolio project.

## Features

- ✅ Smooth 60 FPS render loop, decoupled from snake movement speed
- ✅ Keyboard controls: Arrow Keys **and** WASD
- ✅ Random food generation that never spawns on the snake
- ✅ Snake grows after eating food
- ✅ Live score counter + persistent high score (saved to `highscore.txt`)
- ✅ Pause (`P`) and Resume
- ✅ Restart (`R`)
- ✅ Full Game Over screen with score / high score
- ✅ Animated Start Menu
- ✅ Countdown sequence before each round (`3...2...1...Go!`)
- ✅ Sound effects using `java.awt.Toolkit` (no audio files needed)
- ✅ Dark theme, rounded snake segments, soft grid lines, clean fonts
- ✅ Small particle-burst animation when food is eaten
- ✅ **Bonus:** Golden food worth 5 points (12% spawn chance)
- ✅ **Bonus:** Speed increases automatically every 5 points scored
- ✅ No external dependencies — pure JDK

## Folder Structure

```
SnakeGame/
│
├── src/
│   ├── Main.java         # Application entry point
│   ├── GameFrame.java    # Top-level JFrame window
│   ├── GamePanel.java    # Game loop, rendering, state machine, input
│   ├── Snake.java        # Snake body, movement, growth, collisions
│   ├── Food.java         # Food position + golden food logic
│   └── Direction.java    # Movement direction enum
│
└── README.md
```

*(A `highscore.txt` file will be created automatically next to the
compiled classes the first time you beat a high score — this is your
local save file, not something you need to create yourself.)*

## Controls

| Key                | Action              |
|--------------------|---------------------|
| Arrow Keys / WASD  | Move the snake      |
| P                  | Pause / Resume      |
| R                  | Restart the game    |
| ESC                | Exit the game       |
| Enter / Space      | Start from the menu |

## Requirements

- **Java 17 or newer** (built and tested on Java 21)
- No external libraries — pure Java Swing/AWT

## How to Compile (Command Line)

From the `SnakeGame/` project root:

```bash
mkdir -p bin
javac -d bin src/*.java
```

This compiles all `.java` files from `src/` into `.class` files inside
a new `bin/` folder.

## How to Run (Command Line)

```bash
java -cp bin Main
```

The game window should open immediately, centered on your screen,
showing the Start Menu.

## How to Run in VS Code

1. **Install the "Extension Pack for Java"** from the VS Code
   Marketplace (by Microsoft) if you haven't already — this gives you
   Java IntelliSense, build, and run support.
2. **Open the `SnakeGame` folder** in VS Code: `File → Open Folder...`
   and select the `SnakeGame/` directory (the one containing `src/`).
3. VS Code should automatically detect it as a Java project. If asked,
   let it create a `.vscode/` config or a Java project structure.
4. Open `src/Main.java`.
5. Click the **▶ Run** button that appears above the `main` method
   (or right-click the file → **Run Java**).
6. The game window will launch. No additional configuration is
   required — VS Code's Java extension handles compilation for you.

> If VS Code doesn't detect the source folder automatically, you can
> add a `.vscode/settings.json` with:
> ```json
> { "java.project.sourcePaths": ["src"] }
> ```

## How to Package into a Runnable JAR

From the `SnakeGame/` project root, after compiling into `bin/`:

```bash
cd bin
jar cfe ../SnakeGame.jar Main *.class
cd ..
```

- `cfe` = **c**reate a new jar, with a **f**ile name, using an **e**ntry
  point (main class).
- `Main` tells the JAR which class contains `public static void main`.

Run the packaged JAR from the project root with:

```bash
java -jar SnakeGame.jar
```

You can now distribute `SnakeGame.jar` as a single file — anyone with
a JRE installed can double-click it (or run the command above) to play.

## Screenshots

| Start Menu | Gameplay | Game Over |
|------------|----------|-----------|
| <img width="892" height="677" alt="Screenshot 2026-07-16 204132" src="https://github.com/user-attachments/assets/ea589fbf-c337-4b0b-a6d6-e0685a02fb6b" />
| <img width="1080" height="808" alt="image" src="https://github.com/user-attachments/assets/e86f4885-bd48-41c9-b2a5-e81a54194ca6" />
 |<img width="1080" height="820" alt="image" src="https://github.com/user-attachments/assets/0eb8acc9-049b-4dce-9cf0-1400e2b390f4" />
|

## Future Improvements

- [ ] Difficulty selector (Easy / Normal / Hard) on the start menu
- [ ] Obstacles / walls that spawn mid-map for extra challenge
- [ ] Multiple selectable maps/arenas
- [ ] Light theme toggle alongside the current dark theme
- [ ] Background music toggle (would require bundling an audio file)
- [ ] Full-screen / resizable window support
- [ ] On-screen mobile-style touch controls
- [ ] Online leaderboard instead of local-only high score

## Tech Notes

- The game loop uses a single `javax.swing.Timer` firing at ~60 FPS to
  drive rendering. Snake *movement* is throttled separately using a
  millisecond timestamp check, which is what allows the render loop to
  stay smooth (60 FPS) even as the snake's logical speed increases.
- Sound effects use `Toolkit.getDefaultToolkit().beep()` so the game
  has zero external asset dependencies — everything needed to run is
  in the source code.
- High score is persisted with `java.nio.file.Files`, writing a plain
  integer to `highscore.txt` in the working directory.

---

Enjoy the game, and feel free to fork/extend it! 🎮
