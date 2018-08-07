package simulator;

//
import java.util.ArrayDeque;
//
import java.util.Deque;
import static simulator.InterruptHandler.TIME_OUT;
import static simulator.InterruptHandler.WAKE_UP;
import static simulator.SystemCall.EXECVE;
import static simulator.SystemCall.IO_REQUEST;
import static simulator.SystemCall.MAKE_DEVICE;
import static simulator.SystemCall.TERMINATE_PROCESS;

/**
 * Concrete Kernel type
 *
 * @author Stephan Jamieson
 * @version 8/3/15
 */
public class FCFSKernel implements Kernel {

    public final Deque<ProcessControlBlock> readyQueue;

    public FCFSKernel() {
        // Set up the ready queue.
        readyQueue = new ArrayDeque<>();
    }

    protected ProcessControlBlock dispatch() {
        ProcessControlBlock toRun = readyQueue.poll(); //get the next program to be run
        ProcessControlBlock toRemove = Config.getCPU().contextSwitch(toRun); // get the current process executing on the CPU
        if (toRun != null) {
            toRun.setState(ProcessControlBlock.State.RUNNING);
        }
        return toRemove;
    }

    @Override
    public int syscall(int number, Object... varargs) {
        int result = 0;
        switch (number) {
            case MAKE_DEVICE: {
                IODevice device = new IODevice((Integer) varargs[0], (String) varargs[1]);
                Config.addDevice(device);
                break;
            }
            case EXECVE: {
                ProcessControlBlock pcb = FCFSKernel.loadProgram((String) varargs[0]);
                if (pcb != null) {
                    readyQueue.offer(pcb);// Now add to end of ready queue.
                    if (Config.getCPU().isIdle()) {
                        dispatch();// If CPU idle then call dispatch.
                    } else {
                        result = -1;
                    }
                    break;
                }
            }
            case IO_REQUEST: {
                ProcessControlBlock pcb = Config.getCPU().getCurrentProcess();
                IODevice dev = Config.getDevice((Integer) (varargs[0]));
                dev.requestIO((Integer) (varargs[1]), pcb, this);
                pcb.setState(ProcessControlBlock.State.WAITING);
                dispatch();
                break;
            }
            case TERMINATE_PROCESS: {
                ProcessControlBlock pcb = Config.getCPU().getCurrentProcess();
                pcb.setState(ProcessControlBlock.State.TERMINATED);
                dispatch();
                break;
            }
            default:
                result = -1;
        }
        return result;
    }

    @Override
    public void interrupt(int interruptType, Object... varargs) {
        switch (interruptType) {
            case TIME_OUT: {
                throw new IllegalArgumentException("Within this class");
            }
            case WAKE_UP: {
                ProcessControlBlock pcb = (Process) varargs[1];
                pcb.setState(ProcessControlBlock.State.READY);
                readyQueue.offer(pcb);
                if (Config.getCPU().isIdle()) {
                    dispatch();
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Method not supported within this class");
        }
    }

    protected static ProcessControlBlock loadProgram(String filename) {
        return Process.loadProgram(filename);
    }
}
