package com.unascribed.ears.api.features;

import java.util.UUID;

import org.teavm.jso.JSBody;

import com.unascribed.ears.EarsFeaturesLookup;
import com.unascribed.ears.api.Slice;

/**
 * Describes the state of every Ears feature for a player skin.
 */
public class EarsFeatures {

	private static final EarsFeaturesLookup lookup;
	static {
		EarsFeaturesLookup lookupTmp = null;
		if (!isJs()) {
			try {
				lookupTmp = (EarsFeaturesLookup)getLookupImpl();
			} catch (Throwable t) {
				t.printStackTrace();
				System.err.println("[Ears] Failed to load static feature lookup binder");
				lookupTmp = null;
			}
		}
		if (lookupTmp == null) {
			lookupTmp = new EarsFeaturesLookup() {
				
				@Override
				public EarsFeatures getByUsername(String username) {
					throw new AbstractMethodError();
				}
				
				@Override
				public EarsFeatures getById(UUID id) {
					throw new AbstractMethodError();
				}
			};
		}
		lookup = lookupTmp;
	}

	@JSBody(script="return true")
	private static boolean isJs() {
		return false;
	}
	
	private static Object getLookupImpl() throws Throwable {
		return Class.forName("com.unascribed.ears.common.EarsFeaturesStorage").getField("INSTANCE").get(null);
	}
	
	/**
	 * Look up known Ears features for the player with the given UUID. This will only work for
	 * players that the client can see and has loaded a skin for already.
	 */
	public static EarsFeatures getById(UUID id) {
		return lookup.getById(id);
	}
	
	/**
	 * Look up known Ears features for the player with the given username. This will only work for
	 * players that the client can see and has loaded a skin for already.
	 * <p>
	 * Should only be used in legacy versions where you don't have easy access to UUIDs.
	 */
	public static EarsFeatures getByUsername(String username) {
		return lookup.getByUsername(username);
	}
	
	public enum EarMode {
		NONE,
		ABOVE,
		SIDES,
		BEHIND,
		AROUND,
		FLOPPY,
		CROSS,
		OUT,
		TALL,
		TALL_CROSS,
	}
	public enum EarAnchor {
		CENTER,
		FRONT,
		BACK,
	}
	public enum TailMode {
		NONE,
		DOWN,
		BACK,
		UP,
		VERTICAL,
	}
	public enum WingMode {
		NONE,
		SYMMETRIC_DUAL,
		SYMMETRIC_SINGLE,
		ASYMMETRIC_L,
		ASYMMETRIC_R,
	}
	
	public static final EarsFeatures DISABLED = new EarsFeatures();
	
	public final boolean enabled;
	public final EarMode earMode;
	public final EarAnchor earAnchor;
	public final boolean claws;
	public final boolean horn;
	public final TailMode tailMode;
	public final int tailSegments;
	public final float tailBend0;
	public final float tailBend1;
	public final float tailBend2;
	public final float tailBend3;
	public final int snoutOffset;
	public final int snoutWidth;
	public final int snoutHeight;
	public final int snoutDepth;
	public final float chestSize;
	public final WingMode wingMode;
	public final boolean animateWings;
	public final boolean capeEnabled;
	public final boolean emissive;
	public final Slice emissiveSkin;
	public final Slice emissiveWing;
	
	public final AlfalfaData alfalfa;
	
	private EarsFeatures(Builder builder) {
		this.enabled = true;
		this.earMode = builder.earMode;
		this.earAnchor = builder.earAnchor;
		this.claws = builder.claws;
		this.horn = builder.horn;
		this.tailMode = builder.tailMode;
		this.tailSegments = builder.tailSegments;
		this.tailBend0 = builder.tailBend0;
		this.tailBend1 = builder.tailBend1;
		this.tailBend2 = builder.tailBend2;
		this.tailBend3 = builder.tailBend3;
		this.snoutOffset = builder.snoutOffset;
		this.snoutWidth = builder.snoutWidth;
		this.snoutHeight = builder.snoutHeight;
		this.snoutDepth = builder.snoutDepth;
		this.chestSize = builder.chestSize;
		this.wingMode = builder.wingMode;
		this.animateWings = builder.animateWings;
		this.capeEnabled = builder.capeEnabled;
		this.emissive = builder.emissive;
		this.emissiveSkin = builder.emissiveSkin;
		this.emissiveWing = builder.emissiveWing;
		this.alfalfa = builder.alfalfa;
	}

	private EarsFeatures() {
		this.enabled = false;
		this.earMode = EarMode.NONE;
		this.earAnchor = EarAnchor.CENTER;
		this.claws = false;
		this.horn = false;
		this.tailMode = TailMode.NONE;
		this.tailSegments = 0;
		this.tailBend0 = 0;
		this.tailBend1 = 0;
		this.tailBend2 = 0;
		this.tailBend3 = 0;
		this.snoutOffset = 0;
		this.snoutWidth = 0;
		this.snoutHeight = 0;
		this.snoutDepth = 0;
		this.chestSize = 0;
		this.wingMode = WingMode.NONE;
		this.animateWings = true;
		this.capeEnabled = false;
		this.emissive = false;
		this.emissiveSkin = Slice.EMPTY;
		this.emissiveWing = Slice.EMPTY;
		this.alfalfa = AlfalfaData.NONE;
	}
	
	@Override
	public String toString() {
		return "EarsFeatures["+
					"earMode="+earMode+", "+
					"earAnchor="+earAnchor+", "+
					"claws="+claws+", "+
					"horn="+horn+
					"tailMode="+tailMode+
					"tailSegments="+tailSegments+
					"tailBends=["+
						tailBend0+", "+
						tailBend1+", "+
						tailBend2+", "+
						tailBend3+
					"], "+
					"snoutOffset="+snoutOffset+", "+
					"snoutWidth="+snoutWidth+", "+
					"snoutHeight="+snoutHeight+", "+
					"snoutDepth="+snoutDepth+", "+
					(chestSize > 0 ? "chestSize="+chestSize+", " : "")+
					"wingMode="+wingMode+", "+
					"animateWings="+animateWings+", "+
					(capeEnabled ? "capeEnabled="+capeEnabled+", " : "")+
					"emissive="+emissive+", "+
					"emissiveSkin="+emissiveSkin+", "+
					"emissiveWing="+emissiveWing+", "+
					"alfalfa="+alfalfa+
				"]";
	}
	
	/**
	 * @deprecated <b>Internal</b>. Do not use.
	 */
	@Deprecated
	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		public Builder tailBends(float tailBend0, float tailBend1, float tailBend2, float tailBend3) {
			return tailBend0(tailBend0).tailBend1(tailBend1).tailBend2(tailBend2).tailBend3(tailBend3);
		}
		
		// AUTOGENERATED
		private EarMode earMode;
		private EarAnchor earAnchor;
		private boolean claws;
		private boolean horn;
		private TailMode tailMode;
		private int tailSegments;
		private float tailBend0;
		private float tailBend1;
		private float tailBend2;
		private float tailBend3;
		private int snoutOffset;
		private int snoutWidth;
		private int snoutHeight;
		private int snoutDepth;
		private float chestSize;
		private WingMode wingMode;
		private boolean animateWings;
		private boolean capeEnabled;
		private boolean emissive;
		private Slice emissiveSkin;
		private Slice emissiveWing;
		private AlfalfaData alfalfa;

		private Builder() {}

		public Builder earMode(EarMode earMode) {
			this.earMode = earMode;
			return this;
		}

		public Builder earAnchor(EarAnchor earAnchor) {
			this.earAnchor = earAnchor;
			return this;
		}

		public Builder claws(boolean claws) {
			this.claws = claws;
			return this;
		}

		public Builder horn(boolean horn) {
			this.horn = horn;
			return this;
		}

		public Builder tailMode(TailMode tailMode) {
			this.tailMode = tailMode;
			return this;
		}

		public Builder tailSegments(int tailSegments) {
			this.tailSegments = tailSegments;
			return this;
		}

		public Builder tailBend0(float tailBend0) {
			this.tailBend0 = tailBend0;
			return this;
		}

		public Builder tailBend1(float tailBend1) {
			this.tailBend1 = tailBend1;
			return this;
		}

		public Builder tailBend2(float tailBend2) {
			this.tailBend2 = tailBend2;
			return this;
		}

		public Builder tailBend3(float tailBend3) {
			this.tailBend3 = tailBend3;
			return this;
		}

		public Builder snoutOffset(int snoutOffset) {
			this.snoutOffset = snoutOffset;
			return this;
		}

		public Builder snoutWidth(int snoutWidth) {
			this.snoutWidth = snoutWidth;
			return this;
		}

		public Builder snoutHeight(int snoutHeight) {
			this.snoutHeight = snoutHeight;
			return this;
		}

		public Builder snoutDepth(int snoutDepth) {
			this.snoutDepth = snoutDepth;
			return this;
		}

		public Builder chestSize(float chestSize) {
			this.chestSize = chestSize;
			return this;
		}

		public Builder wingMode(WingMode wingMode) {
			this.wingMode = wingMode;
			return this;
		}

		public Builder animateWings(boolean animateWings) {
			this.animateWings = animateWings;
			return this;
		}

		public Builder capeEnabled(boolean capeEnabled) {
			this.capeEnabled = capeEnabled;
			return this;
		}

		public Builder emissive(boolean emissive) {
			this.emissive = emissive;
			return this;
		}

		public Builder emissiveSkin(Slice emissiveSkin) {
			this.emissiveSkin = emissiveSkin;
			return this;
		}

		public Builder emissiveWing(Slice emissiveWing) {
			this.emissiveWing = emissiveWing;
			return this;
		}

		public Builder alfalfa(AlfalfaData alfalfa) {
			this.alfalfa = alfalfa;
			return this;
		}
		
		public EarMode getEarMode() {
			return earMode;
		}

		public EarAnchor getEarAnchor() {
			return earAnchor;
		}

		public boolean isClaws() {
			return claws;
		}

		public boolean isHorn() {
			return horn;
		}

		public TailMode getTailMode() {
			return tailMode;
		}

		public int getTailSegments() {
			return tailSegments;
		}

		public float getTailBend0() {
			return tailBend0;
		}

		public float getTailBend1() {
			return tailBend1;
		}

		public float getTailBend2() {
			return tailBend2;
		}

		public float getTailBend3() {
			return tailBend3;
		}

		public int getSnoutOffset() {
			return snoutOffset;
		}

		public int getSnoutWidth() {
			return snoutWidth;
		}

		public int getSnoutHeight() {
			return snoutHeight;
		}

		public int getSnoutDepth() {
			return snoutDepth;
		}

		public float getChestSize() {
			return chestSize;
		}

		public WingMode getWingMode() {
			return wingMode;
		}

		public boolean isAnimateWings() {
			return animateWings;
		}

		public boolean isCapeEnabled() {
			return capeEnabled;
		}

		public boolean isEmissive() {
			return emissive;
		}

		public Slice getEmissiveSkin() {
			return emissiveSkin;
		}

		public Slice getEmissiveWing() {
			return emissiveWing;
		}

		public AlfalfaData getAlfalfa() {
			return alfalfa;
		}

		public EarsFeatures build() {
			return new EarsFeatures(this);
		}
	}

	
	
}
