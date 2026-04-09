# Final Project: Developing Games in Java Using Swing and Agentic AI

**Course:** COMP 2243 — Introduction to Programming  
**Duration:** 3 weeks  
**Deliverable due:** End of each week (Friday by 11:59 PM)

---

## Overview

In this project you will build a working arcade game in Java — without starting from scratch. Instead, you will use an **agentic AI** (such as Claude or GitHub Copilot) to generate, debug, and extend your code. Your job is to *direct* the AI clearly, evaluate what it gives you, fix what's broken, and push further on your own.

Each week has a theme, a set of **seed prompts** to get you started, and room to take the project as far as you want. You are not expected to understand every line of code the AI writes — but you *are* expected to be able to describe what each part does and explain the choices you made.

Over three weeks you will:

- Set up a professional development environment (VS Code + Java + Git)
- Learn to use agentic AI to generate and iterate on real code
- Understand the Model-View-Controller (MVC) design pattern
- Build and extend your own Java arcade game
- Practice version control with Git and GitHub

---

## Tools You Will Need

- **VS Code** with the Extension Pack for Java installed
- **Git** and a free **GitHub** account
- An agentic AI tool — Claude (claude.ai) or GitHub Copilot are both fine
- Java 17 or later

---

## The PROMPTS.md Requirement

Every week, you will maintain a file called `PROMPTS.md` in your repository. This file is a **required part of your deliverable** — it is just as important as the code itself.

In `PROMPTS.md`, record:

- The key prompts you used (copy and paste them)
- A brief note on what each prompt produced
- What you had to fix or change, and why
- At least one prompt that didn't work the way you expected, and what you did about it

Think of this as a lab notebook for your AI conversations. It makes your process visible and helps you reflect on what you're actually learning.

For **Weeks 1 and 2**, you must go beyond the seed prompts and include **at least 5 additional prompts of your own choosing each week**.

For **Week 3**, you must include **at least 10 total prompts**.

---

## Week 1 — Snake

**Learning goals:** Set up your dev environment, create a GitHub repo, and use agentic AI to build a working single-file Java game.

### What you'll build

A playable Snake game in Java using Swing. The snake moves on a grid, grows when it eats food, and ends when it hits a wall or itself.

### Seed prompts

Use these prompts in order to get started. Copy them into your AI tool, read what comes back, and ask follow-up questions if something doesn't make sense.

**Prompt 1 — Project setup**
> I'm starting a Java game project in VS Code using Swing. Create a single file called `SnakeGame.java` with a `main` method that opens a `JFrame` window (600x600 pixels) titled "Snake". Add a `JPanel` subclass called `GamePanel` inside the frame. No game logic yet — just get the window to open.

**Prompt 2 — Grid and snake**
> Add a 20x20 grid to `GamePanel`. Represent the snake as a sequence of grid cells and start it with 3 segments near the center, facing right. Each cell should be drawn as a 30x30 pixel square. Draw the snake in green and the background in dark gray.

**Prompt 3 — Game loop, movement, and keyboard input**
> Make the snake move automatically using a Swing timer that ticks every 150 milliseconds — the snake should advance one cell per tick in its current direction. Add arrow key controls so the player can steer, but don't allow the snake to reverse direction. For now, have the snake wrap around the edges instead of dying. Make sure the panel can receive keyboard input.

**Prompt 4 — Food, collision, score, and restart**
> Add a food pellet that spawns at a random empty cell. When the snake eats it, grow by one segment and spawn new food. Add collision detection: hitting a wall or the snake's own body should end the game, stop movement, and show a "Game Over" message with the final score in the center of the screen. Display the current score in the top-left corner during play. When the game is over, let the player press R to reset everything and play again.

### After the seed prompts

Once your game is running, **take it somewhere of your own.** Here are some ideas — pick one or invent your own:

You are required to use **at least 5 additional prompts of your own choosing** after the seed prompts this week.

- Add a high score that persists across games
- Add speed levels that increase as the score grows
- Add a start screen or restart button
- Change the visual style (rounded segments, color gradients, pixel art)
- Add sound using `javax.sound.sampled`
- Give the game a theme (the snake is a spaceship, the food is planets, etc.)

### Deliverable

Submit a link to your GitHub repository. It must contain:

- `SnakeGame.java` — your working game
- `PROMPTS.md` — your prompt log with reflection
- A `README.md` with one paragraph describing what you built and what you changed from the seed prompt version

The game must run. If it doesn't run, it doesn't meet the baseline.

### Ambition lanes

All lanes meet the week's requirements and earn full credit — choose the one that fits where you are.

| Lane | What it looks like |
|---|---|
| **Foundation** | Seed prompts produce a working game; you make at least one meaningful personal change; `PROMPTS.md` documents your process |
| **Core** | Working game plus one self-directed feature added through your own AI dialogue; commits show iteration over time |
| **Stretch** | Multiple self-directed features; `PROMPTS.md` shows a deliberate prompting strategy and reflects on what the AI got wrong |

---

## Week 2 — Space Invaders

**Learning goals:** Understand and apply the Model-View-Controller (MVC) design pattern by building a more complex game across three separate classes.

### What you'll build

A playable Space Invaders game in Java. A grid of aliens moves and descends, the player shoots, and the game ends when aliens reach the bottom or the player runs out of lives.

### What is MVC?

MVC is a way of organizing code so that three concerns stay separate:

- **Model** — the game's data and rules. Positions of aliens, player health, score, collision logic. No Swing imports.
- **View** — everything the player sees. Reads from the Model and draws it using Swing. No game logic.
- **Controller** — input handling. Listens for key presses and tells the Model what to do. Connects Model and View.

This week, your game must be split into exactly these three classes: `GameModel.java`, `GameView.java`, and `GameController.java`.

### Seed prompts

**Prompt 1 — MVC skeleton**
> I'm building Space Invaders in Java using Swing. Create three files: `GameModel.java`, `GameView.java`, and `GameController.java`. `GameView` should extend `JPanel` and be hosted in a `JFrame`. `GameController` should set up the frame and wire the three classes together. `GameModel` should have no Swing imports. For now, just create the class shells with a few placeholder comments explaining what each class will do. Include a `main` method in `GameController`.

**Prompt 2 — Build the Model**
> Fill in `GameModel.java`. The model should store: the player's position (x only — they move horizontally), a grid of aliens (5 rows of 11), the player's bullet (one at a time), alien bullets, the player's score, and the number of lives (start with 3). Add methods to move the player left/right, fire a bullet, move the player's bullet upward each tick, move aliens (right until edge, then down and reverse), and check for collisions between bullets and aliens or aliens and the player. Aliens and the player should be represented as simple rectangles for now.

**Prompt 3 — Build the View**
> Fill in `GameView.java`. It should override `paintComponent` and draw everything from `GameModel`: the player as a white rectangle, aliens as green rectangles in their grid positions, bullets as small yellow rectangles, the score in the top-left, and lives in the top-right. The view should take a `GameModel` reference in its constructor and read all data from it — no game logic in this class.

**Prompt 4 — Wire the Controller**
> Fill in `GameController.java`. Add a `KeyListener` that moves the player left/right on arrow keys and fires on spacebar by calling the appropriate model methods. Add a `javax.swing.Timer` that ticks every 16ms — on each tick, call the model's update methods (move bullets, move aliens, check collisions) and repaint the view. Add game-over handling: stop the timer and display a message when the player loses all lives or aliens reach the bottom.

### After the seed prompts

Extend the game using your own AI prompts. Some directions:

You are required to use **at least 5 additional prompts of your own choosing** after the seed prompts this week.

- Add shields/bunkers the player can hide behind (and aliens can destroy)
- Add alien shooting that fires downward at intervals
- Add a UFO that crosses the top of the screen for bonus points
- Add increasing alien speed as more are destroyed
- Add a high score screen
- Improve the visual style — sprites, colors, animations

### Deliverable

Submit a link to your GitHub repository. It must contain:

- `GameModel.java`, `GameView.java`, `GameController.java` — your working game
- `PROMPTS.md` — your prompt log with reflection
- A `README.md` describing your game, your extensions, and which class handles which responsibility

Be prepared to answer: *"Point to a line of code in the Model. Why does it belong there and not in the View?"*

### Ambition lanes

| Lane | What it looks like |
|---|---|
| **Foundation** | Three-class MVC structure; game runs; you can identify what each class does; `PROMPTS.md` documents your process |
| **Core** | Working game plus one self-directed extension; you can explain the MVC split in your own words |
| **Stretch** | Multiple extensions; `PROMPTS.md` shows how you diagnosed and fixed an AI mistake; clean separation of concerns throughout |

---

## Week 3 — Your Game

**Learning goals:** Drive the full development process yourself, from spec to working game, using agentic AI as your primary tool.

### What you'll build

A game of your choosing, built in Java with Swing, using MVC. You are not given seed prompts this week — you write them yourself.

If you want a suggested direction, a Pac-Man outline is provided as an optional starting point (see the course page). You are not required to use it.

Other options: Breakout, Tetris, Frogger, a puzzle game, a game of your own invention. If you're unsure whether your idea is in scope, ask.

### How to start

Before writing any code, write a **one-page spec** (put it in your `README.md`). Your spec should describe:

- What the game is and how it's played
- What the Model will store
- What the View will draw
- What the Controller will handle
- What "done" looks like for this week

Then open your AI tool and start a conversation. A good first prompt is:

> "I'm building [game name] in Java using Swing and MVC. Here's my spec: [paste your spec]. Create the three class shells — `GameModel.java`, `GameView.java`, `GameController.java` — with method stubs based on this design."

From there, the conversation is yours.

### Deliverable

Submit a link to your GitHub repository. It must contain:

- Your game files — **at least 3 separate Java files**, with at minimum three MVC classes
- `PROMPTS.md` — a complete log of your AI conversation including your spec-writing prompts, your build prompts, and at least one example of the AI producing something wrong and how you corrected it. It must include **at least 10 total prompts**.
- `README.md` with your spec and a description of what you built
- A **5-minute video demo** of your final project, submitted as either a YouTube link or a MediaSpace link (available through your RCTC account)

Your game must run and be playable in some form. It does not need to be complete or polished.

### Ambition lanes

| Lane | What it looks like |
|---|---|
| **Foundation** | Extend your Week 2 Space Invaders with new features using your own prompts; MVC structure intact |
| **Core** | New game from scratch using MVC; core gameplay loop works; `PROMPTS.md` shows a real AI dialogue |
| **Stretch** | Polished game with multiple features; `PROMPTS.md` shows deliberate prompt strategy, iteration, and reflection on AI limitations |

---

## Grading

Each week is graded on three things:

| Component | Weight | What we're looking for |
|---|---|---|
| Working code | 50% | The game runs and does what you described |
| PROMPTS.md | 30% | Honest, detailed record of your AI process including what went wrong |
| README / reflection | 20% | Clear description of what you built and what you learned |

Grades do not depend on which lane you chose. A Foundation submission that runs cleanly and has a thoughtful `PROMPTS.md` earns full marks. A Stretch submission with broken code and a thin prompt log does not.

---

## Academic Integrity

Using AI to generate code is **expected and encouraged** in this project — that is the point. What matters is that you understand what you submitted well enough to explain it, that your `PROMPTS.md` honestly documents how the code was created, and that you are the one directing the process.

Submitting code you cannot explain, or a `PROMPTS.md` that does not reflect your actual process, violates the spirit of the assignment.

---

## Getting Help

- Bring your `PROMPTS.md` to office hours — it makes troubleshooting much faster
- If the AI gives you something that doesn't compile, paste the error back into the conversation and ask it to fix it — that is a normal part of the process
- The AI will sometimes be confidently wrong. When something seems off, trust your instincts and ask it to explain its reasoning

Good luck. Build something you're proud of.
