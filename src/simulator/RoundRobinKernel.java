package simulator;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Meluleki Dube
 */
public class RoundRobinKernel extends FCFSKernel {

    int timeSlice;

    public RoundRobinKernel(int ts) {
        super();
        this.timeSlice = ts;
    }

    @Override
    protected ProcessControlBlock dispatch() {
        ProcessControlBlock toRunBlock = readyQueue.poll();
        ProcessControlBlock toRemoveBlock = Config.getCPU().contextSwitch(toRunBlock);
        if (toRunBlock != null) {
            Config.getSimulationClock().scheduleInterrupt(timeSlice, this, toRunBlock.getPID());
            toRunBlock.setState(ProcessControlBlock.State.RUNNING);
        }
        return toRemoveBlock;
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
                ProcessControlBlock pcb = RoundRobinKernel.loadProgram((String) varargs[0]);
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
                Config.getSystemTimer().cancelInterrupt(pcb.getPID());
                IODevice dev = Config.getDevice((Integer) (varargs[0]));
                dev.requestIO((Integer) (varargs[1]), pcb, this);
                pcb.setState(ProcessControlBlock.State.WAITING);
                dispatch();
                break;
            }
            case TERMINATE_PROCESS: {
                ProcessControlBlock pcb = Config.getCPU().getCurrentProcess();
                Config.getSystemTimer().cancelInterrupt(pcb.getPID());
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
                ProcessControlBlock pcb = Config.getCPU().getCurrentProcess(); //remove the already in front event
                readyQueue.offer(pcb); // can change to add () // put the previously in front event at the back
                pcb.setState(ProcessControlBlock.State.READY);
                if (readyQueue.size() == 1) {
                    pcb.setState(ProcessControlBlock.State.RUNNING);
                }
                dispatch();
                break;
            }
            case WAKE_UP: {
                super.interrupt(interruptType, varargs);
            }
        }
    }

}
