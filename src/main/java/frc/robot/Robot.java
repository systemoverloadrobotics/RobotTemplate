// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import org.littletonrobotics.junction.LogFileUtil;
import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.NT4Publisher;
import org.littletonrobotics.junction.wpilog.WPILOGReader;
import org.littletonrobotics.junction.wpilog.WPILOGWriter;

import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.sorutil.Logging;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends LoggedRobot {
  @SuppressWarnings("unused")
  private RobotContainer m_robotContainer;

  //private Logger logger;
  private java.util.logging.Logger javaLogger;

  public Robot(double period) {
    super(period);
  }

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    configureAdvantageKit();

    // Enable SOR Logging that copies Stderr to disk
    Logging.initLogging();

    javaLogger = java.util.logging.Logger.getLogger(Robot.class.getName());

    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();

    javaLogger.info("Robot started");
  }

  @SuppressWarnings("resource")
  public void configureAdvantageKit() {
    var advantageLogger = Logger.getInstance();

    // Metadata
    advantageLogger.recordMetadata("ProjectName", Constants.PROJECT_NAME);
    advantageLogger.recordMetadata("BuildDate", BuildConstants.BUILD_DATE);
    advantageLogger.recordMetadata("GitSHA", BuildConstants.GIT_SHA);
    advantageLogger.recordMetadata("GitDate", BuildConstants.GIT_DATE);
    advantageLogger.recordMetadata("GitBranch", BuildConstants.GIT_BRANCH);

    // Adds logging for the Rev Power Distribution board
    new PowerDistribution(1, Constants.POWER_MODULE_TYPE);

    switch (BuildConstants.DIRTY) {
      case 0:
        advantageLogger.recordMetadata("GitDirty", "All changes committed");
        break;
      case 1:
        advantageLogger.recordMetadata("GitDirty", "Uncomitted changes");
        break;
      default:
        advantageLogger.recordMetadata("GitDirty", "Unknown");
        break;
    }

    if (isReal()) {
      advantageLogger.addDataReceiver(new WPILOGWriter("/media/sda1")); // Log to USB Flash
      advantageLogger.addDataReceiver(new NT4Publisher()); // Publish data to NetworkTables
    } else {
      setUseTiming(false); // Disable time spacing when simulating, run as fast as possible
      String logPath = LogFileUtil.findReplayLog(); // Pull replay from Scope or prompt user
      advantageLogger.setReplaySource(new WPILOGReader(logPath));

      // Save output to same file, with a _sim suffix
      advantageLogger.addDataReceiver(new WPILOGWriter(LogFileUtil.addPathSuffix(logPath, "_sim"))); 
    }

    advantageLogger.start();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {
    javaLogger.info("Robot disabled");
  }

  @Override
  public void disabledPeriodic() {}

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    javaLogger.info("Autonomous started");
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    javaLogger.info("Teleop started");
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {}

  @Override
  public void testInit() {
    javaLogger.info("Test mode started");
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** Called while the robot is being simulated */
  @Override
  public void simulationPeriodic() {

  }
}
