# F29OC Concurrency Coursework (2023-24)

## Overview

This project involves the implementation of a simulation for a non-pre-emptive operating system as part of the F29OC Concurrency course. The primary goal is to assess the ability to develop thread-safe software using concurrency principles and methods taught in the course.

## Key Objectives

- **Thread-Safety**: The simulation must be thread-safe, ensuring consistent results across multiple runs.
- **Concurrency**: Proper usage of concurrency tools like extrinsic monitors (`ReentrantLock`, `Condition`) to handle the simulation of processes.
- **Simulation**: Implement a non-pre-emptive scheduling system for user-defined processes, organized into ready queues based on priority.

## Project Structure

The project is structured around a GitLab repository with the following key files:

- **`OS_sim_interface.java`**: Interface defining the methods your `OS.java` class must implement.
- **`OS.java`**: The main class where the core functionality of the OS, including processes scheduling, processor allocation and termination simulation logic will be developed. It must implement `OS_sim_interface` and adhere to the user requirements. 
- **`Tests.java`**: Contains example tests. This is where you will develop additional tests to ensure your implementation meets the specified requirements.
- **`Main.java`**: Entry point for the program, used to control and run tests during development.

## Usage

To run the tests, execute the **`Main.java`** which allows selective execution of the provided tests.

## User Requirements (UR)

Your `OS.java` implementation will be tested against the following user requirements:

1. **UR1**: Implement a registration system for processes returning consecutive process IDs (`pid`).
2. **UR2**: Simulate a single process on a single processor with a single priority queue.
3. **UR3**: Handle multiple processes on a single processor with a single priority queue.
4. **UR4**: Extend the simulation to multiple processors with a single priority queue.
5. **UR5**: Handle multiple processes and multiple priority queues on a single processor.
6. **UR6**: Simulate multiple processes, multiple processors, and multiple priority queues.

## Constraints and Requirements

### Programming Constraints

- **Extrinsic Monitors**: Use `ReentrantLock` and `Condition` for synchronization. Avoid `signalAll`, `synchronized`, `Thread.Sleep()`, or any other blocking/synchronization techniques that may lead to inefficiencies.
- **No Additional Libraries**: Only use classes from Java SE17 and those specified in the coursework instructions.
- **Timely Execution**: Ensure that the implementation is efficient and that tests run within defined time limits.

## Marking Breakdown

Marks will be awarded based on the correctness and thread-safety of your implementation, as demonstrated by running the provided tests. Each test will be run multiple times to ensure consistency.

- **GitLab Fork**: 3%
- **User Requirements Tests (UR1 - UR6)**: 2% per test, totaling up to 25%

## Testing and Evaluation

- The project underwent rigorous testing with 88 test runs executed.
- All test cases passed successfully, demonstrating the robustness of the implementation.
- Final Marks = 25/25

## Plagiarism Policy

Code similarity will be checked through GitLab's plagiarism detection tools. Sharing code or using public domain code, including AI-generated code, will be considered plagiarism and will result in penalties as per university regulations.

## License

This project is licensed under Heriot-Watt University's student coursework guidelines. Unauthorized distribution or use of this code is prohibited.

