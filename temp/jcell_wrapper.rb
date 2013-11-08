ejecucion=1
archivo_conf="C:/JCell/src/cfg/genGAforCSP.cfg"

while(ejecucion<31)
	cmd = "java -jar JCell.jar  #{archivo_conf} #{ejecucion}"
	system cmd
	ejecucion+=1
end

