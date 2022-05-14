package frc.sorutil.motor;

import java.util.logging.Logger;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import frc.sorutil.Errors;
import frc.sorutil.motor.SuMotor.ControlMode;

public class SuSparkMax implements SuController {
  private static final double STALL_LIMIT = 30;
  private static final double DEFAULT_CURRENT_LIMIT = 70;

  private final Logger logger;

  private final CANSparkMax sparkMax;

  public SuSparkMax(CANSparkMax sparkMax, String name) {
    int channel = sparkMax.getDeviceId();
    logger = Logger.getLogger(String.format("SparkMax(%d: %s)", channel, name));

    this.sparkMax = sparkMax;
  }

  @Override
  public void configure(MotorConfiguration config) {
    Errors.handleRev(sparkMax.restoreFactoryDefaults(), logger, "resetting motor config");

    sparkMax.enableVoltageCompensation(12);
    if (!config.voltageCompenstationEnabled()) {
      sparkMax.disableVoltageCompensation();
    }

    Errors.handleRev(sparkMax.getPIDController().setP(config.pidProfile().p()), logger, "setting P constant");
    Errors.handleRev(sparkMax.getPIDController().setI(config.pidProfile().i()), logger, "setting I constant");
    Errors.handleRev(sparkMax.getPIDController().setD(config.pidProfile().d()), logger, "setting D constant");
    Errors.handleRev(sparkMax.getPIDController().setFF(config.pidProfile().f()), logger, "setting F constant");

    double limit = DEFAULT_CURRENT_LIMIT;
    if (config.currentLimit() != null) {
      limit = config.currentLimit();
    }

    Errors.handleRev(sparkMax.setSmartCurrentLimit((int) STALL_LIMIT, (int) limit), logger, "setting current limit");

    CANSparkMax.IdleMode desiredMode = CANSparkMax.IdleMode.kCoast;
    if (config.idleMode() == SuMotor.IdleMode.BRAKE) {
      desiredMode = CANSparkMax.IdleMode.kBrake;
    }
    Errors.handleRev(sparkMax.setIdleMode(desiredMode), logger, "setting idle mode");
  }

  @Override
  public MotorController rawController() {
    return sparkMax;
  }

  @Override
  public void tick() {

  }

  @Override
  public void set(ControlMode mode, double setpoint) {
    switch(mode) {
      case PERCENT_OUTPUT:
        sparkMax.set(setpoint);
      case POSITION:
        sparkMax.getPIDController().setReference(setpoint, ControlType.kPosition);
      case VELOCITY:
        sparkMax.getPIDController().setReference(setpoint, ControlType.kVelocity);
      case VOLTAGE:
        sparkMax.getPIDController().setReference(setpoint, ControlType.kVoltage);
    }
  }

  @Override
  public void stop() {
    sparkMax.stopMotor();
  }

  @Override
  public void follow(SuController other) { 
    // Techincally, the spark max can follow other motor controllers, but 
  }
}