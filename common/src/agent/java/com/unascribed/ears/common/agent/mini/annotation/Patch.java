/*
 * Mini - an ASM-based class transformer reminiscent of MalisisCore and Mixin
 * 
 * The MIT License
 *
 * Copyright (c) 2017-2021 Una Thompson (unascribed) and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.unascribed.ears.common.agent.mini.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

public final class Patch {

	/**
	 * Flags a MiniTransformer as affecting the given class. Can be specified
	 * multiple times; beware that in that case, all your patch methods will
	 * scan each class for methods meeting the criteria.
	 */
	@Documented
	@Retention(RUNTIME)
	@Target(TYPE)
	@Repeatable(Classes.class)
	public @interface Class {
		String value();
	}
	
	@Documented
	@Retention(RUNTIME)
	@Target(TYPE)
	public @interface Classes {
		Class[] value();
	}

	/**
	 * Flags a method in a MiniTransformer as being a patcher for a given
	 * method. Can be specified multiple times to patch multiple methods in the
	 * same way. The method must take a PatchContext as its sole argument.
	 */
	@Documented
	@Retention(RUNTIME)
	@Target(METHOD)
	@Repeatable(Methods.class)
	public @interface Method {
		/**
		 * @return the runtime name and signature of the affected method, e.g. a(Lac;Lbas;)V
		 */
		String value();

		/**
		 * Flags a method in a MiniTransformer as being an optional patcher.
		 */
		@Documented
		@Retention(RUNTIME)
		@Target(METHOD)
		public @interface Optional {}
		/**
		 * Flags a method in a MiniTransformer as affecting control flow, and therefore requiring
		 * frames to be recomputed.
		 */
		@Documented
		@Retention(RUNTIME)
		@Target(METHOD)
		public @interface AffectsControlFlow {}
	}
	
	@Documented
	@Retention(RUNTIME)
	@Target(METHOD)
	public @interface Methods {
		Method[] value();
	}

	
}
