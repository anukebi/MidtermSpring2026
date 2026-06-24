package edu.kiu.uno.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Component
@ConfigurationProperties(prefix = "game.deck")
public class DeckProperties {

  private int zero;
  private int number;
  private int skip;
  private int reverse;
  private int drawTwo;
  private int wild;
  private int wildDrawFour;
  private int playerCards;

}