package frc.sorutil.motor;

public class MotorConfigurationError extends RuntimeException {
  public MotorConfigurationError(String error) {
    super(error);
  }
}
