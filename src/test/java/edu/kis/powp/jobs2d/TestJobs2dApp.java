package edu.kis.powp.jobs2d;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.command.gui.CommandManagerWindow;
import edu.kis.powp.jobs2d.command.gui.CommandManagerWindowCommandChangeObserver;
import edu.kis.powp.jobs2d.command.historyComponent.HistoryFeature;
import edu.kis.powp.jobs2d.drivers.Job2dDriverDecorator;
import edu.kis.powp.jobs2d.drivers.UsageMonitorObserver;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;
import edu.kis.powp.jobs2d.events.*;
import edu.kis.powp.jobs2d.features.CommandsFeature;
import edu.kis.powp.jobs2d.features.DrawerFeature;
import edu.kis.powp.jobs2d.features.DriverFeature;
import edu.kis.powp.jobs2d.features.MacroFeature;

public class TestJobs2dApp {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Setup test concerning preset figures in context.
     *
     * @param application Application context.
     */
    private static void setupPresetTests(Application application) {
        SelectTestFigureOptionListener selectTestFigureOptionListener = new SelectTestFigureOptionListener(
                DriverFeature.getDriverManager());
        SelectTestFigure2OptionListener selectTestFigure2OptionListener = new SelectTestFigure2OptionListener(
                DriverFeature.getDriverManager());

        application.addTest("Figure Joe 1", selectTestFigureOptionListener);
        application.addTest("Figure Joe 2", selectTestFigure2OptionListener);
    }

    /**
     * Setup test using driver commands in context.
     *
     * @param application Application context.
     */
    private static void setupCommandTests(Application application) {
        application.addTest("Load secret command", new SelectLoadSecretCommandOptionListener());
        application.addTest("Test command", new SelectLoadTestCommand());
        application.addTest("Run command", new SelectRunCurrentCommandOptionListener(DriverFeature.getDriverManager()));

        application.addTest("Make a command deep copy", new CopyCommandListener());

        application.addTest("Rotate: 36 degrees", new SelectCommandTransformationRotateOptionListener(36));
        application.addTest("Rotate: -15 degrees", new SelectCommandTransformationRotateOptionListener(-15));

        application.addTest("Scale: 2.5", new SelectCommandTransformationScaleOptionListener(2.5, 2.5));
        application.addTest("Scale: 0.3", new SelectCommandTransformationScaleOptionListener(0.3, 0.3));
        application.addTest("Scale: 1.2, 0.8", new SelectCommandTransformationScaleOptionListener(1.2, 0.8));

        application.addTest("Flip: horizontal", new SelectCommandTransformationFlipOptionListener(true));
        application.addTest("Flip: vertical", new SelectCommandTransformationFlipOptionListener(false));

        application.addTest("test VisitorCommandPattern", new SelectCommandUsageCounterVisitorTestListener());

        application.addTest("Load MACRO command", new SelectLoadMacroListener());
        application.addTest("Clear Macro command", new SelectClearMacroListener());
    }

    /**
     * Setup driver manager, and set default Job2dDriver for application.
     *
     * @param application Application context.
     */
    private static void setupDrivers(Application application) {
        Job2dDriver loggerDriver = new LoggerDriver();
        DriverFeature.addDriver("Logger driver", loggerDriver);

        DrawPanelController drawerController = DrawerFeature.getDrawerController();
        Job2dDriver driver = new LineDriverAdapter(drawerController, LineFactory.getBasicLine(), "basic");
        DriverFeature.addDriver("Line Simulator", driver);
        DriverFeature.getDriverManager().setCurrentDriver(new Job2dDriverDecorator(driver));
        DriverFeature.addDriver("Macro Driver (special line)", MacroFeature.getMacroAdapter());
        MacroFeature.getMacroAdapter().setDriver(driver);
        driver = new LineDriverAdapter(drawerController, LineFactory.getSpecialLine(), "special");
        DriverFeature.addDriver("Special line Simulator", driver);
        DriverFeature.updateDriverInfo();

        UsageMonitorObserver usageMonitorObserver = new UsageMonitorObserver();
        DriverFeature.getDriverManager().getChangePublisher().addSubscriber(usageMonitorObserver);
    }

    private static void setupWindows(Application application) {

        CommandManagerWindow commandManager = new CommandManagerWindow(CommandsFeature.getDriverCommandManager());
        application.addWindowComponent("Command Manager", commandManager);

        CommandManagerWindowCommandChangeObserver windowObserver = new CommandManagerWindowCommandChangeObserver(
                commandManager);
        CommandsFeature.getDriverCommandManager().getChangePublisher().addSubscriber(windowObserver);
    }

    /**
     * Setup menu for adjusting logging settings.
     *
     * @param application Application context.
     */
    private static void setupLogger(Application application) {

        application.addComponentMenu(Logger.class, "Logger", 0);
        application.addComponentMenuElement(Logger.class, "Clear log",
                (ActionEvent e) -> application.flushLoggerOutput());
        application.addComponentMenuElement(Logger.class, "Fine level", (ActionEvent e) -> logger.setLevel(Level.FINE));
        application.addComponentMenuElement(Logger.class, "Info level", (ActionEvent e) -> logger.setLevel(Level.INFO));
        application.addComponentMenuElement(Logger.class, "Warning level",
                (ActionEvent e) -> logger.setLevel(Level.WARNING));
        application.addComponentMenuElement(Logger.class, "Severe level",
                (ActionEvent e) -> logger.setLevel(Level.SEVERE));
        application.addComponentMenuElement(Logger.class, "OFF logging", (ActionEvent e) -> logger.setLevel(Level.OFF));
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                Application app = new Application("Jobs 2D");
                DrawerFeature.setupDrawerPlugin(app);
                CommandsFeature.setupCommandManager();
                MacroFeature.setMacroAdapter();
                DriverFeature.setupDriverPlugin(app);
                HistoryFeature.setupHistoryFeature(app);
                setupDrivers(app);
                setupPresetTests(app);
                setupCommandTests(app);
                setupLogger(app);
                setupWindows(app);
                app.getFreePanel().addMouseListener(new MouseClickDrawListener(app.getFreePanel()));

                app.setVisibility(true);
            }
        });
    }

}
