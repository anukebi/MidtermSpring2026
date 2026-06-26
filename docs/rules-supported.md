# Supported UNO Rules

What this game implements. For the original course rules page see `docs/rules.html`.

## Rules

| Rule              | Notes                                                                                                       |
|-------------------|-------------------------------------------------------------------------------------------------------------|
| 108-card deck     | Four colors, 0-9, Skip, Reverse, Draw Two, Wild, Wild Draw Four                                             |
| Legal plays       | Match color, number, or action type; wilds always OK                                                        |
| Skip              | Next player loses their turn                                                                                |
| Reverse           | Direction flips; with 2 players it acts like Skip                                                           |
| Draw Two          | Next player draws 2 and skips, or stacks a matching +2                                                      |
| Wild              | Pick the next color                                                                                         |
| Wild Draw Four    | Pick color; next player draws 4 and skips, or stacks a matching W4                                          |
| Draw / pass       | Type `draw`; if the new card is legal you can play it (`y`/`n`). Bots play a legal drawn card automatically |
| UNO + penalty     | Human gets a short window to type `UNO` with one card left; miss it → draw 2                                |
| Scoring           | Winner gets points for cards left in other players' hands                                                   |
| Multi-round match | Play until someone reaches the target score (default **500**, set with `--target=N`)                        |
| Stack +2 / W4     | Matching draw card passes the stack; otherwise take all pending cards                                       |

Opening discard: if the first flipped card is Wild, it is reshuffled and replaced. Other action cards can stay on the
pile.

## Card codes

| Code   | Meaning        |
|--------|----------------|
| `R5`   | Red 5          |
| `YS`   | Yellow Skip    |
| `BR`   | Blue Reverse   |
| `G+2`  | Green Draw Two |
| `W`    | Wild           |
| `W4`   | Wild Draw Four |
| `draw` | Draw one card  |

## Scoring

| Card                    | Points     |
|-------------------------|------------|
| Number cards            | Face value |
| Skip, Reverse, Draw Two | 20         |
| Wild, Wild Draw Four    | 50         |

## Tests

Run `./mvnw test` or `scripts/test.sh`.

Unit tests cover the deck, action cards, scoring, UNO penalty, legality, bots, and more. Integration tests run scripted
turns and a full Spring bot game with Postgres (Testcontainers).
