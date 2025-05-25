package controlador.interfaces;

public enum FuncionStatus {
	
	ON("encender"),
	OFF("apagar"),
	BLINK("parpadear");

	private String nombre = null;
	private FuncionStatus(String nombre) {
		this.nombre = nombre;
	}
	
	public String getNombre() {
		return this.nombre;
	}
	
	public static FuncionStatus getStatus(String code) {
		for(FuncionStatus f : FuncionStatus.values())
			if ( f.name().equalsIgnoreCase(code) || f.getNombre().equalsIgnoreCase(code) )
				return f;
		return null;
	}

}
