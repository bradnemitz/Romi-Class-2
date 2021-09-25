// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Spark;


public class RomiDrivetrain {
  private static final double kCountsPerRevolution = 1440.0;
  private static final double kWheelDiameterInch = 2.75591; // 70 mm

  // The Romi has the left and right motors set to
  // PWM channels 0 and 1 respectively
  private final Spark m_leftMotor = new Spark(0);
  private final Spark m_rightMotor = new Spark(1);

  // The Romi has onboard encoders that are hardcoded
  // to use DIO pins 4/5 and 6/7 for the left and right
  private final Encoder m_leftEncoder = new Encoder(4, 5);
  private final Encoder m_rightEncoder = new Encoder(6, 7);

  /** Creates a new RomiDrivetrain. */
  public RomiDrivetrain() {
    // Use inches as unit for encoder distances
    m_leftEncoder.setDistancePerPulse((Math.PI * kWheelDiameterInch) / kCountsPerRevolution);
    m_rightEncoder.setDistancePerPulse((Math.PI * kWheelDiameterInch) / kCountsPerRevolution);
    resetEncoders();
  }

  public void arcadeDrive(double xaxisSpeed, double zaxisRotate) {
    //m_diffDrive.arcadeDrive(xaxisSpeed, zaxisRotate);
    //// Homework: Fill in this class to run the robot with arcade drive
    // Arcade Drive is: Left stick forward/back controls forward speed
    // right stick left/right controls rotation rate

    //Arcade drive code test 1
    //m_leftMotor.set(-xaxisSpeed+.5*zaxisRotate);
    //m_rightMotor.set(xaxisSpeed+.5*zaxisRotate);

    //Arcade drive code test 2
    xaxisSpeed = xaxisSpeed * xaxisSpeed * xaxisSpeed;
    zaxisRotate = zaxisRotate * zaxisRotate * zaxisRotate;
    m_leftMotor.set(-xaxisSpeed+zaxisRotate);
    m_rightMotor.set(xaxisSpeed+zaxisRotate);


  }

  public void tankDrive(double leftSpeed, double rightSpeed){
    m_leftMotor.set(-leftSpeed);
    m_rightMotor.set(rightSpeed);

  }

  public void resetEncoders() {
    m_leftEncoder.reset();
    m_rightEncoder.reset();
  }

  public double getLeftDistanceInch() {
    return m_leftEncoder.getDistance();
  }

  public double getRightDistanceInch() {
    return m_rightEncoder.getDistance();
  }
}
