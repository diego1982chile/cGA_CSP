class Float
  def signif(signs)
    Float("%.#{signs}g" % self)
  end
end
File.open("prueba.csv", 'w') do |file| 
	numero=Math.sqrt(2)	
	#fprintf( "%0.02f", numero )
	file.puts "#{numero.signif(4)};"	
end