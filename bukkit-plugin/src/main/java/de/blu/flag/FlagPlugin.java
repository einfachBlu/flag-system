package de.blu.flag;

import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Singleton;

@Singleton
public final class FlagPlugin extends JavaPlugin {

  @SneakyThrows
  @Override
  public void onEnable() {
    FlagAPI.init();

    /*
    // Testing
    final UUID playerId = UUID.fromString("ae3e1de7-08a0-4ad5-b364-2c2fcddb7889");
    String url = "http://179.61.251.222:8081";
    final ProfileWebRequester profileWebRequester = new ProfileWebRequester();
    Profile profile = profileWebRequester.getProfileByName(url, "Tomato");
    if (profile == null) {
      profile = profileWebRequester.createProfile(url, playerId, "Tomato");
    }

    // Server Flag
    System.out.println("Server Flag");
    System.out.println(FlagAPI.getInstance().isSet(FlagType.SERVER, "flag.test"));
    FlagAPI.getInstance().set(FlagType.SERVER, "flag.test");
    System.out.println(FlagAPI.getInstance().isSet(FlagType.SERVER, "flag.test"));
    FlagAPI.getInstance().unset(FlagType.SERVER, "flag.test");
    System.out.println(FlagAPI.getInstance().isSet(FlagType.SERVER, "flag.test"));

    // Player Flag
    System.out.println("Player Flag");
    System.out.println(FlagAPI.getInstance().isSet(FlagType.PLAYER, "flag.test", playerId));
    FlagAPI.getInstance().set(FlagType.PLAYER, "flag.test", playerId);
    System.out.println(FlagAPI.getInstance().isSet(FlagType.PLAYER, "flag.test", playerId));
    FlagAPI.getInstance().unset(FlagType.PLAYER, "flag.test", playerId);
    System.out.println(FlagAPI.getInstance().isSet(FlagType.PLAYER, "flag.test", playerId));

    // Profile Flag
    System.out.println("Profile Flag");
    System.out.println(FlagAPI.getInstance().isSet(FlagType.PROFILE, "flag.test", profile.getId()));
    FlagAPI.getInstance().set(FlagType.PROFILE, "flag.test", profile.getId());
    System.out.println(FlagAPI.getInstance().isSet(FlagType.PROFILE, "flag.test", profile.getId()));
    FlagAPI.getInstance().unset(FlagType.PROFILE, "flag.test", profile.getId());
    System.out.println(FlagAPI.getInstance().isSet(FlagType.PROFILE, "flag.test", profile.getId()));
    */
  }
}
