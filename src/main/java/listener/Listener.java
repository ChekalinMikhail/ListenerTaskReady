package listener;

import domain.EppState;
import domain.bonds.MainBond;
import domain.bonds.Part2PartBond;
import domain.parts.EppRegistryElementPart;
import domain.versions.EppRegistryElement;
import domain.versions.EppRegistryProfModule;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Melton Smith
 * @since 01.12.2020
 */
public class Listener implements IListenProfModules {

    public static final IListenProfModules INSTANCE = new Listener();

    private Listener() {
    }

    @Override
    public boolean onEvent(Map<EppRegistryProfModule, List<EppRegistryElementPart>> profModules, Map<EppRegistryElement, List<EppRegistryElementPart>> regElements, Collection<MainBond> mainBonds, Collection<Part2PartBond> part2PartBonds) throws IllegalStateException {
        //проверяем все ли elements не ниже по уровню состояния со связанными profModules

        //TODO think about how can you group the data in the very beginning to avoid to many iterations. (just think, do not implement)
        profModules.keySet()
                .forEach(profModule -> elementStateLvl(profModule, mainBonds));

        //проверяем нет ли elements с количеством частей больше чем у связанного с ним profModule
        profModules.keySet()
                .forEach(profModule -> numberOfParts(profModule, mainBonds, profModules.get(profModule), regElements));

        //проверяем все ли elementParts распределены между profModuleParts (element обязательно связан с profModule через mainBond, лишних объектов нет)
        //а так же все ли elementParts распределены между profModuleParts в правильном порядке
        mainBonds.forEach(mainBond -> incompleteOrInvalidDistribution(mainBond.getModule(), mainBond.getElement(), regElements.get(mainBond.getElement()), part2PartBonds));

        return true;
    }

    /**
     * Этот метод находит в List<EppRegistryElement> все элементы, связанные с определенным EppRegistryProfModule и сравнивает их состояния.
     * В случае если состояние любого EppRegistryElement ниже чем у связанного с ним EppRegistryProfModule, метод возвращает false.
     */
    private void elementStateLvl(EppRegistryProfModule profModule, Collection<MainBond> mainBonds) throws IllegalStateException {
        List<EppState> requiredStates = new ArrayList<>();
        requiredStates.add(EppState.Accepted);
        if (profModule.getState().equals(EppState.Acceptable))
            requiredStates.add(EppState.Acceptable);

        //TODO invert matching condition. By that, you'll improve readability of your code.
        if (mainBonds.stream()
                .filter(bond -> bond.getModule().equals(profModule))
                .map(MainBond::getElement)
                .anyMatch(element -> !checkingStates(element, requiredStates)))
            throw new IllegalStateException(String.format("Нельзя согласовать/отправить на согласование профмодуль %s, т.к. вложенные мероприятия имеют неподходящее состояние.", profModule.getTitle()));
    }

    /**
     * TODO Rename the method. The name of the method in clean code is always supposed to give a reader some idea about
     * TODO what this method actually does.
     * TODO My hint here is: element -> isStateAcceptable(EppRegistryElement element, List<EppState> requiredStates)
     * Вспомогательный метод для elementStateLvl, определяет что состояние element не ниже уровнем чем у связанного с ним profModule.
     */
    private boolean checkingStates(EppRegistryElement element, List<EppState> requiredStates) {
        //TODO Well done, checking states is available for extension.
        //TODO Let's say on some day the product manager will decide to check not only Accepted and Acceptable, but also Declined.
        //TODO You can achieve this by simply putting another state in your "requiredStates" param.

        //TODO One noteworthy thing here: do not try to impress somebody by using lambda expression where it's not really needed.
        //TODO You could simply use:  requiredStates.contains(element.getState());
        //TODO Remember that any lambda is a anonymous class object in JVM with it's own overheads impacting your code performance.

        return requiredStates.stream()
                .anyMatch(requiredState -> requiredState.equals(element.getState()));
    }

    /**
     * Этот метод находит в List<EppRegistryElement> все элементы, связанные с определенным EppRegistryProfModule и сравнивает количество частей.
     * Если количество частей любого EppRegistryElement больше чем у связанного с ним EppRegistryProfModule, метод кидает IllegalStateException.
     */
    private void numberOfParts(EppRegistryProfModule profModule, Collection<MainBond> mainBonds, List<EppRegistryElementPart> profModuleParts, Map<EppRegistryElement, List<EppRegistryElementPart>> regElements) throws IllegalStateException {
        if (profModuleParts.get(0) == null)
            return;

        int maxNumberOfParts = profModuleParts.get(profModuleParts.size() - 1).getNumber();

        //TODO ! + allMatch => why not use "anyMatch" without "!"?. Try to inverse the condition.
        if (!mainBonds.stream()
                .filter(bond -> bond.getModule().equals(profModule))
                .map(MainBond::getElement)
                .allMatch(element -> ((regElements.get(element).size()) != 0) && (regElements.get(element).get(regElements.get(element).size() - 1).getNumber() <= maxNumberOfParts)))
            throw new IllegalStateException(String.format("Нельзя согласовать профмодуль %s, т.к. распределены не все части вложенных элементов.", profModule.getTitle()));
    }

    /**
     * Этот метод проверяет все ли части EppRegistryElement распределены между частями EppRegistryProfModule, а так же в правильном ли они порядке.
     * Если распределены не все части EppRegistryElement, либо нарушен порядок распределения, метод кидает IllegalStateException.
     */
    private void incompleteOrInvalidDistribution(EppRegistryProfModule profModule, EppRegistryElement element, List<EppRegistryElementPart> regElementParts, Collection<Part2PartBond> part2PartBonds) throws IllegalStateException {

        if (regElementParts.size() == 0)
            return;

        var bondsForCurrentModuleAndElement = part2PartBonds.stream()
                .filter(bond -> (bond.getElementPart().getParent().equals(element)))
                .sorted(Comparator.comparingInt((Part2PartBond bond) -> bond.getModulePart().getNumber()))
                .collect(Collectors.toList());

        //TODO why does your code throw the same exception in 2 different places? Try to avoid repeating yourself. Are there any options to do that?
        if (bondsForCurrentModuleAndElement.size() != regElementParts.size())
            throw new IllegalStateException(String.format("Нельзя согласовать профмодуль %s, т.к. распределены не все части вложенных элементов.", profModule.getTitle()));

        //проверяем порядок частей элемента
        for (int i = 1; i < bondsForCurrentModuleAndElement.size(); i++) {
            if (bondsForCurrentModuleAndElement.get(i - 1).getElementPart().getNumber() > bondsForCurrentModuleAndElement.get(i).getElementPart().getNumber())
                throw new IllegalStateException(String.format("Нельзя согласовать профмодуль %s, т.к. части вложенного мероприятия %s распределены в неправильном порядке.", profModule.getTitle(), element.getTitle()));
        }
    }
}
