package controlador.interfaces;

import java.util.Collection;

public interface IDispositivo {

	public String getId();
	
	public IDispositivo iniciar();
	public IDispositivo detener();
		
	public IDispositivo addFuncion(IFuncion f);
	public IFuncion getFuncion(String funcionId);
	public Collection<IFuncion> getFunciones();
	public IDispositivo habilitar();
	public IDispositivo deshabilitar();
	public boolean isHabilitado();
}
