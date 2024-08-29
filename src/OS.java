
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition; //Note that the 'notifyAll' method or similar polling mechanism MUST not be used

// IMPORTANT:
//
//'Thread safe' and 'synchronized' classes (e.g. those in java.util.concurrent) other than the two imported above MUST not be used.
//
//You MUST not use the keyword 'synchronized', or any other `thread safe` classes or mechanisms  
//or any delays or 'busy waiting' (spin lock) methods. Furthermore, you must not use any delays such as Thread.sleep().

//However, you may import non-tread safe classes e.g.:
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;

//Your OS class must handle exceptions locally i.e. it must not explicitly 'throw' exceptions 
//otherwise the compilation with the Test classes will fail!!!

public class OS implements OS_sim_interface {
	private int availableProcessors = 0;
	private int nextPid = 0;
    private final ReentrantLock lock = new ReentrantLock();
    private final HashMap<Integer, Process> processesMap = new HashMap<>();
    private final ArrayList<Process> runningProcessesList = new ArrayList<>();
    private final Queue<Process> readyQueue = new LinkedList<>();

	@Override
	public void set_number_of_processors(int nProcessors) {
        lock.lock();
        try {
            this.availableProcessors = nProcessors;
        } finally {
            lock.unlock();
        }	
	}

	/**
	 *Registers a process with the OS with a given priority and returns a unique process identifier (PID)
	 */
	@Override
	public int reg(int priority) {
		lock.lock();
        try {
            int pid = nextPid++;
            Process newProcess = new Process(priority);
            processesMap.put(pid, newProcess);
            return pid;
        } finally {
            lock.unlock();
        }
	}

	/**
	 *Starts the execution of the process with the given PID
	 *Attempt to allocate a processor to the process. If no processors are available, the process waits in its priority queue
	 */
	@Override
	public void start(int ID) {
		lock.lock();
        try {
			// get the process with the given ID from the processesMap
            Process currentProcess = processesMap.get(ID);
			// checks if the currentProcess is not null
            if (currentProcess != null) {
                // After allocating a processor to a process, this isAvaliableAndAllocateProcessor method will return true, which will be converted to false by the ! operator, and the following code below will not be executed.
                if (!isAvailableAndAllocateProcessor(currentProcess)) {
                    // If there is no processor available, then the calling process must be suspended by placing it on the end of its ready queue, and awaiting on a condition variable.
                    readyQueue.offer(currentProcess);
                    waitForProcessorAllocation(currentProcess);
                }
            }
        } finally {
            lock.unlock();
        }	
	}

	/**
	 *Indicates that the process with the given PID is willing to relinquish control of the processor
	 *Determine if the process can continue execution or if it should yield to a higher-priority process. This involves checking if there are any higher-priority processes 
	 *waiting and managing the processors accordingly.
	 */
	@Override
	public void schedule(int ID) {
        lock.lock();
        try {
            Process currentProcess = processesMap.get(ID);
            if (currentProcess != null) {
                if (runningProcessesList.contains(currentProcess)){
                //If the calling process has higher priority than all other ready processes, then it will continue to run.
                    if(RQhasHigherPriority(currentProcess)){
                        giveUpProcessor(currentProcess);
                        signalNextReadyProcess();
                        start(ID);
                    }
                }
                
            }
        } finally {
            lock.unlock();
        }
	}

	/**
	 *Indicates that the process with the given PID no longer requires the processor
	 *Release the processor and adjust the scheduling queue, allowing waiting processes to proceed.
	 */
	@Override
	public void terminate(int ID) {
        lock.lock();
        try {
            Process currentProcess = processesMap.get(ID);
            if (currentProcess != null) {
                giveUpProcessor(currentProcess);
                signalNextReadyProcess();
            }
        } finally {
            lock.unlock();
        }
	}

    private class Process{
        private final Condition conditionMet = lock.newCondition();
        final int priority;

        Process(int priority) {
            this.priority = priority;
        }

        private int getPriority() {
            return this.priority;
        }

        private Condition getCondition() {
            return this.conditionMet;
        }
    }

    // Allocate a processor to the given process if available
    private boolean isAvailableAndAllocateProcessor(Process process) {
        lock.lock();
        try {
			// check if there are available processors
            if (availableProcessors > 0) {
				// subtract one from the available processors
                availableProcessors--;
				// remove the process from the ready queue if it is present and add the process to the running processes list
                readyQueue.remove(process);
                runningProcessesList.add(process);
				// return true if the process was successfully allocated a processor
                return true;
            }
			// return false if there are no available processors
            return false;
        } finally {
            lock.unlock();
        }
    }
	
    // Wait for a processor to become available for the given process
    private void waitForProcessorAllocation(Process waitingProcess) {
        lock.lock();
        try {
            // get the condition variable of the waiting process
            Condition readyCondition = waitingProcess.getCondition();
            // check if the condition variable is not null. 
            if (readyCondition != null) {
                //continue looping as long as the waitingProcess is not found in the runningProcessesList.
                //If the waitingProcess is found in the runningProcessesList, then it will return true which will be converted to false by the ! operator, and the following code below will not be executed.
                while (!runningProcessesList.contains(waitingProcess)) {
                    // Once the waitingProcess is added to the runningProcessesList, the condition is met, the while loop ends. 
                    // The condition being met is that the waitingProcess has been allocated a processor and is now running.
                    // wait for the condition to be met
                    readyCondition.await();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    // Give up the processor allocated to the given process
    private void giveUpProcessor(Process process) {
        lock.lock();
        try {
            if (runningProcessesList.contains(process)) {
                availableProcessors++;
                runningProcessesList.remove(process);
            }
        } finally {
            lock.unlock();
        }
    }

    // Ensures that the process with the highest priority in the readyQueue is allocated a processor and signaled to proceed for execution
    private void signalNextReadyProcess() {
		lock.lock();
    try {
        int highestPriorityProcessNum = Integer.MAX_VALUE;
        Process nextProcess = null;

        if (!readyQueue.isEmpty()) {
           
                 // Find the process with the highest priority
                for (Process process : readyQueue) {
                    if (process.getPriority() < highestPriorityProcessNum) {
                        highestPriorityProcessNum = process.getPriority();
                        nextProcess = process;
                    }
                }   
            
                //the highest priority ready process will be allocated to a processor and signalled to proceed
                if (nextProcess != null) {
                    if (isAvailableAndAllocateProcessor(nextProcess)) {
                        nextProcess.getCondition().signal();
                    }
                }
        }
        
    } finally {
        lock.unlock();
    }
    }

    /**
     * Check if there is a process in the ready queue with higher or same priority than the calling process
     * @param callingProcess The process ID of the calling function
     * @return true there is a process in the ready queue with higher or same priority than the calling process
     */
    private boolean RQhasHigherPriority(Process callingProcess) {
        lock.lock();
        try {
           
            int callingProcessPriority = callingProcess.getPriority();
            for (Process process : readyQueue) {
                // Check if there is a process in the ready queue with higher or same priority than the calling process
                if (process.getPriority() <= callingProcessPriority) {
                    // If there is a process in the ready queue with higher or same priority than the calling process, return true
                    return true;
                }
            }
            return false;
        
        } finally {
            lock.unlock();
        }
    }
	
}


