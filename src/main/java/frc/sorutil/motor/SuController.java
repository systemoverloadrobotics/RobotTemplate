package frc.sorutil.motor;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;

public interface SuController {
  public void configure(MotorConfiguration config); 

  MotorController rawController();

  public void tick();

  public void set(SuMotor.ControlMode mode, double setpoint);

  public void stop();

  public void follow(SuController other);
}
