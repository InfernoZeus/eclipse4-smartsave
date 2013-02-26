package com.laboki.eclipse.e4.plugin.autosave.automaticsaver;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;

// This class manages all the events needed to trigger automatic saving.
public final class AutomaticSaver {

	private final IEventBroker eventBroker;
	private final Listener bufferModificationListener;
	private final Listener partActivationListener;
	private final CaretMovementListener caretMovementListener = new CaretMovementListener(new CaretMovementListenerHandler());
	private static final String EDITOR_IS_MODIFIED = UIEvents.Dirtyable.TOPIC_DIRTY;
	private static final String EDITOR_IS_ACTIVE = UIEvents.UILifeCycle.ACTIVATE;
	private final SaveJobCountDownTimer saveCountDownTimer = new SaveJobCountDownTimer("AutoSaveJob");

	public AutomaticSaver(final MPart editorPart) {
		this.eventBroker = editorPart.getContext().get(IEventBroker.class);
		this.bufferModificationListener = new Listener(AutomaticSaver.EDITOR_IS_MODIFIED, new BufferModificationHandler(editorPart), this.eventBroker);
		this.partActivationListener = new Listener(AutomaticSaver.EDITOR_IS_ACTIVE, new PartActivationHandler(editorPart), this.eventBroker);
	}

	public void init() {
		this.startListeningForPartActivation();
		this.startListeningForBufferModification();
	}

	private void startListeningForPartActivation() {
		this.partActivationListener.start();
	}

	@SuppressWarnings("unused")
	private void stopListeningForPartActivation() {
		this.partActivationListener.stop();
	}

	protected void startListeningForBufferModification() {
		this.bufferModificationListener.start();
	}

	protected void stopListeningForBufferModification() {
		this.save();
		this.bufferModificationListener.stop();
	}

	private void save() {
		this.saveCountDownTimer.save();
	}

	protected void startAutomaticSaving() {
		if (!ActivePart.canSaveAutomatically()) return;
		this.startMonitoringEvents();
		this.startCountDownToSaveFile();
	}

	protected void stopAutomaticSaving() {
		this.stopMonitoringEvents();
		this.stopCountDownToSaveFile();
	}

	private void startCountDownToSaveFile() {
		this.saveCountDownTimer.restart();
	}

	private void stopCountDownToSaveFile() {
		this.saveCountDownTimer.stop();
	}

	private void startMonitoringEvents() {
		this.caretMovementListener.start();
	}

	private void stopMonitoringEvents() {
		this.caretMovementListener.stop();
	}

	private final class BufferModificationHandler implements ListenerHandler {

		private final MPart editor;

		public BufferModificationHandler(final MPart editorPart) {
			this.editor = editorPart;
		}

		@Override
		public void handle(final Object event) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					if (BufferModificationHandler.this.getEditor().isDirty()) AutomaticSaver.this.startAutomaticSaving();
					else AutomaticSaver.this.stopAutomaticSaving();
				}
			});
		}

		public MPart getEditor() {
			return this.editor;
		}
	}

	private final class PartActivationHandler implements ListenerHandler {

		private final MPart editor;

		public PartActivationHandler(final MPart editorPart) {
			this.editor = editorPart;
		}

		@Override
		public void handle(final Object event) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					final MPart activePart = (MPart) ((Event) event).getProperty(UIEvents.EventTags.ELEMENT);
					if (PartActivationHandler.this.editorIsActive(activePart)) AutomaticSaver.this.startListeningForBufferModification();
					else AutomaticSaver.this.stopListeningForBufferModification();
				}
			});
		}

		protected boolean editorIsActive(final MPart activePart) {
			if (ActivePart.isNotAnEditor(activePart)) return false;
			if (ActivePart.isNotTagged(activePart)) return false;
			return this.editor == activePart;
		}
	}

	private final class CaretMovementListenerHandler implements ListenerHandler, Runnable {

		public CaretMovementListenerHandler() {}

		@Override
		public void run() {
			AutomaticSaver.this.startAutomaticSaving();
		}

		@Override
		public void handle(final Object event) {
			Display.getDefault().asyncExec(this);
		}
	}

	@Override
	public String toString() {
		return String.format("AutomaticSaver [getClass()=%s]", this.getClass());
	}
}
