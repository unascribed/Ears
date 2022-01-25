# Ears Platform Not So Seecret Saturday

This is a platform implementation for Ears targetting the Alpha 1.1.2 retrofork, Not So Seecret
Saturday. It uses the agent-vlegacy Common target.

NSSS is odd in that it deobfuscates most of the game, meaning there are a lot of duplicate classes
between the obf game and the deobf ones actually used by NSSS. It's easy to accidentally use the
wrong class in patches.

NSSS contains a minimal skin fix, which is used in this port as a shortcut to implementing integration
into Common's vlegacy.
