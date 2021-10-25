import com.unascribed.ears.common.agent.EarsAgent;

import net.minecraft.client.Minecraft;

public class fl extends Thread {
	private final Minecraft a;

	public fl(Minecraft var1, String var2) {
		super(var2);
		if (!EarsAgent.initialized) {
			System.err.println();
			System.err.println("ERROR: Ears for Not So Seecret Saturday is not a jar mod, it is a Java agent, and requires special installation.");
			System.err.println("Please read this: https://github.com/unascribed/Ears/wiki/NSSS-Installation");
			System.err.println();
			System.exit(2);
		}
		this.a = var1;
		this.setDaemon(true);
		this.start();
	}

	@Override
	public void run() {
		while(this.a.F) {
			try {
				Thread.sleep(2147483647L);
			} catch (InterruptedException var2) {
				;
			}
		}

	}
}