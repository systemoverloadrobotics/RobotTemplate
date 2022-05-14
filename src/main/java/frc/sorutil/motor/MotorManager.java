package frc.sorutil.motor;

import java.util.HashSet;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class MotorManager extends SubsystemBase{
  private static final MotorManager instance;
  static {
    instance = new MotorManager();
  }

  private HashSet<SuMotor> motors = new HashSet<>();

  private MotorManager() {}

  public static MotorManager instance() {
    return instance;
  }

  @Override
  public void periodic() {
    for (SuMotor motor : motors) {
      motor.tick();
    }
  }
}
