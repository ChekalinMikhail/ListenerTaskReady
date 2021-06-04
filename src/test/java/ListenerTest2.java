import domain.EppState;
import domain.bonds.MainBond;
import domain.bonds.Part2PartBond;
import domain.parts.EppRegistryElementPart;
import domain.versions.EppRegistryElement;
import domain.versions.EppRegistryProfModule;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import listener.IListenProfModules;
import listener.Listener;
import org.junit.*;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static util.TestUtils.*;

/**
 * @author Melton Smith
 * @since 05.12.2020
 */
@RunWith(JUnitParamsRunner.class)
public class ListenerTest2 extends Assert {

    private IListenProfModules listener;

    private static List<TestDataWrapper> wrongDistributionData() {
        return List.of(testCase2());
    }

    private static List<TestDataWrapper> wrongDistributionOrderData() {
        return List.of(testCase1());
    }

    /**
     * I expect "wrong distribution order" here because of
     * the element with index 3 (Вложенное мероприятие 3) and  the element with index 2 (Вложенное мероприятие 2)
     */
    public static TestDataWrapper testCase1(){
        List<EppRegistryProfModule> modules = new ArrayList<>() {{
            add(new EppRegistryProfModule("Модуль 1", EppState.Accepted));
        }};

        //profModules
        HashMap<EppRegistryProfModule, List<EppRegistryElementPart>> module2Parts = new HashMap<>(modules.size());

        createParts(module2Parts, modules.get(0), 4);


        //elements
        HashMap<EppRegistryElement, List<EppRegistryElementPart>> elem2Parts = new HashMap<>(modules.size());
        List<EppRegistryElement> eppRegistryElements = new ArrayList<>() {{
            add(new EppRegistryElement("Вложенное мероприятие 0", EppState.Accepted));
            add(new EppRegistryElement("Вложенное мероприятие 1", EppState.Accepted));
            add(new EppRegistryElement("Вложенное мероприятие 2", EppState.Accepted));
            add(new EppRegistryElement("Вложенное мероприятие 3", EppState.Accepted));
        }};

        //RegElements
        createParts(elem2Parts, eppRegistryElements.get(0), 2);
        createParts(elem2Parts, eppRegistryElements.get(1), 3);
        createParts(elem2Parts, eppRegistryElements.get(2), 4);
        createParts(elem2Parts, eppRegistryElements.get(3), 3);


        //relations
        Collection<MainBond> mainBonds = new ArrayList<>(modules.size());
        HashMap<EppRegistryProfModule, Collection<EppRegistryElement>> moduleToRegElements = new HashMap<>(modules.size());
        moduleToRegElements.put(modules.get(0), eppRegistryElements);


        //module to its bonds
        HashMap<EppRegistryProfModule, Collection<MainBond>> profModuleToBonds = new HashMap<>();

        moduleToRegElements.forEach((profModule, collection) ->{
            Collection<MainBond> mainBondsForModule = createMainBonds(profModule, collection);
            profModuleToBonds.put(profModule, mainBondsForModule);
            mainBonds.addAll(mainBondsForModule);
        });

        List<Part2PartBond> parts2Parts = new ArrayList<>();
        parts2Parts.addAll(createPart2Part(modules.get(0), eppRegistryElements.get(0), module2Parts, elem2Parts, profModuleToBonds, "2/2", "1/1"));
        parts2Parts.addAll(createPart2Part(modules.get(0), eppRegistryElements.get(1), module2Parts, elem2Parts, profModuleToBonds, "1/1", "2/2", "3/4"));
        parts2Parts.addAll(createPart2Part(modules.get(0), eppRegistryElements.get(2), module2Parts, elem2Parts, profModuleToBonds, "1/4", "2/2", "3/3", "4/1"));
        parts2Parts.addAll(createPart2Part(modules.get(0), eppRegistryElements.get(3), module2Parts, elem2Parts, profModuleToBonds, "1/2", "2/1", "3/4"));


        return new TestDataWrapper(module2Parts, elem2Parts, parts2Parts, mainBonds);
    }


    /**
     * I expect "not all element parts are distributed" because of the element with index 7
     */
    public static TestDataWrapper testCase2(){
        List<EppRegistryProfModule> modules = new ArrayList<>() {{
            add(new EppRegistryProfModule("Модуль 1", EppState.Accepted));
            add(new EppRegistryProfModule("Модуль 2", EppState.Accepted));
            add(new EppRegistryProfModule("Модуль 3", EppState.Accepted));
            add(new EppRegistryProfModule("Модуль 4", EppState.Acceptable));
            add(new EppRegistryProfModule("Модуль 5", EppState.Acceptable));
        }};

        //profModules
        HashMap<EppRegistryProfModule, List<EppRegistryElementPart>> module2Parts = new HashMap<>(modules.size());

        createParts(module2Parts, modules.get(0), 4);
        createParts(module2Parts, modules.get(1), 2);
        createParts(module2Parts, modules.get(2), 3);
        createParts(module2Parts, modules.get(3), 2);

        //elements
        HashMap<EppRegistryElement, List<EppRegistryElementPart>> elem2Parts = new HashMap<>(modules.size());
        List<EppRegistryElement> eppRegistryElements = new ArrayList<>() {{
            add(new EppRegistryElement("Вложенное мероприятие 0", EppState.Forming));
            add(new EppRegistryElement("Вложенное мероприятие 1", EppState.Archived));
            add(new EppRegistryElement("Вложенное мероприятие 2", EppState.Acceptable));
            add(new EppRegistryElement("Вложенное мероприятие 3", EppState.Accepted));
            add(new EppRegistryElement("Вложенное мероприятие 4", EppState.Forming));
            add(new EppRegistryElement("Вложенное мероприятие 5", EppState.Declined));
            add(new EppRegistryElement("Вложенное мероприятие 6", EppState.Accepted));
            add(new EppRegistryElement("Вложенное мероприятие 7", EppState.Accepted));
            add(new EppRegistryElement("Вложенное мероприятие 8", EppState.Accepted));
            add(new EppRegistryElement("Вложенное мероприятие 9", EppState.Declined));
            add(new EppRegistryElement("Вложенное мероприятие 10", EppState.Accepted));
            add(new EppRegistryElement("Вложенное мероприятие 11", EppState.Accepted));
            add(new EppRegistryElement("Вложенное мероприятие 12", EppState.Accepted));
        }};

        //RegElements
        createParts(elem2Parts, eppRegistryElements.get(3), 2);
        createParts(elem2Parts, eppRegistryElements.get(6), 2);
        createParts(elem2Parts, eppRegistryElements.get(7), 4);
        createParts(elem2Parts, eppRegistryElements.get(8), 0);


        //relations
        Collection<MainBond> mainBonds = new ArrayList<>(modules.size());
        HashMap<EppRegistryProfModule, Collection<EppRegistryElement>> moduleToRegElements = new HashMap<>(modules.size());
        moduleToRegElements.put(modules.get(0), List.of(eppRegistryElements.get(3), eppRegistryElements.get(6), eppRegistryElements.get(7), eppRegistryElements.get(8)));

        //module to its bonds
        HashMap<EppRegistryProfModule, Collection<MainBond>> profModuleToBonds = new HashMap<>();

        moduleToRegElements.forEach((profModule, collection) ->{
            Collection<MainBond> mainBondsForModule = createMainBonds(profModule, collection);
            profModuleToBonds.put(profModule, mainBondsForModule);
            mainBonds.addAll(mainBondsForModule);
        });

        List<Part2PartBond> parts2Parts = new ArrayList<>();
        parts2Parts.addAll(createPart2Part(modules.get(0), eppRegistryElements.get(3), module2Parts, elem2Parts, profModuleToBonds, "1/2", "2/1"));
        parts2Parts.addAll(createPart2Part(modules.get(0), eppRegistryElements.get(6), module2Parts, elem2Parts, profModuleToBonds, "1/2", "2/3"));

        return new TestDataWrapper(module2Parts, elem2Parts, parts2Parts, mainBonds);
    }


    @Test
    @Parameters(method = "wrongDistributionData")
    public void wrongDistributionTest(TestDataWrapper testDataWrapper){
        var illegalStateException = assertThrows(IllegalStateException.class, () -> listener.onEvent(testDataWrapper.getProfModules(), testDataWrapper.getRegistryElements(), testDataWrapper.getMainBonds(), testDataWrapper.getPart2PartBonds()));
        assertEquals("Нельзя согласовать профмодуль Модуль 1, т.к. распределены не все части вложенных элементов.", illegalStateException.getMessage());
    }

    @Test
    @Parameters(method = "wrongDistributionOrderData")
    public void wrongDistributionOrderTest(TestDataWrapper testDataWrapper){
        var illegalStateException = assertThrows(IllegalStateException.class, () -> listener.onEvent(testDataWrapper.getProfModules(), testDataWrapper.getRegistryElements(), testDataWrapper.getMainBonds(), testDataWrapper.getPart2PartBonds()));
        assertEquals("Нельзя согласовать профмодуль Модуль 1, т.к. части вложенного мероприятия Вложенное мероприятие 2 распределены в неправильном порядке.", illegalStateException.getMessage());
    }

    @Before
    public void getListener(){
        listener = Listener.INSTANCE;
    }

    @After
    public void endTest() {
        listener = null;
    }
}
