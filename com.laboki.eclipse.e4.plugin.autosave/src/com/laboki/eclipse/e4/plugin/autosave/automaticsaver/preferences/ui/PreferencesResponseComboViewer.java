package com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.ui;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.IPreferencesHandler;
import com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences.PreferencesListener;

class PreferencesResponseComboViewer extends ResponseComboViewer implements IPreferencesHandler {

	private final PreferencesListener listener = new PreferencesListener(this);
	public static final int YES = 0;
	public static final int NO = 1;

	public PreferencesResponseComboViewer(final Composite parent) {
		super(parent);
		this.setComboProperties();
		this.listener.start();
	}

	@Override
	protected void handleResponseSelection(final SelectionChangedEvent event) {}

	protected boolean getSelectionValue(final SelectionChangedEvent event) {
		super.handleResponseSelection(event);
		final boolean value = ((Response) ((IStructuredSelection) event.getSelection()).getFirstElement()).value();
		return value;
	}

	private void setComboProperties() {
		this.updateSelection();
	}

	protected void updateSelection() {}

	protected void setSelection(final int index) {
		this.stopListening();
		this.setSelection(new StructuredSelection(this.getResponses()[index]));
		this.startListening();
	}

	@Override
	public void preferencesChanged() {
		this.updateSelection();
	}
}