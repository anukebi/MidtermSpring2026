package edu.kiu.uno.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Component
@ConfigurationProperties
public class GameProperties {

  private int bots = 3;
  private int games = 1;
  private long seed = System.currentTimeMillis();
  private boolean human = false;
  private boolean quiet = false;
  private boolean help = false;
  private String query = null;
  private boolean cliOnly = true;
  private int target = 500;
  private long unoTimeout = 2000L;
  private long turnSafetyLimit = 3000L;

}
