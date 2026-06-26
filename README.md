# Final - UNO CLI

This is a standalone CLI UNO-like game. It started as one big procedural `Main` class and was refactored into smaller
pieces with tests. The game now runs as a Spring Boot app with fuller UNO rules, bot play, and PostgreSQL for saving
game stats.

## Compile

```bash
scripts/compile.sh
```

or

```bash
./mvnw clean compile
```

## Run Bot Games

```bash
scripts/run.sh --bots=3 --games=5 --quiet
```

or

```bash
./mvnw -q clean compile exec:java -Dexec.args="--bots=3 --games=5 --quiet"
```

For human play you need Postgres running first:

```bash
docker compose up -d db
```

## Run Interactive Game

```bash
scripts/run.sh --human --bots=2 --games=1
```

or

```bash
./mvnw -q clean compile exec:java -Dexec.args="--human --bots=2 --games=1"
```

Card input examples:

```text
R5   red 5
YS   yellow skip
BR   blue reverse
G+2  green draw two
W    wild
W4   wild draw four
draw draw a card
uno  call UNO when you have one card left (timed prompt)
```

### Extension: stack draw cards

When a +2 or W4 is played, the next player may play a matching draw card to pass the stack, or type `draw` to take all
pending cards. See `docs/extension-readiness.md`.

## CLI arguments

All flags use `--name=value` (booleans can be `--flag` or `--flag=true`).

| Argument                | Default      | Description                                                                                                                 |
|-------------------------|--------------|-----------------------------------------------------------------------------------------------------------------------------|
| `--bots=N`              | `3`          | Number of bot players (2-4 players total with `--human`)                                                                    |
| `--games=N`             | `1`          | How many rounds to play in one run                                                                                          |
| `--seed=N`              | current time | Random seed for shuffling                                                                                                   |
| `--human`               | off          | Add a human player (`You`)                                                                                                  |
| `--quiet`               | off          | Less console output                                                                                                         |
| `--help`                | off          | Print usage and exit                                                                                                        |
| `--target=N`            | `500`        | Score needed to win the match                                                                                               |
| `--uno-timeout=N`       | `2000`       | Milliseconds to type `UNO` with one card left                                                                               |
| `--turn-safety-limit=N` | `3000`       | Max turns per round (safety cap for bot games)                                                                              |
| `--query=NAME`          | -            | Skip the game and run a stats query (needs Postgres). Values: `RECENT_GAMES`, `PLAYER_SCORES`, `PLAYER_WINS`, `MOST_ACTIVE` |
| `--cli-only=true\false` | `true`       | Exit the JVM when the CLI finishes (`false` keeps the Spring app running - useful for tests)                                |

Examples:

```bash
scripts/run.sh --bots=2 --games=3 --quiet --seed=42
scripts/run.sh --human --bots=1 --games=1 --target=300
scripts/run.sh --query=RECENT_GAMES --cli-only=false
```

## Implemented rules (short version)

- Standard 108-card deck
- Skip, Reverse, Draw Two, Wild, Wild Draw Four
- Draw one card; play it right away if it's legal (`y`/`n`), otherwise pass
- UNO call with a short timeout; miss it and you draw two penalty cards
- Round scoring; first player to **500** points wins the match (change with `--target=N`)
- Two-player Reverse counts like Skip
- Stack matching +2 / W4 instead of drawing (see extension above)

More detail: `docs/rules-supported.md`.

## Tests

```bash
scripts/test.sh
```

or

```bash
./mvnw clean test
```

Unit tests cover individual rules (deck, action cards, scoring, UNO penalty, etc.). Integration tests run scripted game
flows and a full Spring bot match. See `docs/final-report.md`.

## Packaging

```bash
scripts/package.sh
```

or

```bash
./mvnw clean package -DskipTests
```

## Docker

```bash
# Build the Docker image
docker build -t uno .

# Run the Docker container
docker run --rm uno --bots=2 --games=1
```

Database only (for local dev / human games):

```bash
docker compose up -d db
```

## Final project docs

Write-ups for the final scope:

* `docs/final-report.md` - what was built, how to play, tests, notes
* `docs/rules-supported.md` - rules this build supports, card codes, scoring
* `docs/extension-readiness.md` - stack +2 / +4 extension

Midterm refactor notes are still in `docs/refactoring-report.md`.

## Rules

See `docs/rules.html` for the original course rules page.

See `docs/rules-supported.md` for what this build implements.

## Midterm Materials

* `docs/midterm-exam.md`: midterm brief
* `docs/rubric.md`: grading rubric
* `docs/refactoring-guide.md`: suggested refactoring path
