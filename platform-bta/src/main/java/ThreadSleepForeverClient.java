import com.unascribed.ears.common.agent.EarsAgent;

import net.minecraft.client.*;

public class ThreadSleepForeverClient extends Thread {

	final Minecraft mc;
	
	public ThreadSleepForeverClient(Minecraft mc, String name) {
		super(name);
		if (!EarsAgent.initialized) {
			System.err.println();
			System.err.println("ERROR: Ears for BTA is not a jar mod, it is a Java agent, and requires special installation.");
			System.err.println("Please read this: https://github.com/unascribed/Ears/wiki/Better-Than-Adventure-Installation");
			System.err.println();
			System.exit(2);
		}
		this.mc = mc;
		setDaemon(true);
		start();
	}

	@Override
	public void run() {
		while (mc.J) {
			try {
				Thread.sleep(Integer.MAX_VALUE);
			} catch (InterruptedException ignore) {}
		}
	}
}
