package listener;

import domain.bonds.MainBond;
import domain.bonds.Part2PartBond;
import domain.parts.EppRegistryElementPart;
import domain.versions.EppRegistryElement;
import domain.versions.EppRegistryProfModule;

import java.util.*;

/**
 * @author Melton Smith
 * @since 01.12.2020
 */
public class Listener implements IListenProfModules {

    public static final IListenProfModules INSTANCE = new Listener();

    private Listener() {}

    @Override
    public boolean onEvent(Map<EppRegistryProfModule, List<EppRegistryElementPart>> profModules, Map<EppRegistryElement, List<EppRegistryElementPart>> regElements, Collection<MainBond> mainBonds, Collection<Part2PartBond> part2PartBonds) {
        //TODO implement me
        return true;
    }
}
