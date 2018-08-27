package com.jharter.game.util;

import com.badlogic.gdx.Gdx;

public class Sys {
	
	private static boolean enabled = true;
	
	public static void enable() {
		enabled = true;
	}
	
	public static void disable() {
		enabled = false;
	}
	
	private static String getStackTraceString() {
		StackTraceElement st = Thread.currentThread().getStackTrace()[4];
		String className = st.getClassName();
		className = className.substring(className.lastIndexOf(".")+1, className.length());
		return "[" + st.getLineNumber() + "] " + className + "." + st.getMethodName() + "()";
	}
	
	public static final Out out = new Out();
	public static final Err err = new Err();
	private static final Deb deb = new Deb();
	
	private Sys() {}
	
	public static class Out {
		
		private Out() {}
		
		public void println() {
			if(!enabled) {
				return;
			}
			Gdx.app.log(getStackTraceString(), "---");
		}
		
		public void println(String x) {
			if(!enabled) {
				return;
			}
			Gdx.app.log(getStackTraceString(), x);
		}
		
	}
	
	public static class Err {
		
		private Err() {}
		
		public void println() {
			if(!enabled) {
				return;
			}
			Gdx.app.error(getStackTraceString(), "---");
		}
		
		public void println(String x) {
			if(!enabled) {
				return;
			}
			Gdx.app.error(getStackTraceString(), x);
		}
		
	}
	
	public static class Deb {
		
		private Deb() {}
		
		public void println() {
			if(!enabled) {
				return;
			}
			Gdx.app.debug(getStackTraceString(), "---");
		}
		
		public void println(String x) {
			if(!enabled) {
				return;
			}
			Gdx.app.debug(getStackTraceString(), x);
		}
		
	}
	
}
