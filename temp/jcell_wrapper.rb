archivo_conf="C:/JCell/src/cfg/genGAforCSP.cfg"
prefix="C:/JCell/temp/instanciasCSP/"
instancias=["3s.txt","A1s.txt","A2s.txt","A3.txt","A4.txt","A5.txt","ATP30.txt","ATP31.txt","ATP32.txt","ATP33.txt","ATP34.txt","ATP35.txt",
			"ATP36.txt","ATP37.txt","ATP38.txt","ATP39.txt","CHL1s.txt","CHL2s.txt","CHL3s.txt","CHL4s.txt","CHL5.txt","CHL6.txt","CHL7.txt","CU1.txt",
			"CU2.txt","CU3.txt","CU4.txt","CU5.txt","CU6.txt","CU7.txt","CU8.txt","CU9.txt","CU10.txt","CU11.txt","Hchl3s.txt","Hchl4s.txt","Hchl5s.txt",		
			"Hchl6s.txt","Hchl7s.txt","Hchl8s.txt","OF1.txt","OF2.txt","STS2s.txt","STS4s.txt","W.txt","wang1.txt","wang2.txt","wang3.txt",""]

instancia_anterior="2s.txt"			
ejecucion=1

instancias.each do |instancia|

	while(ejecucion<31)
		cmd = "java -jar JCell.jar  #{archivo_conf} #{ejecucion}"
		system cmd
		ejecucion+=1
	end	
		
	File.write(f = archivo_conf, File.read(f).gsub("#{prefix}#{instancia_anterior}","#{prefix}#{instancia}"))
	
	instancia_anterior=instancia
	ejecucion=1
end