package main.lobby;

import java.io.IOException;

public abstract class LobbySettingsFactory {
	public abstract LobbySettings parseFromInput(byte[] arr) throws IOException;
	public abstract byte[] toByteArr(LobbySettings settings) throws IOException;
}
