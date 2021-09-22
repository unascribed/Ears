#!/usr/bin/fish
set coremod ears-forge-1.4 ears-forge-1.5
set agent ears-nfc ears-forge-1.2
for t in (ls -1 artifacts/ |rev |cut -d- -f 2- |rev)
	if contains $t $agent
		rm -f ~/MultiMC/instances/$t/.minecraft/ears.jar
		cp artifacts/$t*.jar ~/MultiMC/instances/$t/.minecraft/ears.jar
	else 
		set dir mods
		if contains $t $coremod
			set dir coremods
		end
		mkdir -p ~/MultiMC/instances/$t/.minecraft/$dir/
		rm -f ~/MultiMC/instances/$t/.minecraft/$dir/ears-*.jar
		cp artifacts/$t*.jar ~/MultiMC/instances/$t/.minecraft/$dir
	end
end
rm -f ~/MultiMC/instances/ears-fabric-1.15/.minecraft/mods/ears-*.jar
cp artifacts/ears-fabric-1.16*.jar ~/MultiMC/instances/ears-fabric-1.15/.minecraft/mods
rm -f ~/MultiMC/instances/ears-fabric-1.18/.minecraft/mods/ears-*.jar
cp artifacts/ears-fabric-1.17*.jar ~/MultiMC/instances/ears-fabric-1.18/.minecraft/mods
