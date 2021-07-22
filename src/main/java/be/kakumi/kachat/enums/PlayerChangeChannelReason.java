package be.kakumi.kachat.enums;

public enum PlayerChangeChannelReason {
    AUTO_WORLD, //Use channel in this world
    WORLD_RESTRICTED, //Can't use channel in this world
    COMMAND, //Player execute command
    COMMAND_OTHERS, //Player change the player channel
    UPDATED, //Channel updated
    DELETED, //Channel no longer exist
    UNKNOWN //Not specified
}
