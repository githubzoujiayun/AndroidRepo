package com.gorillalogic.monkeyconsole.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

public class MonkeyHandlerBase extends AbstractHandler {

	@Override
	final public Object execute(ExecutionEvent event) throws ExecutionException {
		validateCommandId(event);
		Object result = doExecute(event);
		return result;
	}

	protected Object doExecute(ExecutionEvent event) throws ExecutionException {
		return null;
	}

	/**
	 * @throws IllegalArgumentException
	 *           when the command id of the UI event that initiated this {@link AbstractHandler} to be
	 *           {@link #execute(ExecutionEvent) executed} does not match the {@link #getCommandId()
	 *           command id} that is expected to be configured for this class.
	 */
	protected void validateCommandId(ExecutionEvent event) {
		String actualCommandId = event.getCommand().getId();
		String expectedCommandId = getCommandId();
		if (actualCommandId.equals(expectedCommandId)) {
			return;
		}
		String className = getClass().getName();
		String msg = className + " has been registered in plugin.xml to handle commands with the id '"
				+ actualCommandId + "'; however, invoking getCommandId() on instances of " + className
				+ " returns '" + expectedCommandId + "'.\n\nPlease correct " + className
				+ ".getCommandId().";
		throw new IllegalArgumentException(msg);
	}

	/**
	 * override this if you do not obey the convention
	 * 
	 * @return
	 */
	protected String getCommandId() {
		return getCommandIdFromClass();
	}

	protected String getCommandIdFromClass() {
		String root = this.getClass().getPackage().getName();
		String className = this.getClass().getSimpleName();
		int end = className.lastIndexOf("HandlerPro");
		end = end >= 0 ? end : className.lastIndexOf("Handler");
		String menuSwizzle = Character.toLowerCase(className.charAt(0)) + className.substring(1, end)
				+ "Command";
		return root + "." + menuSwizzle;
	}

	protected void refreshUIElements() {
		MonkeyHandlerBase.refreshUIElements(this.getCommandId());
	}

	protected Command getCommand() {
		return getCommand(this.getCommandId());
	}

	protected static Command getCommand(String commandId) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ICommandService commandService = (ICommandService) window.getService(ICommandService.class);
		return commandService.getCommand(commandId);
	}

	protected static void refreshUIElements(String commandId) {
		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(
				ICommandService.class);
		if (commandService != null) {
			commandService.refreshElements(commandId, null);
		}
	}
}
