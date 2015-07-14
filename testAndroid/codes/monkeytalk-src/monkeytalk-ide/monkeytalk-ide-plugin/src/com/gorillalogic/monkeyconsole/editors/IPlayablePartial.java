package com.gorillalogic.monkeyconsole.editors;

import java.util.List;
import com.gorillalogic.monkeytalk.Command;
import com.gorillalogic.monkeytalk.processor.PlaybackListener;

public interface IPlayablePartial extends IPlayable {
	List<Command> getCommands();
	PlaybackListener getPlaybackListener(int from, int to);
}
