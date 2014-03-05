prefix="C:/JCell/temp/instanciasCSP/"
instancias=["2s.txt","3s.txt","A1s.txt","A2s.txt","A3.txt","A4.txt","A5.txt","ATP30.txt","ATP31.txt","ATP32.txt","ATP33.txt","ATP34.txt","ATP35.txt",
			"ATP36.txt","ATP37.txt","ATP38.txt","ATP39.txt","CHL1s.txt","CHL2s.txt","CHL3s.txt","CHL4s.txt","CHL5.txt","CHL6.txt","CHL7.txt","CU1.txt",
			"CU2.txt","CU3.txt","CU4.txt","CU5.txt","CU6.txt","CU7.txt","CU8.txt","CU9.txt","CU10.txt","CU11.txt","Hchl3s.txt","Hchl4s.txt","Hchl5s.txt",		
			"Hchl6s.txt","Hchl7s.txt","Hchl8s.txt","OF1.txt","OF2.txt","STS2s.txt","STS4s.txt","W.txt","wang1.txt","wang2.txt","wang3.txt"]

W=nil
L=nil
n=nil
opt=nil				

class Float
  def signif(signs)
    Float("%.#{signs}g" % self)
  end
end
			
File.open("salida_instancias.csv", 'w') do |file| 	
		
	file.puts "instancia;W;L;opt;n;amean;pmean;psd;bmean;bsd"	
			
	instancias.each do |instancia|			
		linea=0			
		wmin=1000
		lmin=1000
		wi=0
		li=0		
		b=[]
		w=[]
		l=[]
		p=[]
		wmean=0.0
		lmean=0.0
		pmean=0.0
		wsd=0.0
		lsd=0.0
		psd=0.0
		bmean=0.0
		bsd=0.0
		amean=0.0
		asd=0.0
		cont=0
		File.open("#{instancia}.csv", 'w') do |file_instancia| 	
			file_instancia.puts "W;L;opt;n;"	
			File.open("#{prefix}#{instancia}", "r").each_line do |line|	  										
				if(linea==0)
					# W L
					data = line.split(/ /)
					W=data[0].to_i
					L=data[1].to_i				
				end
				if(linea==1)
					# Optimo
					data = line.split(/ /)
					opt=data[0].to_i
				end
				if(linea==2)
					# N
					data = line.split(/ /)
					n=data[0].to_i			
					b=Array.new(n)
					w=Array.new(n)
					l=Array.new(n)
					file_instancia.puts "#{W};#{L};#{opt};#{n};"	
					file_instancia.puts "wi;li;bi;"	
				end
				if(linea>2)
					#wi li bi
					data = line.split(/ /)
					wi=data[0].to_i				
					w[cont]=data[0].to_i	
					li=data[1].to_i	
					l[cont]=data[1].to_i	
					b[cont]=data[2].to_i	
					p[cont]=2*(data[0].to_i+data[1].to_i)						
					wmean=bmean+data[0].to_i	
					lmean=bmean+data[1].to_i	
					bmean=bmean+data[2].to_i	
					pmean=pmean+2*(data[0].to_i+data[1].to_i)							
					amean=amean+data[0].to_i*data[1].to_i							
					file_instancia.puts "#{data[0].to_i};#{data[1].to_i};#{data[2].to_i};"					
					cont+=1
				end					
				linea+=1
			end
		end	
		wmean=wmean/n
		lmean=lmean/n
		pmean=pmean/n
		bmean=bmean/n
		amean=amean/n
		w.each do |wi|
			wsd=wsd+(wi-wmean)*(wi-wmean)
		end 
		l.each do |li|
			lsd=lsd+(li-lmean)*(li-lmean)
		end 
		b.each do |bi|
			bsd=bsd+(bi-bmean)*(bi-bmean)
		end 
		p.each do |pi|
			psd=psd+(pi-pmean)*(pi-pmean)
		end 
		wsd=Math.sqrt(wsd)
		lsd=Math.sqrt(lsd)
		bsd=Math.sqrt(bsd)
		psd=Math.sqrt(psd)
		cont=0
		file.puts "#{instancia};#{W};#{L};#{opt};#{n};#{amean.signif(4)};#{pmean.signif(4)};#{psd.signif(4)};#{bmean.signif(4)};#{bsd.signif(4)}"	
	end
end