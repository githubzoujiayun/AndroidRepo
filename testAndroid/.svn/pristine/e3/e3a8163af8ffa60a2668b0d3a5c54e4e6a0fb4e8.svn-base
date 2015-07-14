package com.gorillalogic.monkeyconsole.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;

import com.gorillalogic.monkeytalk.utils.IStringSetStore;
import com.gorillalogic.monkeytalk.utils.StringSetStore;

/**
 * {@link IPreferenceStore} functionality implemented as pass through methods to the
 * {@link #preferenceStore} delegate.<br>
 * <br>
 * {@link IStringSetStore} functionality provided by extending {@link StringSetStore}.
 * 
 * @author j0nm00re
 * 
 */
public class SetEnabledPreferenceStore extends StringSetStore implements ISetEnabledPreferenceStore {
	private final IPreferenceStore preferenceStore;

	/**
	 * @param preferenceStore
	 *          the {@code IPreferenceStore} to be decorated with set functionality.
	 */
	public SetEnabledPreferenceStore(IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
	}

	@Override
	protected String get(String key) {
		String value = preferenceStore.getString(key);
		return value;
	}

	@Override
	protected void set(String key, String collection) {
		preferenceStore.setValue(key, collection);
	}

	@Override
	public void addPropertyChangeListener(IPropertyChangeListener arg0) {
		preferenceStore.addPropertyChangeListener(arg0);
	}

	@Override
	public boolean contains(String arg0) {
		return preferenceStore.contains(arg0);
	}

	@Override
	public void firePropertyChangeEvent(String arg0, Object arg1, Object arg2) {
		preferenceStore.firePropertyChangeEvent(arg0, arg1, arg2);
	}

	@Override
	public boolean getBoolean(String arg0) {
		return preferenceStore.getBoolean(arg0);
	}

	@Override
	public boolean getDefaultBoolean(String arg0) {
		return preferenceStore.getDefaultBoolean(arg0);
	}

	@Override
	public double getDefaultDouble(String arg0) {
		return preferenceStore.getDefaultDouble(arg0);
	}

	@Override
	public float getDefaultFloat(String arg0) {
		return preferenceStore.getDefaultFloat(arg0);
	}

	@Override
	public int getDefaultInt(String arg0) {
		return preferenceStore.getDefaultInt(arg0);
	}

	@Override
	public long getDefaultLong(String arg0) {
		return preferenceStore.getDefaultLong(arg0);
	}

	@Override
	public String getDefaultString(String arg0) {
		return preferenceStore.getDefaultString(arg0);
	}

	@Override
	public double getDouble(String arg0) {
		return preferenceStore.getDouble(arg0);
	}

	@Override
	public float getFloat(String arg0) {
		return preferenceStore.getFloat(arg0);
	}

	@Override
	public int getInt(String arg0) {
		return preferenceStore.getInt(arg0);
	}

	@Override
	public long getLong(String arg0) {
		return preferenceStore.getLong(arg0);
	}

	@Override
	public String getString(String arg0) {

		return preferenceStore.getString(arg0);
	}

	@Override
	public boolean isDefault(String arg0) {
		return preferenceStore.isDefault(arg0);
	}

	@Override
	public boolean needsSaving() {
		return preferenceStore.needsSaving();
	}

	@Override
	public void putValue(String arg0, String arg1) {
		preferenceStore.putValue(arg0, arg1);
	}

	@Override
	public void removePropertyChangeListener(IPropertyChangeListener arg0) {
		preferenceStore.removePropertyChangeListener(arg0);
	}

	@Override
	public void setDefault(String arg0, boolean arg1) {
		preferenceStore.setDefault(arg0, arg1);
	}

	@Override
	public void setDefault(String arg0, double arg1) {
		preferenceStore.setDefault(arg0, arg1);
	}

	@Override
	public void setDefault(String arg0, float arg1) {
		preferenceStore.setDefault(arg0, arg1);
	}

	@Override
	public void setDefault(String arg0, int arg1) {
		preferenceStore.setDefault(arg0, arg1);
	}

	@Override
	public void setDefault(String arg0, long arg1) {
		preferenceStore.setDefault(arg0, arg1);
	}

	@Override
	public void setDefault(String arg0, String arg1) {
		preferenceStore.setDefault(arg0, arg1);
	}

	@Override
	public void setToDefault(String arg0) {
		preferenceStore.setToDefault(arg0);
	}

	@Override
	public void setValue(String arg0, boolean arg1) {
		preferenceStore.setValue(arg0, arg1);
	}

	@Override
	public void setValue(String arg0, double arg1) {
		preferenceStore.setValue(arg0, arg1);
	}

	@Override
	public void setValue(String arg0, float arg1) {
		preferenceStore.setValue(arg0, arg1);
	}

	@Override
	public void setValue(String arg0, int arg1) {
		preferenceStore.setValue(arg0, arg1);
	}

	@Override
	public void setValue(String arg0, long arg1) {
		preferenceStore.setValue(arg0, arg1);
	}

	@Override
	public void setValue(String arg0, String arg1) {
		preferenceStore.setValue(arg0, arg1);
	}
}
