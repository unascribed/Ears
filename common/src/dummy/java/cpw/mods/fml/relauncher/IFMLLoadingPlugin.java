package cpw.mods.fml.relauncher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * Never used. Allows the agent to compile against a generic version of the IFMLLoadingPlugin
 * interface that includes all methods that were ever present, since method overriding is not
 * checked at runtime.
 */
public interface IFMLLoadingPlugin {
	String[] getLibraryRequestClass(); // 1.4 and 1.5
	String[] getASMTransformerClass();
	String getModContainerClass();
	String getSetupClass();
	void injectData(Map<String, Object> map);

	// All following are 1.7 (maybe also 1.6?)
	
	String getAccessTransformerClass();
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface TransformerExclusions {
		public String[] value() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface MCVersion {
		public String value() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Name {
		public String value() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface DependsOn {
		public String[] value() default {};
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface SortingIndex {
		public int value() default 0;
	}

}