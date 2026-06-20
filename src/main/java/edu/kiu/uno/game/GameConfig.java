package edu.kiu.uno.game;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GameConfig {

  private static final Function<GameConfig, List<BiFunction<String[], Integer, Integer>>> CONFIG_SETTERS = g -> List.of(
      g::setBots, g::setGames, g::setSeed, g::setHuman, g::setQuiet, g::setHelp
  );

  private int bots = 3;
  private int games = 1;
  private long seed = System.currentTimeMillis();
  private boolean human = false;
  private boolean quiet = false;
  private boolean help = false;

  public GameConfig(String[] args) {
    var setters = CONFIG_SETTERS.apply(this);
    for (int i = 0; i < args.length; i++) {
      for (var setter : setters) {
        var newIndex = setter.apply(args, i);
        if (newIndex != i) {
          break;
        }
      }
    }
  }

  private int setBots(String[] args, int i) {
    bots = args[i].equals("--bots") && hasNextArg(args, i)
        ? Integer.parseInt(args[++i])
        : bots;
    return i;
  }

  private int setGames(String[] args, int i) {
    games = args[i].equals("--games") && hasNextArg(args, i)
        ? Integer.parseInt(args[++i])
        : games;
    return i;
  }

  private int setSeed(String[] args, int i) {
    seed = args[i].equals("--seed") && hasNextArg(args, i)
        ? Long.parseLong(args[++i])
        : seed;
    return i;
  }

  private int setHuman(String[] args, int i) {
    human |= args[i].equals("--human");
    return i;
  }

  private int setQuiet(String[] args, int i) {
    quiet |= args[i].equals("--quiet");
    return i;
  }

  private int setHelp(String[] args, int i) {
    help |= args[i].equals("--help");
    return i;
  }

  private boolean hasNextArg(String[] args, int i) {
    return i < args.length;
  }

}
