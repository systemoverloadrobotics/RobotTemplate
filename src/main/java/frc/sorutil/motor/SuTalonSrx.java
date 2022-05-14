package frc.sorutil.motor;

import java.util.logging.Logger;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import frc.sorutil.Errors;
import frc.sorutil.motor.SuMotor.IdleMode;

public class SuTalonSrx implements SuController {
  private static final double DEFAULT_CURRENT_LIMIT = 60;

  private final Logger logger;

  private final WPI_TalonSRX talon;
  private MotorConfiguration config;
  boolean voltageControlOverrideSet = false;
  private Double lastVoltageCompensation = null;

  public SuTalonSrx(WPI_TalonSRX talon, String name) {
    int channel = talon.getDeviceID();
    logger = Logger.getLogger(String.format("TalonSRX(%d: %s)", channel, name));

    this.talon = talon;
  }

  @Override
  public void configure(MotorConfiguration config) {
    this.config = config;

    Errors.handleCtre(talon.configFactoryDefault(), logger, "resetting motor config");

    Errors.handleCtre(talon.config_kP(0, config.pidProfile().p()), logger, "setting P constant");
    Errors.handleCtre(talon.config_kI(0, config.pidProfile().i()), logger, "setting I constant");
    Errors.handleCtre(talon.config_kD(0, config.pidProfile().d()), logger, "setting D constant");
    Errors.handleCtre(talon.config_kF(0, config.pidProfile().f()), logger, "setting F constant");

    double limit = DEFAULT_CURRENT_LIMIT;
    if (config.currentLimit() != null) {
      limit = config.currentLimit();
    }

    SupplyCurrentLimitConfiguration limitConfig = new SupplyCurrentLimitConfiguration();
    limitConfig.currentLimit = limit;
    limitConfig.triggerThresholdCurrent = limit;

    Errors.handleCtre(talon.configSupplyCurrentLimit(limitConfig), logger, "setting current limit");

    restoreDefaultVoltageCompensation();

    NeutralMode desiredMode = NeutralMode.Coast;
    if (config.idleMode() == IdleMode.BRAKE) {
      desiredMode = NeutralMode.Brake;
    }
    talon.setNeutralMode(desiredMode);
  }

  @Override
  public MotorController rawController() {
    return talon;
  }

  @Override
  public void tick() {

  }

  private void restoreDefaultVoltageCompensation() {
    Errors.handleCtre(talon.configVoltageCompSaturation(SuMotor.DEFAULT_VOLTAGE_COMPENSTAION),
        logger, "configuring voltage compenstation");
    talon.enableVoltageCompensation(config.voltageCompenstationEnabled());
  }

  @Override
  public void set(SuMotor.ControlMode mode, double setpoint) {
    if (voltageControlOverrideSet && mode != SuMotor.ControlMode.VOLTAGE) {
      restoreDefaultVoltageCompensation();
      voltageControlOverrideSet = false;
      lastVoltageCompensation = null;
    }

    switch(mode) {
      case PERCENT_OUTPUT:
        talon.set(ControlMode.PercentOutput, setpoint);
      case POSITION:
        talon.set(ControlMode.Position, setpoint);
      case VELOCITY:
        talon.set(ControlMode.Velocity, setpoint);
      case VOLTAGE:
        boolean negative = setpoint < 0;
        double abs = Math.abs(setpoint);
        if (lastVoltageCompensation != null && setpoint != lastVoltageCompensation) {
          Errors.handleCtre(talon.configVoltageCompSaturation(abs), logger,
              "configuring voltage compenstation for voltage control");
          lastVoltageCompensation = setpoint;
        }
        if (!voltageControlOverrideSet) {
          talon.enableVoltageCompensation(true);
          voltageControlOverrideSet = true;
        }
        talon.set(ControlMode.PercentOutput, negative ? -1 : 1);
    }
  }

  @Override
  public void stop() {
    talon.stopMotor();
  }

  @Override
  public void follow(SuController other) { 

  }
}
