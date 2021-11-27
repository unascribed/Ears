<p align="center">
  <img src="https://unascribed.com/ears-banner.png?v=2" alt="Ears" width="512"/>
  <h3 align="center">Faithful fancy fashion features for fuzzy folk.</h3>
</p>

**The GitHub repository for Ears is now a [mirror of the Gitea repository](https://git.sleeping.town/unascribed/Ears).** GitHub-side issues will still be responded to.

Ears is a player model customization mod available for a dizzying number of Minecraft versions.

Get it and/or learn more at [CurseForge](https://www.curseforge.com/minecraft/mc-mods/ears), [Modrinth](https://modrinth.com/mod/ears),
or [Glass Repo](https://glass-repo.net/repo/mod/ears).

Check out the [Manipulator](https://unascribed.com/ears)!

**Mappings Notice**: Ears platform ports use a variety of mappings, including Plasma, Yarn, MCP, and Mojmap.
References to these mappings are made even in common code. *Viewer discretion is advised.*

## Using the API

![Current API version](https://img.shields.io/maven-metadata/v?color=%23FB0&label=current%20api%20version&metadataUrl=https%3A%2F%2Frepo.unascribed.com%2Fcom%2Funascribed%2Fears-api%2Fmaven-metadata.xml)

Ears provides an API (identical for all ports) that allows forcing Ears features to not render, or to change whether or not Ears thinks the player is wearing some kinds of equipment or has elytra equipped, etc.

You can add it to your mod like so (this example is for Fabric, but it's similar for Forge):

```gradle
repositories {
	maven {
		url "https://repo.unascribed.com"
		content {
			includeGroup "com.unascribed"
		}
	}
}

dependencies {
	modImplementation "com.unascribed:ears-api:1.4.1"
}
```

You can see examples of usage of both current APIs in real code in [Fabrication](https://github.com/unascribed/Fabrication/blob/1.17/src/main/java/com/unascribed/fabrication/features/FeatureHideArmor.java#L62) and [Yttr](https://github.com/unascribed/Yttr/blob/trunk/src/main/java/com/unascribed/yttr/compat/EarsCompat.java). Fabrication uses a state overrider to add support for its /hidearmor system, and Yttr uses the inhibitor system to force things not to render when the diving suit is worn.
