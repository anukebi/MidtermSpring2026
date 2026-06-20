# Extension Readiness (Submission)

> Working draft: [`extension-readiness.md`](extension-readiness.md)

## Design Strengths After Refactoring

| Layer       | Classes                         | Responsibility                        |
|-------------|---------------------------------|---------------------------------------|
| `uno.rule`  | `RulesValidator`, `BotLogic`    | Legality, stacking check, bot choices |
| `uno.game`  | `GameEngine`, `GameState`       | Turn flow and effects                 |
| `uno.io`    | `CliInput`, `CliOutput`         | Prompts and output                    |
| `uno.model` | `CardMapper`, card/player types | Card codes and domain model           |

New rules or effects can usually be added in `uno.rule` and `uno.game`; the CLI changes only when prompts or messages
change.

---

## Implemented Extension: Stack +2 and +4 (Latest Commit)

This extends the base game. It implements a house rule listed under “not implemented” in `docs/rules.html`.

**Rule.** When a draw card is played (`G+2`, `R+2`, or wild `W4`):

1. The draw amount (2 or 4) is added to `pendingDraw`.
2. Turn passes to the next player.
3. That player must either play a **matching** draw card (`+2` on pending 2, `W4` on pending 4) and pass the stack, or
   type `draw`, take all pending cards, and end the turn.

Mixed stacks are not allowed (+2 cannot stack on a pending 4).

**Implementation.**

| Component                | Change                                                           |
|--------------------------|------------------------------------------------------------------|
| `GameState`              | `pendingDraw`, `pendingDrawAmount`, start/add/clear helpers      |
| `RulesValidator`         | `canStack(card, pendingDrawAmount)`                              |
| `GameEngine`             | `DRAW` adds to stack; `resolvePendingDraw` on draw under pending |
| `BotLogic`               | Stack or return `-1` when `pendingDrawAmount > 0`                |
| `CliInput` / `CliOutput` | “Stack +N or draw”; `printPendingDraw`                           |

**Tests.** `CharacterizationTest.testDrawStacking` (stack legality, accumulation, bot choice).

---

## Course Baseline vs Planned Card Codes

The **base** build uses course codes (`W`, `W4` = draw four, `G+2`). The table below is **not implemented**; it
describes a possible next step.

| Code                   | Planned meaning                                                |
|------------------------|----------------------------------------------------------------|
| `W`                    | Wild — call a color                                            |
| `RC`, `YC`, `GC`, `BC` | Colored change — call a new color (`C` suffix)                 |
| `WC`                   | Wild change — same effect, explicit code                       |
| `W+4`                  | Wild draw four (like `G+2`)                                    |
| `W4`, `W7`, …          | Numbered wild — digit on card + call color (**not** draw four) |

---

## Planned Extension A: Colored Change Cards (`RC`, `WC`, …)

Extend `CardRank.CHANGE` with suffix `C` (`RC`, `BC`, …). Reuse `GameEngine.triggerColorUpdate` and `CliInput.askColor`.
Add deck entries and characterization tests (e.g. play `RC`, call blue, `B3` legal on called color). Keep `W` on empty
change suffix if desired.

---

## Planned Extension B: `W+4` and Numbered `W4` (One Migration)

Must be done as **one** change so `W4` never means two things at once.

| Course baseline  | Planned                                                 |
|------------------|---------------------------------------------------------|
| `W4` = draw four | `W+4` = draw four                                       |
| —                | `W4` = numbered wild (call color; number fixed on card) |

Requires: `CardRank.DRAW` with `\+4`, deck swap, remove wild `NUMBER` → `DRAW` shortcut, `calledNumber` on `GameState`,
updated `RulesValidator` and tests (`W+4` draws four; `W4` does not; `R4` unchanged).

---

## Other Possible Extensions

- Smarter bots (`BotLogic`)
- Replay hooks on `GameEngine`
- Replace CLI only (`CliInput` / `CliOutput`)

---

## Remaining Constraints

- `applyCardEffect` will grow with more card types unless effect helpers are extracted
- `calledNumber` not present (needed for numbered wild)

**Suggested order:** colored change cards → `W+4` / numbered `W4` migration → further numbered wilds if needed. Stacking
is already in place for current `W4` draw-four cards.
