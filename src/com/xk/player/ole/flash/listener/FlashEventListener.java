package com.xk.player.ole.flash.listener;

public interface FlashEventListener {
	/**
	 * Generated when target changes ReadyState.
	 * 
	 * @param newState
	 *            the new state of the control, one of:
	 *            READYSTATE_UNINITIALIZED; READYSTATE_LOADING;
	 *            READYSTATE_LOADED; READYSTATE_INTERACsTIVE;
	 *            READYSTATE_COMPLETE.
	 */
	void onReadyStateChange(int newState);

	/**
	 * <p>
	 * Generated as the Flash movie is downloading.
	 * </p>
	 */
	void onProgress(int percentDone);

	/**
	 * <p>
	 * Generated when an FSCommand action is performed in the movie with a URL
	 * and the URL starts with "FSCommand :". Use this to create a response to a
	 * frame or button action in the Flash movie.
	 * </p>
	 * 
	 * @param command
	 *            "Quit", "Fullscreen", "AllowScale", "Showmenu", "Exec"
	 */
	void onFSCommand(String command, String args);
}