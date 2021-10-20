#!/usr/bin/fish
set coremod ears-forge-1.4 ears-forge-1.5
set agent ears-nfc ears-forge-1.2
set compat ears-fabric-1.16=ears-fabric-1.15 ears-fabric-1.17=ears-fabric-1.18 ears-forge-1.9=ears-forge-1.10,ears-forge-1.11
set hasCompat (echo $compat |tr ' ' '\n' |cut -d'=' -f1)
function doCopy
	set src $argv[1]
	set dst $argv[2]
	if contains $dst $agent
		rm -f ~/MultiMC/instances/$dst/.minecraft/ears.jar
		cp artifacts/$src*.jar ~/MultiMC/instances/$dst/.minecraft/ears.jar
	else 
		set dir mods
		if contains $dst $coremod
			set dir coremods
		end
		mkdir -p ~/MultiMC/instances/$dst/.minecraft/$dir/
		rm -f ~/MultiMC/instances/$dst/.minecraft/$dir/ears-*.jar
		cp artifacts/$src*.jar ~/MultiMC/instances/$dst/.minecraft/$dir
	end
end
for t in (ls -1 artifacts/ |rev |cut -d- -f 2- |rev)
	if contains $t $hasCompat
		for sub in (string split ',' (string split '=' (string match -ea $t= $compat)))
			doCopy $t $sub
		end
	else
		doCopy $t $t
	end
end
