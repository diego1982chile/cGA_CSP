prefix="C:/JCell/temp/instanciasCSP/"
instancias=["2s.txt","3s.txt","A1s.txt","A2s.txt","A3.txt","A4.txt","A5.txt","ATP30.txt","ATP31.txt","ATP32.txt","ATP33.txt","ATP34.txt","ATP35.txt",
			"ATP36.txt","ATP37.txt","ATP38.txt","ATP39.txt","CHL1s.txt","CHL2s.txt","CHL3s.txt","CHL4s.txt","CHL5.txt","CHL6.txt","CHL7.txt","CU1.txt",
			"CU2.txt","CU3.txt","CU4.txt","CU5.txt","CU6.txt","CU7.txt","CU8.txt","CU9.txt","CU10.txt","CU11.txt","Hchl3s.txt","Hchl4s.txt","Hchl5s.txt",		
			"Hchl6s.txt","Hchl7s.txt","Hchl8s.txt","OF1.txt","OF2.txt","STS2s.txt","STS4s.txt","W.txt","wang1.txt","wang2.txt","wang3.txt"]

W=nil
L=nil
n=nil
opt=nil				
			
File.open("salida_instancias.csv", 'w') do |file| 	
	
	file.puts "instancia;W;L;opt;n;wmin;lmin"	
			
	instancias.each do |instancia|			
		linea=0			
		wmin=1000
		lmin=1000
		wi=0
		li=0		
		File.open("#{prefix}#{instancia}", "r").each_line do |line|	  		
			if(linea==0)
				data = line.split(/ /)
				W=data[0].to_i
				L=data[1].to_i
			end
			if(linea==1)
				data = line.split(/ /)
				opt=data[0].to_i
			end
			if(linea==2)
				data = line.split(/ /)
				n=data[0].to_i			
			end
			if(linea>2)
				#wi li bi
				data = line.split(/ /)
				wi=data[0].to_i
				li=data[1].to_i
				if(wi*li<wmin*lmin)
					wmin=wi
					lmin=li
				end
			end		
			linea+=1
		end	
		file.puts "#{instancia};#{W};#{L};#{opt};#{n};#{wmin};#{lmin}"	
	end
end