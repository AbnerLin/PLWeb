package org.plweb.jedit;

import java.awt.Color;

public interface MessageConsoleInterface {
	public abstract void init();

	public abstract String getAllText();

	public abstract void switchTo(int idx);

	public abstract void println(String text);

	public abstract void print(String text);

	public abstract void println(String text, Color color);

	public abstract void print(String text, Color color);
}
