package frc.sorutil.motor;

import java.util.logging.Logger;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import frc.sorutil.Errors;
import frc.sorutil.motor.SuMotor.IdleMode;

public class SuVictorSpx implements SuController {
  private final Logger logger;

  private final WPI_VictorSPX victor;
  private MotorConfiguration config;
  boolean voltageControlOverrideSet = false;
  private Double lastVoltageCompensation = null;

  public SuVictorSpx(WPI_VictorSPX victor, String name) {
    int channel = victor.getDeviceID();
    logger = Logger.getLogger(String.format("VictorSPX(%d: %s)", channel, name));

    this.victor = victor;
  }

  @Override
  public void configure(MotorConfiguration config) {
    this.config = config;

    Errors.handleCtre(victor.configFactoryDefault(), logger, "resetting motor config");

    Errors.handleCtre(victor.config_kP(0, config.pidProfile().p()), logger, "setting P constant");
    Errors.handleCtre(victor.config_kI(0, config.pidProfile().i()), logger, "setting I constant");
    Errors.handleCtre(victor.config_kD(0, config.pidProfile().d()), logger, "setting D constant");
    Errors.handleCtre(victor.config_kF(0, config.pidProfile().f()), logger, "setting F constant");

    if (config.currentLimit() != null) {
      logger.warning(
          "Victor SPX initialized with current limit, current limits are NOT SUPPORTED, ignoring instruction.");
    }

    Errors.handleCtre(victor.configVoltageCompSaturation(SuMotor.DEFAULT_VOLTAGE_COMPENSTAION),
        logger, "configuring voltage compenstation");
    victor.enableVoltageCompensation(config.voltageCompenstationEnabled());

    NeutralMode desiredMode = NeutralMode.Coast;
    if (config.idleMode() == IdleMode.BRAKE) {
      desiredMode = NeutralMode.Brake;
    }
    victor.setNeutralMode(desiredMode);
  }

  @Override
  public MotorController rawController() {
    return victor;
  }

  @Override
  public void tick() {

  }

  private void restoreDefaultVoltageCompensation() {
    Errors.handleCtre(victor.configVoltageCompSaturation(SuMotor.DEFAULT_VOLTAGE_COMPENSTAION),
        logger, "configuring voltage compenstation");
    victor.enableVoltageCompensation(config.voltageCompenstationEnabled());
  }

  @Override
  public void set(SuMotor.ControlMode mode, double setpoint) {
    if (voltageControlOverrideSet && mode != SuMotor.ControlMode.VOLTAGE) {
      restoreDefaultVoltageCompensation();
      voltageControlOverrideSet = false;
      lastVoltageCompensation = null;
    }

    switch (mode) {
      case PERCENT_OUTPUT:
        victor.set(ControlMode.PercentOutput, setpoint);
      case POSITION:
        victor.set(ControlMode.Position, setpoint);
      case VELOCITY:
        victor.set(ControlMode.Velocity, setpoint);
      case VOLTAGE:
        boolean negative = setpoint < 0;
        double abs = Math.abs(setpoint);
        if (lastVoltageCompensation != null && setpoint != lastVoltageCompensation) {
          Errors.handleCtre(victor.configVoltageCompSaturation(abs), logger,
              "configuring voltage compenstation for voltage control");
          lastVoltageCompensation = setpoint;
        }
        if (!voltageControlOverrideSet) {
          victor.enableVoltageCompensation(true);
          voltageControlOverrideSet = true;
        }
        victor.set(ControlMode.PercentOutput, negative ? -1 : 1);
    }
  }

  @Override
  public void stop() {
    victor.stopMotor();
  }

  @Override
  public void follow(SuController other) { 

  }
}
