# Snake Game — AI Prompts

## Prompt 1 — Project setup

> I'm starting a Java project in VS Code. Create an appropriate "source/snake" directory with a single file called `SnakeGame.java`. It should have a `main` method that opens a `JFrame` window (600x600 pixels) with the title "Snake". Inside the frame, add a `JPanel` subclass called `GamePanel`. Don't add any game logic yet — just get the window to open. Then git commit with an appropriate message. Finally copy this prompt to a prompts.md document (in the snake directory).

## Prompt 2 — Grid and snake

> Now add a 20x20 grid to `GamePanel`. Represent the snake as a `LinkedList<Point>` where each `Point` is a grid cell. Start the snake with 3 segments near the center, moving right. Draw the grid cells as 30x30 pixel squares. Draw the snake in green and leave everything else dark gray. Same things with prompts and git commit.

## Prompt 3 — Game loop and movement

> Add a `javax.swing.Timer` that fires every 150 milliseconds. On each tick, move the snake one cell in its current direction by adding a new head and removing the tail. Repaint the panel on each tick. The snake should wrap around the edges for now instead of dying. Same as before with prompts.md and git commit.
