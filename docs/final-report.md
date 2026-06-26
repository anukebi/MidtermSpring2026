# Final Report

## What this is

A command-line UNO game that grew out of the midterm refactor. Game logic lives separately from CLI input/output, so
most rules are covered by unit tests without typing into the console. Postgres stores game history and stats when the
app runs with a database.

## Rules implemented

- Full 108-card deck and legal play checks
- Skip, Reverse, Draw Two, Wild, Wild Draw Four
- Draw one card and optionally play it if legal
- UNO call + two-card penalty if you miss it
- Round scoring and multi-round play to a target score (default 500)
- Stack matching +2 / W4 (see `docs/extension-readiness.md`)

Details and card codes: `docs/rules-supported.md`.

## How it's organized (briefly)

- **Game engine** - turns, playing cards, effects, scoring
- **CLI layer** - prompts, reading input, printing what happened
- **Rules / cards** - parsing card codes, checking if a play is legal
- **Bots** - automatic choices for bot players

Rules don't depend on `System.in`, so tests can drive the engine directly.

## How to play

Bot game:

```bash
scripts/run.sh --bots=3 --games=5 --quiet
```

Human game (start Postgres first with `docker compose up -d db`):

```bash
scripts/run.sh --human --bots=2 --games=1
```

On your turn: card index, card code (`R5`, `G+2`, …), or `draw`. Wilds ask for `R` / `Y` / `G` / `B`. One card left →
type `UNO` within the timeout.

All CLI flags are listed in the README. Maven run examples are there too.

## Tests

**Unit / component** - deck size, Skip, Reverse, Draw Two, Wild, W4, scoring, UNO penalty, legality, card parsing, bot
choices, etc.

**Integration** - `GameEngineFlowTest` runs scripted scenarios (round win, Draw Two, missed UNO penalty, match stops at
target score). `GameEngineIntegrationTest` runs a full bot match through Spring + Postgres.

Run everything with `./mvnw test` or `scripts/test.sh`.

## Notes

- No Wild Draw Four challenge rule (optional in standard UNO; not part of this build)
- Two-player Reverse is treated like Skip
- Wild on the opening discard is reshuffled; other opening action cards are kept

## Midterm work

The original monolith refactor and write-up: `docs/refactoring-report.md`.
