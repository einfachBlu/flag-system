package de.blu.flag;

import de.blu.database.DatabaseAPI;
import de.blu.database.data.TableColumn;
import de.blu.database.data.TableColumnType;
import de.blu.database.storage.cassandra.CassandraConnection;
import de.blu.flag.data.FlagType;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public final class FlagAPI {

  @Getter private static FlagAPI instance;

  private ExecutorService executorService = Executors.newCachedThreadPool();

  public FlagAPI() {
    FlagAPI.instance = this;
  }

  public static void init() {
    FlagAPI flagAPI = new FlagAPI();

    // Init Database Tables
    CassandraConnection cassandraConnection = flagAPI.getCassandraConnection();
    if (cassandraConnection == null) {
      return;
    }

    List<TableColumn> serverFlagsColumns =
        Arrays.asList(
            new TableColumn(TableColumnType.UUID, "id", true),
            new TableColumn(TableColumnType.STRING, "flag", false));
    List<TableColumn> playerFlagsColumns =
        Arrays.asList(
            new TableColumn(TableColumnType.UUID, "id", true),
            new TableColumn(TableColumnType.STRING, "flag", false),
            new TableColumn(TableColumnType.UUID, "player", false));
    List<TableColumn> profileFlagsColumns =
        Arrays.asList(
            new TableColumn(TableColumnType.UUID, "id", true),
            new TableColumn(TableColumnType.STRING, "flag", false),
            new TableColumn(TableColumnType.UUID, "profile", false));

    cassandraConnection.createTableIfNotExist("server_flags", serverFlagsColumns);
    cassandraConnection.createTableIfNotExist("player_flags", playerFlagsColumns);
    cassandraConnection.createTableIfNotExist("profile_flags", profileFlagsColumns);
  }

  public void set(FlagType flagType, String flag, UUID reference) {
    if (this.isSet(flagType, flag, reference)) {
      return;
    }

    CassandraConnection cassandraConnection = this.getCassandraConnection();
    if (cassandraConnection == null) {
      return;
    }

    switch (flagType) {
      case PLAYER:
        cassandraConnection.insertInto(
            "player_flags",
            new String[] {"id", "flag", "player"},
            new Object[] {UUID.randomUUID(), flag, reference});
        break;
      case PROFILE:
        cassandraConnection.insertInto(
            "profile_flags",
            new String[] {"id", "flag", "profile"},
            new Object[] {UUID.randomUUID(), flag, reference});
        break;
      case SERVER:
        cassandraConnection.insertInto(
            "server_flags", new String[] {"id", "flag"}, new Object[] {UUID.randomUUID(), flag});
        break;
    }
  }

  public void setAsync(FlagType flagType, String flag, UUID reference) {
    this.executorService.submit(() -> this.set(flagType, flag, reference));
  }

  public void unset(FlagType flagType, String flag, UUID reference) {
    if (!this.isSet(flagType, flag, reference)) {
      return;
    }

    CassandraConnection cassandraConnection = this.getCassandraConnection();
    if (cassandraConnection == null) {
      return;
    }

    UUID id = null;
    Map<Integer, Map<String, Object>> rows;
    switch (flagType) {
      case PLAYER:
        rows = cassandraConnection.selectAll("player_flags", "player", reference);
        for (Map<String, Object> row : rows.values()) {
          if (((String) row.get("flag")).equalsIgnoreCase(flag)) {
            id = (UUID) row.get("id");
            break;
          }
        }

        if (id == null) {
          return;
        }

        cassandraConnection.deleteFrom("player_flags", "id", id);
        break;
      case PROFILE:
        rows = cassandraConnection.selectAll("profile_flags", "profile", reference);
        for (Map<String, Object> row : rows.values()) {
          if (((String) row.get("flag")).equalsIgnoreCase(flag)) {
            id = (UUID) row.get("id");
            break;
          }
        }

        if (id == null) {
          return;
        }

        cassandraConnection.deleteFrom("profile_flags", "id", id);
        break;
      case SERVER:
        rows = cassandraConnection.selectAll("server_flags", "flag", flag);
        if (rows.size() == 0) {
          return;
        }

        id = (UUID) rows.values().iterator().next().get("id");
        cassandraConnection.deleteFrom("server_flags", "id", id);
        break;
    }
  }

  public void unsetAsync(FlagType flagType, String flag, UUID reference) {
    this.executorService.submit(() -> this.unset(flagType, flag, reference));
  }

  public boolean isSet(FlagType flagType, String flag, UUID reference) {
    CassandraConnection cassandraConnection = this.getCassandraConnection();
    if (cassandraConnection == null) {
      return false;
    }

    Map<Integer, Map<String, Object>> rows;
    switch (flagType) {
      case PLAYER:
        rows = cassandraConnection.selectAll("player_flags", "player", reference);
        for (Map<String, Object> row : rows.values()) {
          if (((String) row.get("flag")).equalsIgnoreCase(flag)) {
            return true;
          }
        }

        break;
      case PROFILE:
        rows = cassandraConnection.selectAll("profile_flags", "profile", reference);
        for (Map<String, Object> row : rows.values()) {
          if (((String) row.get("flag")).equalsIgnoreCase(flag)) {
            return true;
          }
        }

        break;
      case SERVER:
        rows = cassandraConnection.selectAll("server_flags", "flag", flag);
        if (rows.size() > 0) {
          return true;
        }

        break;
    }

    return false;
  }

  public void isSetAsync(
      FlagType flagType, String flag, UUID reference, Consumer<Boolean> isSetCallback) {
    this.executorService.submit(() -> isSetCallback.accept(this.isSet(flagType, flag, reference)));
  }

  private CassandraConnection getCassandraConnection() {
    DatabaseAPI databaseAPI = DatabaseAPI.getInstance();
    if (databaseAPI == null) {
      return null;
    }

    if (!databaseAPI.getCassandraConfig().isEnabled()) {
      return null;
    }

    if (!databaseAPI.getCassandraConnection().isConnected()) {
      return null;
    }

    return databaseAPI.getCassandraConnection();
  }
}
