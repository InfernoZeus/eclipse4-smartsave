package com.laboki.eclipse.plugin.smartsave.listeners;

import org.eclipse.ui.IEditorPart;

import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionEndedEvent;
import com.laboki.eclipse.plugin.smartsave.events.AssistSessionStartedEvent;
import com.laboki.eclipse.plugin.smartsave.events.DisableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.EnableSaveListenersEvent;
import com.laboki.eclipse.plugin.smartsave.events.PartChangedEvent;
import com.laboki.eclipse.plugin.smartsave.instance.AbstractEventBusInstance;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;

public final class ListenerSwitch extends AbstractEventBusInstance {

	private final IEditorPart editor = EditorContext.getEditor();

	public ListenerSwitch(final EventBus eventBus) {
		super(eventBus);
	}

	@Subscribe
	public void toggleListeners(@SuppressWarnings("unused") final PartChangedEvent event) {
		new AsyncTask() {

			@Override
			public void asyncExecute() {
				ListenerSwitch.this.toggleSaverListeners();
			}
		}.begin();
	}

	@Subscribe
	public void toggleListeners(@SuppressWarnings("unused") final AssistSessionEndedEvent event) {
		new AsyncTask() {

			@Override
			public void asyncExecute() {
				ListenerSwitch.this.toggleSaverListeners();
			}
		}.begin();
	}

	@Subscribe
	public void disableListeners(@SuppressWarnings("unused") final AssistSessionStartedEvent event) {
		new AsyncTask() {

			@Override
			public void asyncExecute() {
				ListenerSwitch.this.postDisableListenersEvent();
			}
		}.begin();
	}

	private void toggleSaverListeners() {
		if (this.editor.isDirty()) this.postEnableListenersEvent();
		else this.postDisableListenersEvent();
	}

	private void postDisableListenersEvent() {
		EditorContext.cancelAllJobs();
		this.eventBus.post(new DisableSaveListenersEvent());
	}

	private void postEnableListenersEvent() {
		EditorContext.cancelAllJobs();
		this.eventBus.post(new EnableSaveListenersEvent());
	}
}
