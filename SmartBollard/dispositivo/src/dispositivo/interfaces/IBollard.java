package dispositivo.interfaces;

import java.util.Collection;

public interface IBollard {
    public IBollard iniciarBollard();
    public IBollard addFuncion(IFuncion f);
    public Collection<IFuncion> getFunciones();
    public IFuncion getFuncion(String funcionId);
}
