package com.laboki.eclipse.e4.plugin.autosave.automaticsaver.preferences;

public final class Preferences implements IPreferencesHandler {

	private static int saveIntervalInSeconds = PreferencesStore.getSaveIntervalInSeconds();
	private static boolean canSaveAutomatically = PreferencesStore.getCanSaveAutomatically();
	private static boolean canCheckErrors = PreferencesStore.getCanCheckErrors();
	private static boolean canCheckWarnings = PreferencesStore.getCanCheckWarnings();
	@SuppressWarnings("unused") private final PreferencesListener listener = new PreferencesListener(this);

	private Preferences() {}

	public static int saveIntervalInSeconds() {
		return Preferences.saveIntervalInSeconds;
	}

	public static boolean canSaveAutomatically() {
		return Preferences.canSaveAutomatically;
	}

	public static boolean canCheckErrors() {
		return Preferences.canCheckErrors;
	}

	public static boolean canCheckWarnings() {
		return Preferences.canCheckWarnings;
	}

	@Override
	public void preferencesChanged() {
		System.out.println("Preferences changed event handler called in Preferences module!");
		Preferences.saveIntervalInSeconds = PreferencesStore.getSaveIntervalInSeconds();
		Preferences.canSaveAutomatically = PreferencesStore.getCanSaveAutomatically();
		Preferences.canCheckErrors = PreferencesStore.getCanCheckErrors();
		Preferences.canCheckWarnings = PreferencesStore.getCanCheckWarnings();
	}
}