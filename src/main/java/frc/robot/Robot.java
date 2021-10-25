// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private final RomiDrivetrain m_drivetrain = new RomiDrivetrain();
  private final Joystick m_controller = new Joystick(0);

  private enum State {
    FORWARD_TANK,
    FORWARD_ARCADE,
    REVERSE_TANK,
    REVERSE_ARCADE,
    DRIVE_FORWARD,
    TURN_LEFT,
    IDLE;
  };

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
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
    SmartDashboard.putNumber("Left Distance", m_drivetrain.getLeftDistanceInch());
    SmartDashboard.putNumber("Right Distance", m_drivetrain.getRightDistanceInch());
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

    m_drivetrain.resetEncoders();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    // We want our robot to move forward 12 inches
    double leftDistance = m_drivetrain.getLeftDistanceInch();
    double rightDistance = m_drivetrain.getRightDistanceInch();
    // Homework: accuracy is your only challenge
    // Must happen within 10 seconds of autonomousInit
    double distance = (leftDistance + rightDistance) / 2.0;
    if(distance < 11) {
      m_drivetrain.arcadeDrive(0.6, 0);}
    else{
      m_drivetrain.arcadeDrive(0, 0);}

  }

  /** This function is called once when teleop is enabled. */

  int buttonCounter = 0;
  State current_state = State.FORWARD_TANK;
  State next_state = State.FORWARD_TANK;
  boolean inTriangleDrive;
  boolean justStarted;
  int driveCount;
  double leftDistanceStart;
  double rightDistanceStart;


  @Override
  public void teleopInit() {
    buttonCounter = 0;
    current_state = State.FORWARD_TANK;
    next_state = State.FORWARD_TANK;
    inTriangleDrive = false;
    driveCount = 0;
    leftDistanceStart = 0;
    rightDistanceStart = 0;
  }


  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {

    // HW 1: Finish this state machine to allow for forwards/backwards selection
    // Have one button to toggle which direction is forward and another button to toggle back
    // Complete code AND draw out the state machine (draw.io or paper or whatever)
    // Can be done with two separate state machines, or with one big state machine for each combination of fw/back, tank/arcade
    // Do this as one state machine with four states


    // HW 2: In state machine form, have your robot drive in a triangle
    // Robot should drive completely in a triangle off a single button press
    // Don't care about accuracy for this - turning and driving can be determined by time or by encoder/gyro values
    // State 0: Idle
    // State 1: Drive forward
    // State 2: Turn
    // State 3: Done

    // Implement this and draw the state machine

    boolean isYButtonPressed = m_controller.getRawButton(4); // Goes from Tank to Arcade
    boolean isXButtonPressed = m_controller.getRawButton(3); // Goes from Arcade to Tank

    boolean isAButtonPressed = m_controller.getRawButton(5); // Goes from Fwd to Rev
    boolean isBButtonPressed = m_controller.getRawButton(6); // Goes from Rev to Fwd

    boolean isButton7Pressed = m_controller.getRawButton(7); // Start triangleDrive
    boolean isButton8Pressed = m_controller.getRawButton(8); // Stop triangleDrive

    double leftSpeed = m_controller.getRawAxis(1);
    double rightSpeed = m_controller.getRawAxis(2);

    double speed = m_controller.getRawAxis(1);
    double rotation = m_controller.getRawAxis(3);

    if(isButton7Pressed == true){
      inTriangleDrive = true;
      justStarted = true;
    } else if (isButton8Pressed == true){
      inTriangleDrive = false;
      justStarted = false;
      next_state = State.FORWARD_TANK;
    }
    

    SmartDashboard.putBoolean("inTriangleDrive", inTriangleDrive);
    
    //State next_state = State.FORWARD_TANK;
    // Top level if statement determines current state
    if(inTriangleDrive == false){
      if(current_state == State.FORWARD_TANK) {
        // We are in arcade drive mode
        // These describe transitions (button presses)
        
        if (isYButtonPressed && isAButtonPressed) {
          next_state = State.REVERSE_ARCADE;
        } else  if(isYButtonPressed) {
          next_state = State.FORWARD_ARCADE;
        } else if (isAButtonPressed) {
          next_state = State.REVERSE_TANK;
        } else {
          next_state = State.FORWARD_TANK;
        }

        m_drivetrain.tankDrive(leftSpeed, rightSpeed);
    
      } else if(current_state == State.FORWARD_ARCADE){

        if (isXButtonPressed && isAButtonPressed) {
          next_state = State.REVERSE_TANK;
        } else  if(isXButtonPressed) {
          next_state = State.FORWARD_TANK;
        } else if (isAButtonPressed) {
          next_state = State.REVERSE_ARCADE;
        } else {
          next_state = State.FORWARD_ARCADE;
        }

        m_drivetrain.arcadeDrive(speed, rotation);

      } else if(current_state == State.REVERSE_TANK){

        if (isYButtonPressed && isBButtonPressed) {
          next_state = State.FORWARD_ARCADE;
        } else  if(isYButtonPressed) {
          next_state = State.REVERSE_ARCADE;
        } else if (isBButtonPressed) {
          next_state = State.FORWARD_TANK;
        } else {
          next_state = State.REVERSE_TANK;
        }

        m_drivetrain.tankDrive(-leftSpeed, -rightSpeed);
        
      } else if(current_state == State.REVERSE_ARCADE){

        if (isXButtonPressed && isBButtonPressed) {
          next_state = State.FORWARD_TANK;
        } else  if(isXButtonPressed) {
          next_state = State.REVERSE_TANK;
        } else if (isBButtonPressed) {
          next_state = State.FORWARD_ARCADE;
        } else {
          next_state = State.REVERSE_ARCADE;
        }

        m_drivetrain.arcadeDrive(-speed, rotation);
        
      } else {
        m_drivetrain.tankDrive(0, 0);
      }
    } else if(inTriangleDrive == true){
      //auto routine for driving in a triangle


      //grab current encoder distances
      if(justStarted == true){
        next_state = State.DRIVE_FORWARD;
        leftDistanceStart = m_drivetrain.getLeftDistanceInch();
        rightDistanceStart = m_drivetrain.getRightDistanceInch();
        justStarted = false;
        driveCount = 0;
      }    
      
      //zero out encoder distances so we know how far we move right now
      double leftDistanceTravelled = m_drivetrain.getLeftDistanceInch() - leftDistanceStart;
      double rightDistanceTravelled = m_drivetrain.getRightDistanceInch() - rightDistanceStart;
    
      //move forward a bit
      double distance = (leftDistanceTravelled + rightDistanceTravelled) / 2.0;
      SmartDashboard.putNumber("left distance", leftDistanceTravelled);
      SmartDashboard.putNumber("right distance", rightDistanceTravelled);
      SmartDashboard.putNumber("distance", distance);
      if(current_state == State.DRIVE_FORWARD && distance < 11 && driveCount < 3) {
        m_drivetrain.arcadeDrive(-0.6, 0);
        next_state = State.DRIVE_FORWARD;
      } else if(current_state == State.DRIVE_FORWARD && distance >= 11 && driveCount < 3) {
        m_drivetrain.arcadeDrive(0, 0);
        
        leftDistanceStart = m_drivetrain.getLeftDistanceInch();
        rightDistanceStart = m_drivetrain.getRightDistanceInch();
        next_state = State.TURN_LEFT;
      } else {
        //do nothing
      }

      //want to draw an equilateral triangle, pivoting on one wheel
      //outside wheel draws an arc with radius ~5.5", circumference = 34.54"
      //needs to turn through 150 degrees
      //(150/360)*34.54 = 14.4"
      
      if(current_state == State.TURN_LEFT && rightDistanceTravelled < 10.25){
        m_drivetrain.tankDrive(0, -0.6);
        next_state = State.TURN_LEFT;
      } else if (current_state == State.TURN_LEFT && rightDistanceTravelled >= 10.25){
        m_drivetrain.tankDrive(0,0);
        leftDistanceStart = m_drivetrain.getLeftDistanceInch();
        rightDistanceStart = m_drivetrain.getRightDistanceInch();
        driveCount++;
        next_state = State.DRIVE_FORWARD;
      } else {
        //do nothing
      }

        //routine done, exit triangle drive
        if(driveCount == 3){
          next_state = State.FORWARD_TANK;
          inTriangleDrive = false;
        }
    }
    current_state = next_state;
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}