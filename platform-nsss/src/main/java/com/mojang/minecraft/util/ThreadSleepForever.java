package com.mojang.minecraft.util;

import com.mojang.minecraft.*;
import com.unascribed.ears.common.agent.EarsAgent;

public class ThreadSleepForever extends Thread
{
    final Minecraft mc;
    
    public ThreadSleepForever(final Minecraft minecraft, final String s) {
        super(s);
		if (!EarsAgent.initialized) {
			System.err.println();
			System.err.println("ERROR: Ears for Not So Seecret Saturday is not a jar mod, it is a Java agent, and requires special installation.");
			System.err.println("Please read this: https://git.sleeping.town/unascribed/Ears/wiki/NSSS-Installation");
			System.err.println();
			System.exit(2);
		}
        this.mc = minecraft;
        this.setDaemon(true);
        this.start();
    }
    
    @Override
    public void run() {
        while (this.mc.field_153_F) {
            try {
                Thread.sleep(2147483647L);
            }
            catch (InterruptedException ex) {}
        }
    }
}
