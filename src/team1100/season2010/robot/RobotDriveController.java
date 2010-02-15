
package team1100.season2010.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.DigitalInput;

public class RobotDriveController
{
    private int drive_type;
    private int joystick_type;
    private int prev_drive_type;
    private int diagnostic_state;

    private int joystick_adjust_X;
    private int joystick_adjust_Y;

    public Joystick joystick_1;
    public Joystick joystick_2;
    private AdvJaguar front_right_motor;
    private AdvJaguar front_left_motor;
    private AdvJaguar back_right_motor;
    private AdvJaguar back_left_motor;
    private int motor_direction_adjust;

    private int FRM_channel;
    private int FLM_channel;
    private int BRM_channel;
    private int BLM_channel;

    private DigitalInput limit_front_max;
    private DigitalInput limit_front_min;
    private DigitalInput limit_back_max;
    private DigitalInput limit_back_min;

    //private RobotDrive tank_drive;

    private AverageController drive_motor_speed_setpoint;

    private ChainRotationMotor CRM_back;
    private ChainRotationMotor CRM_front;


    public RobotDriveController()
    {
        this(0,1,2,1,2,3,4,5,6,7,8,10);
    }

    
    public RobotDriveController(int type, int j1_channel, int j2_channel, int frm_channel,
                                int flm_channel, int brm_channel, int blm_channel, int CRM_front_channel, int CRM_back_channel,
                                int pot_front_channel, int pot_back_channel,int avgNum)
    {
        drive_type = type;
        joystick_type = 0;
        prev_drive_type = 1;
        diagnostic_state = 0;

        joystick_adjust_Y = 1;
        joystick_adjust_X = 1;

        joystick_1 = new Joystick(j1_channel);
        joystick_2 = new Joystick(j2_channel);

        front_right_motor = new AdvJaguar(frm_channel);
        FRM_channel = frm_channel;
        front_left_motor = new AdvJaguar(flm_channel);
        FLM_channel = flm_channel;
        back_right_motor = new AdvJaguar(brm_channel);
        BRM_channel = brm_channel;
        back_left_motor = new AdvJaguar(blm_channel);
        BLM_channel = blm_channel;

        drive_motor_speed_setpoint = new AverageController(avgNum);

        CRM_back = new ChainRotationMotor(CRM_back_channel, avgNum, pot_back_channel);
        CRM_front = new ChainRotationMotor(CRM_front_channel, avgNum, pot_front_channel);

        CRM_back.setInvertedMotor(false);
        CRM_front.setInvertedMotor(true);
        translationInit();

        limit_front_max = new DigitalInput(4,12);
        limit_front_min = new DigitalInput(4,11);
        limit_back_max = new DigitalInput(4,14);
        limit_back_min = new DigitalInput(4,13);
    }

    /*Sets drive type (tank, swerve, car...)
     * @param type : 0 = tank drive, 1 = car drive, 2 = swerve drive, 3 = swerve rotation, 4 = diagnostic
     * */
    public void setDriveType(int type)
    {
        drive_type = type;
        if(drive_type == 2)
            translationInit();
        if(drive_type == 3)
            rotationInit();
    }

    //@param type: 0 = one joystick mode, 1 = two joystick mode
    public void setJoystickType(int type)
    {
        joystick_type = type;
    }

    public void change90Mode()
    {
        if(drive_type == 22)
            drive_type = prev_drive_type;
        else 
        {
            if(drive_type != 33)
              prev_drive_type = drive_type;
            drive_type = 22;
        }
        translationInit();
    }

    /*public void change45Mode()
    {
        if(drive_type == 33)
            drive_type = prev_drive_type;
        else
        {
            if(drive_type != 22)
                prev_drive_type = drive_type;
            drive_type = 33;
        }
        rotationInit();
    }*/

    public void setInvertJoystickX()
    {
        joystick_adjust_X = joystick_adjust_X * -1;
        System.out.println("X inverted");
    }

    public void setInvertJoystickY()
    {
        joystick_adjust_Y = joystick_adjust_Y * -1;
        System.out.println("Y inverted");
    }

    public String getPotVals()
    {
        return "CRM Front: " + CRM_front.getPot() + "\n\tCRM Back: " + CRM_back.getPot() + "\n";
    }

    public void drive()
    {
        //if     (drive_type == 0)
        //    tankDrive();
        if(joystick_type == 1) //2 Joystick
        {
          if(drive_type == 1)
              carDrive();
          else if(drive_type == 2)
              swerveDrive();
          else if(drive_type == 3)
              swerveRotationDrive();
          else if(drive_type == 4)
              diagnostic();
          else if(drive_type == 22)
              translate90_TwoJoystick();
          //else if(drive_type == 33)
              //rotate45_TwoJoystick();
          else if(drive_type == 5)
              diagnosticLimitSwitches();
        }
        else  //1 joystick
        {
          if(drive_type == 1)
              carDriveOneJoystick();
          else if(drive_type == 2)
              swerveDriveOneJoystick();
          else if(drive_type == 3)
              swerveRotationDriveOneJoystick();
          else if(drive_type == 4)
              diagnostic();
          else if(drive_type == 22)
              translate90_OneJoystick();
          //else if(drive_type == 33)
              //rotate45_OneJoystick();
          else if(drive_type == 5)
              diagnosticLimitSwitches();
        }

        if(!limit_back_max.get() || !limit_back_min.get())
            CRM_back.setDirect(0);
        if(!limit_front_max.get() || !limit_front_min.get())
            CRM_front.setDirect(0);
    }

    public void driveAutonomous(double speed)
    {
        if(!CRM_back.atCenter())
            CRM_back.setCenter();
        if(!CRM_front.atCenter())
           CRM_front.setCenter();
        goTankDriveAutonomous(speed);
    }

    private void diagnostic()
    {

        System.out.println("CRM front: " + CRM_front.getPot());
        System.out.println("\tCRM back: " + CRM_back.getPot());

        if(joystick_1.getX()>.4)
          CRM_back.setDirect(.2);
        else if(joystick_1.getX()<-.4)
          CRM_back.setDirect(-.2);
        else CRM_back.setDirect(0);
        if(joystick_2.getX()>.4)
          CRM_front.setDirect(.2);
        else if(joystick_2.getX()<-.4)
          CRM_front.setDirect(-.2);
        else CRM_front.setDirect(0);
    }

    private void diagnosticLimitSwitches()
    {
        if(joystick_1.getTrigger())
        {
            if(diagnostic_state == 0)  //find max
            {
                CRM_front.setDirect(.2);
                if(!limit_front_max.get()) //hits limit switch
                {
                    CRM_front.setDirect(0);
                    System.out.println("CRM_front pot_max: " + CRM_front.getPot());
                    CRM_front.setPotMax(CRM_front.getPot());
                    diagnostic_state++;
                }
            }
            else if(diagnostic_state == 1) //find min
            {
                CRM_front.setDirect(-.2);
                if(!limit_front_min.get()) //hits limit switch 2
                {
                    CRM_front.setDirect(0);
                    System.out.println("CRM_front pot_min: " + CRM_front.getPot());
                    CRM_front.setPotMin(CRM_front.getPot());
                    diagnostic_state++;
                }
            }
            else if(diagnostic_state == 2)  //find max
            {
                CRM_back.setDirect(.2);
                if(!limit_back_max.get()) //hits limit switch
                {
                    CRM_back.setDirect(0);
                    System.out.println("CRM_back pot_max: " + CRM_back.getPot());
                    CRM_back.setPotMax(CRM_back.getPot());
                    diagnostic_state++;
                }
            }
            else if(diagnostic_state == 3) //find min
            {
                CRM_back.setDirect(-.2);
                if(!limit_back_min.get()) //hits limit switch 2
                {
                    CRM_back.setDirect(0);
                    System.out.println("CRM_back pot_min: " + CRM_back.getPot());
                    CRM_back.setPotMin(CRM_back.getPot());
                    diagnostic_state++;
                }
            }
        }

    }

    private void tankDrive()
    {
        if(!CRM_back.atCenter())
            CRM_back.setCenter();
        if(!CRM_front.atCenter())
           CRM_front.setCenter();
        goTankDrive();
    }

    private void goTankDrive()
    {
        front_right_motor.set(joystick_2.getY());
        front_left_motor.set(joystick_1.getY());
        back_right_motor.set(joystick_2.getY());
        back_left_motor.set(joystick_1.getY());
    }

    private void goTankDriveAutonomous(double spd)
    {
        front_right_motor.set(spd);
        front_left_motor.set(spd);
        back_right_motor.set(spd);
        back_left_motor.set(spd);
    }

    private void carDrive()
    {
        if(!CRM_front.atCenter())
           CRM_front.setCenter();

        drive_motor_speed_setpoint.addNewValue(-1* joystick_adjust_Y * joystick_2.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setWheelDirection(joystick_adjust_X * joystick_1.getX());
    }

    private void carDriveOneJoystick()
    {
        if(!CRM_front.atCenter())
           CRM_front.setCenter();

        drive_motor_speed_setpoint.addNewValue(-1* joystick_adjust_Y *joystick_1.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setWheelDirection(joystick_adjust_X * joystick_1.getX());
    }

    private void swerveDrive()
    {
       // System.out.println("\t\t\t\tPCOEFF: "+(joystick_1.getZ()+1)/2);
        //System.out.println("\t\t\t\tPCOEFF: " + (joystick_2.getZ()+1)/2);
       // CRM_back.setPCoeff((joystick_1.getZ()+1)/2);
        //CRM_front.setPCoeff((joystick_2.getZ()+1)/2);

        drive_motor_speed_setpoint.addNewValue(joystick_adjust_Y * joystick_2.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setWheelDirection(joystick_adjust_X * joystick_1.getX());
        CRM_front.setWheelDirection(-1 * joystick_adjust_X * joystick_1.getX());
    }

    private void swerveDriveOneJoystick()
    {
       // System.out.println("\t\t\t\tPCOEFF: "+(joystick_1.getZ()+1)/2);
        //System.out.println("\t\t\t\tPCOEFF: " + (joystick_2.getZ()+1)/2);
       // CRM_back.setPCoeff((joystick_1.getZ()+1)/2);
        //CRM_front.setPCoeff((joystick_2.getZ()+1)/2);

        drive_motor_speed_setpoint.addNewValue(joystick_adjust_Y * joystick_1.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setWheelDirection(joystick_adjust_X * joystick_1.getX());
        CRM_front.setWheelDirection(-1 * joystick_adjust_X * joystick_1.getX());
    }

    private void translate90_TwoJoystick()
    {
        drive_motor_speed_setpoint.addNewValue(joystick_adjust_Y * joystick_2.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setWheelDirection(joystick_adjust_X);
        CRM_front.setWheelDirection(-1 * joystick_adjust_X);
    }

    private void translate90_OneJoystick()
    {
        drive_motor_speed_setpoint.addNewValue(joystick_adjust_Y * joystick_1.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setWheelDirection(joystick_adjust_X);
        CRM_front.setWheelDirection(-1 * joystick_adjust_X);
    }

    private void swerveRotationDrive()
    {
        //CRM_back.setPCoeff((joystick_1.getZ()+1)/2);
        //CRM_front.setPCoeff((joystick_1.getZ()+1)/2);
        //System.out.println("\t\t\t\tPCOEFF: "+(joystick_1.getZ()+1)/2);
        //System.out.println("\t\t\t\tPCOEFF: " + (joystick_2.getZ()+1)/2);

        drive_motor_speed_setpoint.addNewValue(joystick_adjust_Y * joystick_2.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setWheelDirection(-1 * joystick_adjust_X * joystick_1.getX());
        CRM_front.setWheelDirection(-1 * joystick_adjust_X * joystick_1.getX());
    }

    private void swerveRotationDriveOneJoystick()
    {
        //CRM_back.setPCoeff((joystick_1.getZ()+1)/2);
        //CRM_front.setPCoeff((joystick_1.getZ()+1)/2);
        //System.out.println("\t\t\t\tPCOEFF: "+(joystick_1.getZ()+1)/2);
        //System.out.println("\t\t\t\tPCOEFF: " + (joystick_2.getZ()+1)/2);

        drive_motor_speed_setpoint.addNewValue(joystick_adjust_Y * joystick_1.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setWheelDirection(-1 * joystick_adjust_X * joystick_1.getX());
        CRM_front.setWheelDirection(-1 * joystick_adjust_X * joystick_1.getX());
    }

    /*private void rotate45_TwoJoystick()
    {
        drive_motor_speed_setpoint.addNewValue(joystick_adjust_Y * joystick_2.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        if(joystick_1.getX()<0)
        {
          CRM_back.setWheelDirection(joystick_adjust_X);
          CRM_front.setWheelDirection(joystick_adjust_X);
        }
        if(joystick_1.getX()>0)
        {
            CRM_back.setWheelDirection(-joystick_adjust_X);
            CRM_front.setWheelDirection(-joystick_adjust_X);
        }
    }

    private void rotate45_OneJoystick()
    {
        drive_motor_speed_setpoint.addNewValue(joystick_adjust_Y * joystick_1.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        if(joystick_1.getX()<0)
        {
          CRM_back.setWheelDirection(joystick_adjust_X);
          CRM_front.setWheelDirection(joystick_adjust_X);
        }
        if(joystick_1.getX()>0)
        {
            CRM_back.setWheelDirection(-joystick_adjust_X);
            CRM_front.setWheelDirection(-joystick_adjust_X);
        }
    }*/

    public void setInvertedMotor(boolean m1, boolean m2, boolean m3, boolean m4)
    {
        front_right_motor.setInvertedMotor(m1);
        front_left_motor.setInvertedMotor(m2);
        back_right_motor.setInvertedMotor(m3);
        back_left_motor.setInvertedMotor(m4);
    }

    private void translationInit()
    {
        CRM_back.setPotMax(547);
        CRM_back.setPotCenter(478);
        CRM_back.setPotMin(404);
        CRM_back.setMinSpeed(.4);
        CRM_back.setPCoeff(.7);

        CRM_front.setPotCenter(502);
        CRM_front.setPotMax(570);
        CRM_front.setPotMin(434);
        CRM_front.setMinSpeed(.4);
        CRM_front.setPCoeff(.76);
    }

    private void rotationInit()
    {
        CRM_back.setPotMax(512);
        CRM_back.setPotCenter(478);
        CRM_back.setPotMin(441);
        CRM_back.setMinSpeed(.4);
        CRM_back.setPCoeff(.7);

        CRM_front.setPotCenter(502);
        CRM_front.setPotMax(536);
        CRM_front.setPotMin(468);
        CRM_front.setMinSpeed(.4); 
        CRM_front.setPCoeff(.76);
    }

}
