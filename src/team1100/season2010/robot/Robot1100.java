package team1100.season2010.robot;


import team1100.season2010.robot.DashboardPacker;
import edu.wpi.first.wpilibj.IterativeRobot;

import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.camera.AxisCameraException;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Dashboard;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot1100 extends IterativeRobot
{
    //Counts how many periodic cycles have passed.
    int m_count;

    // crm code
    // AnalogChannel pot_1 = new AnalogChannel(7);

    Joystick joystick_1;
    Joystick joystick_2;

    //RobotDrive drive;

    final int POT_RANGE = 10;
    final int DIGPORT = 4;
    int prev_pot;
    final double JOYSTICK_DEADBAND = .5;
    double prev_speed;
    double setpt_speed;
    final double SPEED_ADJUST = .1;

    final int MOTOR_DIRECTION_ADJUST = -1;

    Jaguar front_right_motor;
    Jaguar front_left_motor;
    Jaguar back_right_motor;
    Jaguar back_left_motor;
    Jaguar chain_rotation_motor;

    double[] speed_array;
    final int NUM_SPEED_ARRAY = 10;
    int speed_array_index;
    double avg_speed_val;

    double[] dir_array;
    final int NUM_DIR_ARRAY = 10;
    int dir_array_index;
    double avg_dir_val;

    final int POT_MIN = 374;
    final int POT_MAX = 579;
    final int POT_CENTER = 483;
    final int POT_DEADBAND = 10;

    //CRM = chain_rotation_motor
    double[] CRM_speed_array;
    final int NUM_CRM_SPEED_ARRAY = 10;
    int CRM_speed_array_index;
    double avg_CRM_speed_val;

    final double CRM_SPEED = .2;

    // mbh code
    SteeringPID steering;
    Kicker kicker;

    boolean m_triggerPulled = false;
    boolean m_topPushed = false;
    int m_kickerTestCycle = 0;
    int m_kickerTestIndex = 0;

    AxisCamera cam;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit()
    {
        //Sets periodic call rate to 10 milisecond intervals, i.e. 100Hz.
        this.setPeriod(0.01);
        System.out.print("ROBOT STARTUP");

        //drive = new RobotDrive(1,5);
        //drive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, false);
        //drive.setInvertedMotor(RobotDrive.MotorType.kRearRight, false);

        //pid = new PIDController(.1,.001,0, pot_1, testMotor);
        //pid.setInputRange(0, 1024);

        speed_array = new double[NUM_SPEED_ARRAY];
        speed_array_index = 0;
        avg_speed_val = 0;

        CRM_speed_array = new double[NUM_CRM_SPEED_ARRAY];
        CRM_speed_array_index = 0;
        avg_CRM_speed_val = 0;

        dir_array = new double[NUM_DIR_ARRAY];
        dir_array_index = 0;
        avg_dir_val = 0;


        joystick_1 = new Joystick(1);
        joystick_2 = new Joystick(2);

        front_right_motor = new Jaguar(DIGPORT,1);
        front_left_motor = new Jaguar(DIGPORT,5);
        //back_right_motor = new Jaguar(DIGPORT,3);
        //back_left_motor = new Jaguar(DIGPORT,4);
        // chain_rotation_motor = new Jaguar(DIGPORT,3);
        // crm code
        //prev_pot = pot_1.getAverageValue();

        cam = AxisCamera.getInstance();

        /* mbh code */
        final int steeringPot = 7;
        final int steeringJag = 3;
        steering = new SteeringPID(steeringPot, steeringJag, true);
        steering.setCenterPct(50.6);
        steering.setLinearPct(3);

        kicker = new Kicker();
        // kicker.arm();
         /* end mbh code*/
    }

    /**
     * This function is called when the robot enters autonomous mode.
     */
    public void autonomousInit()
    {
        m_count = 0;
        System.out.println("Autonomous Init");
    }

    /**
     * This function is called periodically (100Hz) during autonomous
     */
    public void autonomousPeriodic()
    {
        m_count++;
        //System.out.println("AutoCount: " + m_count);

        //Runs periodically at 100Hz
        {

        }

        //Runs periodically at 50Hz.
        if (m_count % 2 == 0)
        {

        }

        //Runs periodically at 25Hz.
        if (m_count % 4 == 0)
        {

        }

        //Runs periodically at 20Hz.
        if (m_count % 5 == 0)
        {
            DashboardPacker.updateDashboard();
            System.out.println("Packet Sent (Auto)");

        }

        //Runs periodically at 10Hz.
        if (m_count % 10 == 0)
        {

        }

        //Runs periodically at 5Hz.
        if (m_count % 20 == 0)
        {

        }
    }

    /**
     * This function is called when the robot enters teleop mode.
     */
    public void teleopInit()
    {
        m_count = 0;

        System.out.println("TeleOp Initialized.");

        
    }

    /**
     * This function is called periodically (100Hz) during operator control
     */
    public void teleopPeriodic()
    {
        m_count++;
        //System.out.println("TeleOp: "+ m_count);

        //drive.tankDrive(joystick_1, joystick_2);

        //Runs periodically at 100Hz
        {

        }

        //Runs periodically at 50Hz.
        if (m_count % 2 == 0)
        {

        }

        //Runs periodically at 25Hz.
        if (m_count % 4 == 0)
        {

        }

        //Runs periodically at 20Hz.
        if (m_count % 5 == 0)
        {
            Watchdog.getInstance().feed();
            DashboardPacker.updateDashboard();

            /* crm code
            System.out.println(pot_1.getAverageValue());
            chain_rotation_motor.set(joystick_2.getY());
            */
            
            //target speed determination

            avg_speed_val = avg_speed_val*NUM_SPEED_ARRAY;
            avg_speed_val -= speed_array[speed_array_index%NUM_SPEED_ARRAY];
            avg_speed_val+=joystick_2.getY();
            avg_speed_val = avg_speed_val/NUM_SPEED_ARRAY;
            speed_array[speed_array_index%NUM_SPEED_ARRAY]=joystick_2.getY();
            speed_array_index++;

            //motor speed assignment
            front_right_motor.set(MOTOR_DIRECTION_ADJUST * avg_speed_val);
            front_left_motor.set(MOTOR_DIRECTION_ADJUST * avg_speed_val);
            //back_right_motor.set(avgval);
            //back_left_motor.set(avgval);


            /* crm code

            //find averaged direction to go to
            avg_dir_val = avg_dir_val*NUM_DIR_ARRAY;
            avg_dir_val -= dir_array[dir_array_index%NUM_DIR_ARRAY];
            avg_dir_val+=joystick_1.getX();
            avg_dir_val = avg_dir_val/NUM_DIR_ARRAY;
            dir_array[dir_array_index%NUM_DIR_ARRAY]=joystick_1.getX();
            dir_array_index++;

            //avg_dir_val = setpoint *ANGLE*
            //pot_1.getAverageValue() = actual value

            //assign x-val setpoint based on potentiometer value
            double avg_dir_setpt = 512.0 * (avg_dir_val + 1);
            double newspeed;

            if(avg_dir_setpt > pot_1.getAverageValue() + POT_DEADBAND)
                newspeed = -CRM_SPEED;
            else if (avg_dir_setpt < pot_1.getAverageValue() - POT_DEADBAND)
                newspeed = CRM_SPEED;
            else newspeed = 0;

            if(pot_1.getAverageValue() >= POT_MAX + POT_DEADBAND)
                    newspeed = -1;
            else if(pot_1.getAverageValue() <= POT_MIN - POT_DEADBAND)
                    newspeed = 1;
            else if(pot_1.getAverageValue() >= POT_MAX - POT_DEADBAND)
                newspeed = 0;
            else if(pot_1.getAverageValue() <= POT_MIN + POT_DEADBAND)
                newspeed = 0;


            System.out.println("Pot Val: " + pot_1.getAverageValue());
            System.out.println("\tTarget: " + avg_dir_setpt);

            //CRM = chain_rotation_motor
            //find averaged speed for CRM in order to not blow it out
            avg_CRM_speed_val = avg_CRM_speed_val*NUM_CRM_SPEED_ARRAY;
            avg_CRM_speed_val -= CRM_speed_array[CRM_speed_array_index%NUM_CRM_SPEED_ARRAY];
            avg_CRM_speed_val += newspeed;
            avg_CRM_speed_val = avg_CRM_speed_val/NUM_CRM_SPEED_ARRAY;
            CRM_speed_array[CRM_speed_array_index%NUM_CRM_SPEED_ARRAY] = newspeed;
            CRM_speed_array_index++;

            chain_rotation_motor.set(avg_CRM_speed_val);
             *
             * end crm code
             */

            // mbh code
            steering.setDirection(joystick_1.getX());
/*
            if (joystick_1.getTop())
            {
                if (joystick_1.getZ() > 0 )
                {
                    System.out.println("entering hard kick mode");
                    kicker.setKickMode(Kicker.kickHard);
                }
                else
                {
                    System.out.println("entering soft kick mode");
                    kicker.setKickMode(Kicker.kickSoft);
                }
            }

            if (kicker.isReady())
            {
                // light the 'armed' light.
                DriverStation.getInstance().setDigitalOut(1, true);
                // kick
                if (joystick_1.getTrigger())
                {
                    kicker.kick();
                    DriverStation.getInstance().setDigitalOut(1, false);
                }
            }
*/
            // kicker tests
            if (joystick_1.getTop() && !m_topPushed)
            {
                m_topPushed = true;
                ++m_kickerTestCycle;
                switch (m_kickerTestCycle)
                {
                    case 1:
                        System.out.println("Open the latch");
                        m_kickerTestIndex = Kicker.kTestOpenLatch;
                        break;
                    case 2:
                        System.out.println("Close the latch");
                        m_kickerTestIndex = Kicker.kTestCloseLatch;
                        break;
                    case 3:
                        System.out.println("Idle Main");
                        m_kickerTestIndex = Kicker.kTestMainIdle;
                        break;
                    case 4:
                        System.out.println("Pull Main");
                        m_kickerTestIndex = Kicker.kTestMainPull;
                        break;
                    case 5:
                        System.out.println("Push Main");
                        m_kickerTestIndex = Kicker.kTestMainPush;
                        break;
                    case 6:
                        System.out.println("open valve 3B");
                        m_kickerTestIndex = Kicker.kTestPullCharge;
                        break;
                    case 7:
                        System.out.println("open valve 3A");
                        m_kickerTestIndex = Kicker.kTestPullVent;
                        break;
                    case 8:
                        System.out.println("open valve 1B");
                        m_kickerTestIndex = Kicker.kTestPullMain;
                        break;
                    case 9:
                        System.out.println("open valve 1A");
                        m_kickerTestIndex = Kicker.kTestPushMain;
                        break;
                    case 10:
                        System.out.println("open valve 2B");
                        m_kickerTestIndex = Kicker.kTestPullLatch;
                        break;
                    case 11:
                        System.out.println("open valve 2A");
                        m_kickerTestIndex = Kicker.kTestPushLatch;
                        m_kickerTestCycle = 0;
                        break;
                }
            }
            if (!joystick_1.getTop())
                m_topPushed = false;

            boolean runKickerTest = false;
            if (joystick_1.getTrigger() && !m_triggerPulled)
            {
                runKickerTest = true;
            }

            if (runKickerTest)
            {
                kicker.test(m_kickerTestIndex);
            }

            if (!joystick_1.getTrigger())
                m_triggerPulled = false;
        }

        //Runs periodically at 10Hz.
        if (m_count % 10 == 0)
        {

        }

        //Runs periodically at 5Hz.
        if (m_count % 20 == 0)
        {
            if (cam.freshImage())
            {
                System.out.println("Got a camera image!");
            }
        }



    }

   

    /**
     * This function is called when the robot enters disabled mode.
     */
    public void disabledInit()
    {
        m_count = 0;
       // System.out.println("Disabled Init 1100 version");
    }

    /**
     * This function is called periodically (100Hz) during disabled mode.
     */
    public void disabledPeriodic()
    {
        m_count++;
       // System.out.println("Mcount =" + m_count);

        //Runs periodically at 100Hz
        {

        }
        
        //Runs periodically at 50Hz.
        if (m_count % 2 == 0)
        {

        }

        //Runs periodically at 25Hz.
        if (m_count % 4 == 0)
        {

        }

        //Runs periodically at 20Hz.
        if (m_count % 5 == 0)
        {
            DashboardPacker.updateDashboard();
            //System.out.println("Packet Sent (D)");
        }

        //Runs periodically at 10Hz.
        if (m_count % 10 == 0)
        {

        }

        //Runs periodically at 5Hz.
        if (m_count % 20 == 0)
        {

        }

        //Runs periodically at 1/5 Hz.
        if (m_count % 500 == 0)
        {
            // System.out.println("Hello, world! in Disable mode...");
        }
    }
}
