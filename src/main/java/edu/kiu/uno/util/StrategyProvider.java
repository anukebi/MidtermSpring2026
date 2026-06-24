package edu.kiu.uno.util;

public interface StrategyProvider<T, R> {

  public R get(T t);

}
