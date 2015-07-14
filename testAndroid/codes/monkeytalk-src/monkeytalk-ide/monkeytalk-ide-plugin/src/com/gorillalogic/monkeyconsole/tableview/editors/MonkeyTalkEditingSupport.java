package com.gorillalogic.monkeyconsole.tableview.editors;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;

abstract public class MonkeyTalkEditingSupport extends EditingSupport {

	public MonkeyTalkEditingSupport(ColumnViewer viewer) {
		super(viewer);
	}

	protected boolean isUnchanged(Object element, Object newValue) {
		Object previousValue = getValue(element);
		return isUnchanged(element, newValue, previousValue);
	}

	static protected boolean isUnchanged(Object element, Object newValue, Object previousValue) {
		if (newValue == null && previousValue == null) {
			return true;
		}

		if (newValue == null) {
			return false;
		}

		boolean unchanged = newValue.equals(previousValue);
		return unchanged;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (value != null && (value instanceof String)) {
			value = ((String) value).trim();
		}
		if (isUnchanged(element, value)) {
			return;
		}
		storeValue(element, value);
	}

	abstract protected void storeValue(Object element, Object value);
}
