import com.unascribed.ears.common.agent.EarsAgent;

import net.minecraft.client.*;

public class kg extends Thread {

	final Minecraft a;
	
	public kg(Minecraft mc, String name) {
		super(name);
		if (!EarsAgent.initialized) {
			System.err.println();
			System.err.println("ERROR: Ears for NFC is not a jar mod, it is a Java agent, and requires special installation.");
			System.err.println("Please read this: https://github.com/unascribed/Ears/wiki/New-Frontier-Craft-Installation");
			System.err.println();
			System.exit(2);
		}
		this.a = mc;
		start();
	}

	@Override
	public void run() {
		while (a.J) {
			try {
				Thread.sleep(Integer.MAX_VALUE);
			} catch (InterruptedException ignore) {}
		}
	}
}
