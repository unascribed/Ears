import com.unascribed.ears.Ears;
import com.unascribed.ears.common.agent.EarsAgent;

public class mod_Ears extends BaseMod {

	@Override
	public String getVersion() {
		return /*VERSION*/"1.4.2"/*/VERSION*/;
	}

	@Override
	public void load() {
		if (!EarsAgent.initialized) {
			System.err.println();
			System.err.println("ERROR: Ears for 1.2.5 is not a normal mod, it is a Java agent, and requires special installation.");
			System.err.println("Please read this: https://github.com/unascribed/Ears/wiki/1.2.5-Installation");
			System.err.println();
			System.exit(2);
		}
		Ears.init();
	}

}
