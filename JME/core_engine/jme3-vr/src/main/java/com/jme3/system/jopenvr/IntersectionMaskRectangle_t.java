package com.jme3.system.jopenvr;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
/**
 * <i>native declaration : headers\openvr_capi.h:1547</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class IntersectionMaskRectangle_t extends Structure {
	public float m_flTopLeftX;
	public float m_flTopLeftY;
	public float m_flWidth;
	public float m_flHeight;
	public IntersectionMaskRectangle_t() {
		super();
	}
        @Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("m_flTopLeftX", "m_flTopLeftY", "m_flWidth", "m_flHeight");
	}
	public IntersectionMaskRectangle_t(float m_flTopLeftX, float m_flTopLeftY, float m_flWidth, float m_flHeight) {
		super();
		this.m_flTopLeftX = m_flTopLeftX;
		this.m_flTopLeftY = m_flTopLeftY;
		this.m_flWidth = m_flWidth;
		this.m_flHeight = m_flHeight;
	}
	public IntersectionMaskRectangle_t(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends IntersectionMaskRectangle_t implements Structure.ByReference {
		
	};
	public static class ByValue extends IntersectionMaskRectangle_t implements Structure.ByValue {
		
	};
}
