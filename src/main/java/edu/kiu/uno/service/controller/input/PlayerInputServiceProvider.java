package edu.kiu.uno.service.controller.input;

import edu.kiu.uno.model.exception.MissingStrategyException;
import edu.kiu.uno.model.player.Player;
import edu.kiu.uno.util.StrategyProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PlayerInputServiceProvider implements StrategyProvider<Player, PlayerInputService> {

  private final List<PlayerInputService> playerInputServices;

  @Override
  public PlayerInputService get(Player player) {
    return playerInputServices.stream()
        .filter(service -> service.supportsPlayer(player.getType()))
        .findFirst()
        .orElseThrow(() -> new MissingStrategyException("No input service found for player type: " + player.getType()));
  }

}
