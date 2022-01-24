# Holy source folders, Batman!

Don't panic.

Ears Common is split into a lot of small pieces due to the disparate sets of platforms that Ears
can target (including *web browsers*). Here's what they all are, in order of scope:

## The Core

### api
This is published, API/ABI-stable classes exposed in the Maven artifact. Others are expected to use
these classes, and care must be taken to ensure no references are made to common and that none of
the classes will crash if used when Ears is not present (as may be the case in dev envs).

Every port includes api.

### main
Anything in main is included in all ports, including the browser. Care must be taken here not to do
anything incompatible with TeaVM.

This contains almost all of the meaningful common code, including the feature parser and renderer.

### normal
This is used by all non-JavaScript ports. Anything incompatible with TeaVM (such as usages of
java.util.concurrent) must go here.

### js
This is used by JavaScript ports; currently, this is just The Manipulator, but will Soon™ include
the Ears Gallery.

### dummy
These are dummy classes used to compile common without directly depending on anything. This includes
facades for old and new versions of FML, specially distorted LWJGL classes that late-bind, and a
stub implementation of EarsLog.

Nothing from this source folder ever makes it into a finished artifact.

## What year is it?

### modern
This source folder is empty at the moment, so it doesn't show up in Git. This is used by all
"modern" non-JS ports, and used to include RawEarsImage before that began being used by main.

"Modern" versions of Minecraft are those 1.13 and later; the switch to LWJGL3.

### legacy
This is used by all pre-1.13 Minecraft ports — those that use LWJGL2. It includes AWTEarsImage, and
some classes related to unmanaged rendering mainly used by Beta ports.

### vlegacy
This is additions to `legacy` used by "**v**ery **legacy**" versions of Minecraft; those before 1.8. It
contains MCAuthLib, Nanojson, and utilities to retrieve data from Mojang's skin servers.

## Will it blend?

### mixin
This is used by any port to a target that has SpongePowered Mixin. This is, more or less, Fabric
and post-1.13 Forge.

### agent
This is used by any port to a target that does *not* have Mixin. It contains ObjectWeb ASM, the
Mini patcher framework, and related gadgets.

# Putting it all together

These source sets are combined into various targets which can be used by a port or published on
their own. They are:

* **ears-common-api** - just `api` (published to Maven)
* **ears-common** - `api`, `main`, and `normal` (e.g. Visage)
* **ears-common-legacy** - `api`, `main`, `normal`, and `legacy` (intermediate, not used)
* **ears-common-modern** - `api`, `main`, `normal`, and `modern` (intermediate, not used)
* **ears-common-agent-vlegacy** - `api`, `main`, `normal`, `legacy`, `vlegacy`, and `agent` (e.g. Forge 1.5)
* **ears-common-agent-legacy** - `api`, `main`, `normal`, `legacy`, and `agent` (e.g. Forge 1.8)
* **ears-common-agent-modern** - `api`, `main`, `normal`, `modern`, and `agent` (not currently used)
* **ears-common-mixin-vlegacy** - `api`, `main`, `normal`, `legacy`, `vlegacy`, and `mixin` (e.g. Fabric Cursed Legacy b1.7.3)
* **ears-common-mixin-legacy** - `api`, `main`, `normal`, `legacy`, and `mixin` (e.g. Fabric Legacy 1.8)
* **ears-common-mixin-modern** - `api`, `main`, `normal`, `modern`, and `mixin` (e.g. Fabric 1.16)

