
package team1100.season2010.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Victor;

public class RobotDriveController
{
    private int drive_type;
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

        //chain_rotation_motor = new Jaguar(CRM_channel);

        CRM_back = new ChainRotationMotor(CRM_back_channel, avgNum, pot_back_channel);
        CRM_front = new ChainRotationMotor(CRM_front_channel, avgNum, pot_front_channel);

        tankInit();
        carInit(avgNum);
        swerveInit();
    }

    /*Sets drive type (tank, swerve, car...)
     * @param type : 0 = tank drive, 1 = car drive, 2 = swerve drive
     * */
    public void setDriveType(int type)
    {
        drive_type = type;
    }

    public String getPotVals()
    {
        return "CRM Front: " + CRM_front.getPot() + "\n\tCRM Back: " + CRM_back.getPot() + "\n";
    }

    public void drive()
    {
        if     (drive_type == 0)
            tankDrive();
        else if(drive_type == 1)
            carDrive();
        else if(drive_type == 2)
            swerveDrive();
    }

    private void tankDrive()
    {
        if(!CRM_back.atCenter())
            CRM_back.setCenter();
        if(!CRM_front.atCenter())
           CRM_front.setCenter();
        //tank_drive.tankDrive(joystick_1, joystick_2);
        goTankDrive();
    }

    private void goTankDrive()
    {
        front_right_motor.set(joystick_2.getY());
        front_left_motor.set(joystick_1.getY());
        back_right_motor.set(joystick_2.getY());
        back_left_motor.set(joystick_1.getY());
    }

    private void carDrive()
    {
        if(!CRM_front.atCenter())
           CRM_front.setCenter();

        drive_motor_speed_setpoint.addNewValue(joystick_2.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setWheelDirection(-joystick_1.getX());
    }

    private void swerveDrive()
    {
        //CRM_back.setPCoeff((joystick_1.getZ()+1)/2);
        //CRM_front.setPCoeff((joystick_1.getZ()+1)/2);
        System.out.println("\t\t\t\tPCOEFF: "+(joystick_1.getZ()+1)/2);

        drive_motor_speed_setpoint.addNewValue(joystick_2.getY());

        front_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        front_left_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_right_motor.set(drive_motor_speed_setpoint.getAverageValue());
        back_left_motor.set(drive_motor_speed_setpoint.getAverageValue());

        CRM_back.setWheelDirection(-joystick_1.getX());
        CRM_front.setWheelDirection(joystick_1.getX());
    }

    public void setInvertedMotor(boolean m1, boolean m2, boolean m3, boolean m4)
    {
        front_right_motor.setInvertedMotor(m1);
        front_left_motor.setInvertedMotor(m2);
        back_right_motor.setInvertedMotor(m3);
        back_left_motor.setInvertedMotor(m4);
    }

    private void tankInit()
    {
        //tank_drive = new RobotDrive(FLM_channel, BLM_channel, FRM_channel, BRM_channel);
    }

    private void carInit(int avgNum)
    {
        drive_motor_speed_setpoint = new AverageController(avgNum);

        CRM_back.setPotMax(548);
        CRM_back.setPotCenter(481);
        CRM_back.setPotMin(401);
        CRM_back.setInvertedMotor(false);

        CRM_front.setPotCenter(436);
        CRM_front.setPotMax(513);
        CRM_front.setPotMin(365);
        CRM_front.setInvertedMotor(true);
    }

    private void swerveInit()
    {

    }

}