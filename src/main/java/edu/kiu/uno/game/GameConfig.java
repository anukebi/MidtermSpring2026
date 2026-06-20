package edu.kiu.uno.game;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class GameConfig {

  private static final Function<GameConfig, List<BiFunction<String[], Integer, Integer>>> CONFIG_SETTERS = g -> List.of(
      g::setBots, g::setGames, g::setSeed, g::setHuman, g::setQuiet, g::setSelfTest, g::setHelp
  );

  private int bots;
  private int games;
  private long seed;
  private boolean human;
  private boolean quiet;
  private boolean selfTest;
  private boolean help;

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
        : 3;
    return i;
  }

  private int setGames(String[] args, int i) {
    games = args[i].equals("--games") && hasNextArg(args, i)
        ? Integer.parseInt(args[++i])
        : 1;
    return i;
  }

  private int setSeed(String[] args, int i) {
    seed = args[i].equals("--seed") && hasNextArg(args, i)
        ? Long.parseLong(args[++i])
        : System.currentTimeMillis();
    return i;
  }

  private int setHuman(String[] args, int i) {
    human = args[i].equals("--human");
    return i;
  }

  private int setQuiet(String[] args, int i) {
    quiet = args[i].equals("--quiet");
    return i;
  }

  private int setSelfTest(String[] args, int i) {
    selfTest = args[i].equals("--self-test");
    return i;
  }

  private int setHelp(String[] args, int i) {
    help = args[i].equals("--help");
    return i;
  }

  private boolean hasNextArg(String[] args, int i) {
    return i < args.length;
  }

  public int getBots() {
    return bots;
  }

  public int getGames() {
    return games;
  }

  public long getSeed() {
    return seed;
  }

  public boolean isHuman() {
    return human;
  }

  public boolean isQuiet() {
    return quiet;
  }

  public boolean isSelfTest() {
    return selfTest;
  }

  public boolean isHelp() {
    return help;
  }

}
