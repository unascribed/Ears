package com.unascribed.ears.common.util;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Basically a zero-allocation inline map.
 * <p>
 * <blockquote>
 * "Mom can we have switch expressions"<br/>
 * "We have switch expressions at home"<br/>
 * Switch expressions at home:<br/>
 * {@link Decider}
 * </blockquote>
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class Decider<K, V> {

	private static final ThreadLocal<Decider> inst = new ThreadLocal<Decider>() {
		@Override
		protected Decider initialValue() {
			return new Decider();
		}
	};
	
	public static <K, V> Decider<K, V> begin(K key) {
		Decider d = inst.get();
		d.needle = key;
		d.value = UNDECIDED;
		return d;
	}
	
	private static final Object UNDECIDED = new Object();
	
	private K needle;
	private V value;
	
	public Decider<K, V> map(K k, V v) {
		if (value == UNDECIDED && Objects.equals(needle, k)) {
			value = v;
		}
		return this;
	}
	
	public V orElse(V def) {
		V v = value;
		needle = null;
		((Decider)this).value = UNDECIDED;
		if (v == UNDECIDED) return def;
		return v;
	}
	
	public V get() {
		V v = value;
		needle = null;
		((Decider)this).value = UNDECIDED;
		if (v == UNDECIDED) throw new NoSuchElementException(Objects.toString(needle));
		return v;
	}
	
	private Decider() {}
	
}
