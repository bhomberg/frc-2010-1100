/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package team1100.season2010.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;

/**
 * Class representing the robot kicker subsystem
 * @author mark
 */
public class Kicker extends Thread
{
    /**
     * constructor
     */
    public Kicker()
    {
        m_hardMode = false;
        m_ready = false;
        m_mainPushValve = new DigitalOutput(kMainPushChannel);
        m_mainPullValve = new DigitalOutput(kMainPullChannel);
        m_latchPushValve = new DigitalOutput(kLatchPushChannel);
        m_latchPullValve = new DigitalOutput(kLatchPullChannel);
        m_mainPullVentValve = new DigitalOutput(kMainPullVentChannel);
        m_mainPullChargeValve = new DigitalOutput(kMainPullEnableChannel);

        m_mainPushedSensor = new DigitalInput(kMainPushedSensorChannel);
        m_mainPulledSensor = new DigitalInput(kMainPulledSensorChannel);
        m_latchPushedSensor = new  DigitalInput(kLatchPushedSensorChannel);
        m_latchPulledSensor = new DigitalInput(kLatchPulledSensorChannel);

        Compressor.instance().start();
        this.start();
    }

    /**
     * constant for hard kick mode
     */
    public final static int kickHard = 0;
    /**
     * constant for soft kick mode
     */
    public final static int kickSoft = 1;

    /**
     * adjust the kicker for short kicks or long kicks.
     * @param hardOrSoft use constant kickHard for long kicks, or kickSoft
     * for short kicks.
     */
    public void setKickMode(int hardOrSoft)
    {

        boolean tmpHardMode = (hardOrSoft == kickHard);

        // if nothing changes, we're done.
        if (m_hardMode == tmpHardMode) return;

        m_hardMode = tmpHardMode;
        // if we're not charged and ready to kick, the hard/soft
        // valve won't actuate, because there's no pressure from the 
        // main pull valve. That's OK, we'll properly set the valve, based
        // on m_hardMode, after the kicker is set.
        // There's a race between changing the valves here while
        // the cycling engine is setting them according to the previous mode.
        // We could open both A and B valves at once, leaving the valve
        // in an indeterminate state. The subsequent kick could have the
        // wrong force. It's unlikely that we'll hit this problem; ignore it.
        // If the kick mode is changed while the kicker is ready,
        // there is no risk of a race.
        if (m_hardMode)
        {
            openValve(m_mainPullChargeValve);
        }
        else
        {
            openValve(m_mainPullVentValve);
        }
    }

    /**
     * start the kick cycle, operating the kicker. This won't do anything
     * if the kicker is not ready.
     */
    public void kick()
    {
        if (m_ready)
            notify();
    }

    /**
     * test if the kicker is ready to kick.
     * @return true if the kicker is ready, false otherwise.
     */
    public boolean isReady() { return m_ready; }

    private boolean m_hardMode = false;
    private boolean m_ready = false;

    private DigitalOutput m_mainPushValve;
    private DigitalOutput m_mainPullValve;
    private DigitalOutput m_latchPushValve;
    private DigitalOutput m_latchPullValve;
    private DigitalOutput m_mainPullVentValve;
    private DigitalOutput m_mainPullChargeValve;

    private DigitalInput m_mainPushedSensor;
    private DigitalInput m_mainPulledSensor;
    private DigitalInput m_latchPushedSensor;
    private DigitalInput m_latchPulledSensor;

    private static final int kMainPushChannel = 1;
    private static final int kMainPullChannel = 2;
    private static final int kMainPullVentChannel = 5;
    private static final int kMainPullEnableChannel = 6;
    private static final int kLatchPushChannel = 3;
    private static final int kLatchPullChannel = 4;
    private static final int kMainPushedSensorChannel = 1;
    private static final int kMainPulledSensorChannel = 2;
    private static final int kLatchPushedSensorChannel = 3;
    private static final int kLatchPulledSensorChannel = 4;

    private static final double kValveOpenPulseWidthS = 0.5;

    /*
     * override Thread.run(). This is called when the thread
     * is started.
     */
    public void run()
    {
        while (true)
        {
            // prepare to kick
            prepareToKick();

            // wait while the compressor gets ready. Before we say
            // we're ready, be sure there's enough pressure to withdraw
            // the foot after the kick.
            if (!Compressor.instance().isPressureReady())
            {
                Sleep(1000);
                continue;
            }

            // set ready flag
            m_ready = true;

            // wait for kick signal
            try
            {
                wait();
            }
            catch (InterruptedException ex)
            {
                System.out.println("*** Interrupted while waiting for kick ***");
                // don't kick
                m_ready = false;
                continue;
            }

            m_ready = false;

            // kick!
            openTheLatch();
        }
    }

    /**
     * cycle the latch and charge the kicker according to the mode.
     */
    private void prepareToKick()
    {
        openTheLatch();
        pushWithMainCylinder();
        closeTheLatch();
        if (m_hardMode)
            pullWithMainCylinder();
        else
            idleMainCylinder();
    }

    private void openTheLatch()
    {
        if (!isLatchOpen())
            openValve(m_latchPushValve);

        while (!isLatchOpen())
        {
            Sleep(50);
        }
    }

    private void closeTheLatch()
    {
        if (!isLatchClosed())
            openValve(m_latchPullValve);

        while (!isLatchClosed())
        {
            Sleep(50);
        }
    }

    private void pushWithMainCylinder()
    {
        openValve(m_mainPushValve);
        while (!isMainExtended())
        {
            Sleep(100);
        }
    }

    private void pullWithMainCylinder()
    {
        openValve(m_mainPullValve);
        openValve(m_mainPullChargeValve);
        // don't wait for it to get there; we're likely latched
    }

    private void idleMainCylinder()
    {
        openValve(m_mainPullValve);
        openValve(m_mainPullVentValve);
    }

    private boolean isLatchOpen()
    {
        return m_latchPushedSensor.get();
    }

    private boolean isLatchClosed()
    {
        return m_latchPulledSensor.get();
    }

    private boolean isMainExtended()
    {
        return m_mainPushedSensor.get();
    }

    private boolean isMainWithdrawn()
    {
        return m_mainPulledSensor.get();
    }

    private void openValve(DigitalOutput digout)
    {
        if (!digout.isPulsing())
            digout.pulse(kValveOpenPulseWidthS);
    }

    /**
     * sleep that doesn't throw. Just returns if the
     * sleep is interrupted.
     * @param ms milleseconds to delay
     */
    private void Sleep(long ms)
    {
        try
        {
            sleep(ms);
        }
        catch (InterruptedException ex)
        {
        }
    }
}
