import com.unascribed.ears.common.agent.EarsAgent;

import net.minecraft.client.Minecraft;

public class hj extends Thread {

	final Minecraft a;
	
	public hj(Minecraft mc, String name) {
		super(name);
		if (!EarsAgent.initialized) {
			System.err.println();
			System.err.println("ERROR: Ears for 1.2.5 is not a jar mod, it is a Java agent, and requires special installation.");
			System.err.println("Please read this: https://github.com/unascribed/Ears/wiki/1.2.5-Installation");
			System.err.println();
			System.exit(2);
		}
		this.a = mc;
		start();
	}

	@Override
	public void run() {
		while (a.L) {
			try {
				Thread.sleep(Integer.MAX_VALUE);
			} catch (InterruptedException ignore) {}
		}
	}
}
