package edu.kiu.uno.util;

import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ScannerInputProvider implements AutoCloseable {

  private final Scanner scanner;
  private final ExecutorService timedReader;
  private final AtomicReference<String> storedInput;
  private final AtomicBoolean acceptTimedInput;
  private volatile Future<String> activeTimedRead;

  public ScannerInputProvider(InputStream inputStream) {
    this.scanner = new Scanner(inputStream);
    storedInput = new AtomicReference<>();
    acceptTimedInput = new AtomicBoolean(false);
    timedReader = Executors.newSingleThreadExecutor(r -> {
      var t = new Thread(r, "timed-input");
      t.setDaemon(true);
      return t;
    });
  }

  public String getInput() {
    finishActiveTimedRead();
    String stored = storedInput.getAndSet(null);
    if (stored != null) {
      return stored;
    }
    return scanner.nextLine().trim();
  }

  public String getInput(long timeout, TimeUnit unit) {
    finishActiveTimedRead();
    String stored = storedInput.getAndSet(null);
    if (stored != null) {
      return stored;
    }

    acceptTimedInput.set(true);
    Future<String> future = timedReader.submit(this::readTimedLine);
    activeTimedRead = future;
    try {
      return future.get(timeout, unit);
    } catch (TimeoutException e) {
      return null;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    } catch (ExecutionException e) {
      throw new RuntimeException(e.getCause());
    } finally {
      acceptTimedInput.set(false);
    }
  }

  private String readTimedLine() {
    String line = scanner.nextLine().trim();
    if (acceptTimedInput.get()) {
      return line;
    }
    storedInput.set(line);
    return null;
  }

  private void finishActiveTimedRead() {
    Future<String> active = activeTimedRead;
    if (active == null || active.isDone()) {
      return;
    }
    try {
      active.get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    } catch (ExecutionException e) {
      throw new RuntimeException(e.getCause());
    }
  }

  @Override
  public void close() {
    timedReader.shutdownNow();
  }

}
