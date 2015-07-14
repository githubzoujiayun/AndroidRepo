package com.gorillalogic.monkeyconsole.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import com.gorillalogic.monkeytalk.utils.IStringSetStore;

/**
 * Add set functionality for the values stored within an {@link IPreferenceStore}.
 * 
 * @author j0nm00re
 */
public interface ISetEnabledPreferenceStore extends IPreferenceStore, IStringSetStore {
}
