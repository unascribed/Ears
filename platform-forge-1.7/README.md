# Ears Platform Forge 1.7

This is a platform implementation for Ears targetting Forge 1.7. It uses the agent-vlegacy Common
target.

1.7 is an awkward version, as it's after the UUID rework but before the new skin system. Originally,
this port tried to re-use the data from the vanilla data retrieval, but due to various bugs I
opted to instead treat 1.7 as vlegacy and eat the extra lookups.
