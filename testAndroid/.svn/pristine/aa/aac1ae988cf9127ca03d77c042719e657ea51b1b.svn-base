package com.gorillalogic.monkeyconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.handlers.RadioState;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import com.gorillalogic.monkeyconsole.plugin.MonkeyTalkImagesEnum;

public class ConnectDropdownDynamicCommandItem extends CompoundContributionItem {

	public interface ItemSupplier {
		IContributionItem[] getContributionItems();
	}

	@SuppressWarnings("rawtypes")
	private static Class itemSupplierClass = ConnectToAgentItemSupplier.class;
	ItemSupplier itemSupplier = null;

	ItemSupplier getItemSupplier() {
		if (itemSupplier == null) {
			try {
				itemSupplier = (ItemSupplier) itemSupplierClass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return itemSupplier;
	}

	public ConnectDropdownDynamicCommandItem() {
		super();
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		return getItemSupplier().getContributionItems();
	}

	public static CommandContributionItem createRadioItem(String itemId, String commandId,
			String radioStateParam, ImageDescriptor icon, String label, String tooltip) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(RadioState.PARAMETER_ID, radioStateParam);
		CommandContributionItemParameter params = new CommandContributionItemParameter(
				PlatformUI.getWorkbench(), // serviceLocator
				itemId,  					// id of this item
				commandId,                        // commandId
				parameters,                // parameters
				icon, 						 // icon
				null,                      // disabledIcon
				null,                      // hoverIcon
				label,               		// label
				null,                      // mnemonic
				tooltip, 					// tooltip
				SWT.RADIO,                 // int style
				null,                      // String helpContextId
				true                       // visibleEnabled
		);
		return new CommandContributionItem(params);
	}

	private static class ConnectToAgentItemSupplier implements ItemSupplier {
		private static final String COMMAND_ID = ConnectToAgentHandler.COMMAND_ID;
		private static final String ITEM_ID_PREFIX = "com.gorillalogic.monkeyconsole.commands.connect";
		private String[] radioParams = {
				ConnectToAgentHandler.ANDROID_DEVICE_TETHERED,
				ConnectToAgentHandler.ANDROID_EMULATOR,
				ConnectToAgentHandler.ANDROID_WIFI,
				ConnectToAgentHandler.IOS_WIFI,
				ConnectToAgentHandler.IOS_SIMULATOR,
				ConnectToAgentHandler.NO_DEVICE
		};

		public ConnectToAgentItemSupplier() {
			super();
		}

		@Override
		public IContributionItem[] getContributionItems() {

			List<IContributionItem> items = new ArrayList<IContributionItem>();
			IContributionItem item;

			for (String radioParam : radioParams) {
				item = ConnectDropdownDynamicCommandItem.createRadioItem(
						ITEM_ID_PREFIX + radioParam + "Item",
						COMMAND_ID,
						radioParam,
						MonkeyTalkImagesEnum.BROWSE.image,
						"Connect to " + radioParam,
						"Connect to " + radioParam);
				items.add(item);
			}

			return (IContributionItem[]) items.toArray(new IContributionItem[items.size()]);
		}
	}

	public static void registerItemSupplierClass(Class klass) {
		itemSupplierClass = klass;
	}
}
