# Snake Game — AI Prompts

## Prompt 1 — Project setup

> I'm starting a Java project in VS Code. Create an appropriate "source/snake" directory with a single file called `SnakeGame.java`. It should have a `main` method that opens a `JFrame` window (600x600 pixels) with the title "Snake". Inside the frame, add a `JPanel` subclass called `GamePanel`. Don't add any game logic yet — just get the window to open.

## Prompt 2 — Grid and snake

> Now add a 20x20 grid to `GamePanel`. Represent the snake as a `LinkedList<Point>` where each `Point` is a grid cell. Start the snake with 3 segments near the center, moving right. Draw the grid cells as 30x30 pixel squares. Draw the snake in green and leave everything else dark gray.

## Prompt 3 — Game loop, movement, and keyboard input

> Add a `javax.swing.Timer` that fires every 150 milliseconds. On each tick, move the snake one cell in its current direction by adding a new head and removing the tail. Repaint the panel on each tick. The snake should wrap around the edges for now instead of dying. Also add a `KeyListener` so the player can steer with the arrow keys — pressing an arrow key should change the snake's direction, but don't allow reversing directly (e.g. can't go left while moving right). Make sure `GamePanel` is focusable so it receives key events.

## Prompt 4 — Food, collision, score, and restart

> Add a food pellet that appears at a random empty grid cell. When the snake's head reaches the food, grow the snake by one segment and spawn new food. Add wall and self-collision — when the snake hits the edge or its own body, stop the timer and display "Game Over" along with the final score in the center of the panel. Show the current score (number of food eaten) in the top-left corner. When the game is over, pressing the R key should reset the snake, score, and food back to their starting state and restart the timer.
