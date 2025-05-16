package pl.kwadratowamasakra.lightspigot.connection;

/**
 * The ConnectionState enum represents the possible states of a connection to the server.
 * It includes the following states: HANDSHAKING, STATUS, LOGIN, and PLAY.
 */
public enum ConnectionState {

    HANDSHAKING,
    STATUS,
    LOGIN,
    PLAY,
    CONFIGURATION

}
