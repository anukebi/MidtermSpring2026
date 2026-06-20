# Refactoring Report (Submission)

> Working draft: [`refactoring-report.md`](refactoring-report.md)

## Submission Structure

This repository contains two layers of work:

1. **Base refactor** — modular design, characterization tests, course rules (`W4` = wild draw four, no draw stacking).
2. **Extension (latest commit)** — stackable `+2` and `+4`. Documented in [
   `extension-readiness.md`](extension-readiness.md).

## Characterized Behavior

| Check                  | Coverage                        |
|------------------------|---------------------------------|
| `Main --self-test`     | 10 quick regression checks      |
| `CharacterizationTest` | 43 checks via `scripts/test.sh` |

Tests describe **this** implementation, including quirks from `docs/rules.html`: optional `draw`, illegal index →
penalty draw, illegal card code → re-prompt, bots auto-play drawn cards, visible hands, 3000-turn safety limit, final
scores printed in quiet mode.

## Problems in the Original Code

- Monolithic `Main` with global mutable state
- Duplicated legality logic in the turn loop, bots, and validators
- Console I/O mixed with rules and effects
- Primitive `String` card codes

## Refactorings Performed

| Package            | Classes                                                          | Role                       |
|--------------------|------------------------------------------------------------------|----------------------------|
| `uno.model.card`   | `Card`, `CardColor`, `CardRank`                                  | Card domain                |
| `uno.model.player` | `Player`, `PlayerType`                                           | Players                    |
| `uno.model`        | `CardMapper`                                                     | Parse and build card codes |
| `uno.game`         | `GameEngine`, `GameState`, `Deck`, `GameDirection`, `GameConfig` | Game loop and state        |
| `uno.rule`         | `RulesValidator`, `BotLogic`                                     | Legality and bot choices   |
| `uno.io`           | `CliInput`, `CliOutput`                                          | Console layer              |

`Main` parses CLI flags (via `GameConfig`) and wires components. Rules and effects are testable without simulating full
interactive play for most behaviors.

## Card Parsing

`CardRank.fromCode(rankSuffix, color)` matches enum patterns in declaration order. For the course baseline, a wild
suffix `4` is treated as draw four (`NUMBER` → `DRAW` when color is `WILD`); `R4` stays a number card. The `DRAW`
pattern is `(\+2|4)`; colored cards use `+2` in play.

## Behavior Preserved (Base)

- Card codes and matching from the starter project
- Skip, reverse (including two-player), draw two, wild draw four
- Wild color call after `W` / `W4`
- Scoring per `docs/rules.html`
- Cumulative scores across `--games`

## Extension Commit (Not in Base Rules)

Draw stacking changes `DRAW` handling: penalties accumulate in `GameState.pendingDraw` until the next player stacks a
matching draw card or takes the pile. See the extension-readiness document.
