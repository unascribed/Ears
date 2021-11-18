package com.unascribed.ears;

public class Identified<T> {

	private final String namespace;
	private final T value;
	
	private Identified(String namespace, T value) {
		this.namespace = namespace;
		this.value = value;
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public T getValue() {
		return value;
	}
	
	public <U> Identified<U> with(U newValue) {
		return of(namespace, newValue);
	}
	
	public static <T> Identified<T> of(String namespace, T value) {
		return new Identified<T>(namespace, value);
	}
	
}
