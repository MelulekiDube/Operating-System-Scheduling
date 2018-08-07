/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Meluleki Dube
 */
public class Process implements ProcessControlBlock {

    private final int PID;
    private static int no =0;
    private int priority;
    private final String ProgramName;
    private final List<Instruction> instructionList;
    private int programCounter;
    private State state;
    
    public Process(int priority, String ProgramName, List<Instruction> instructionList) {
        no++;
        this.PID =no;
        this.priority = priority;
        this.ProgramName = ProgramName;
        this.instructionList = instructionList;
        this.programCounter = 0;
        this.state = State.READY;
    }

    public static ProcessControlBlock loadProgram(String fileName) {
        List<Instruction> instructions = new ArrayList<>();
        File file = new File(fileName);
        try {
            Scanner in = new Scanner(file);
            instructions = new ArrayList<>();
            String[] content;
            while (in.hasNextLine()) {
                String line = in.nextLine();
                content = line.split(" ");
                Instruction ins = null;
                if (content[0].equals("IO")) {
                    int duration = Integer.parseInt(content[1]);
                    int deviceId = Integer.parseInt(content[2]);
                    ins = new IOInstruction(duration, deviceId);
                }

                if (content[0].equals("CPU")) {
                    int duration = Integer.parseInt(content[1]);
                    ins = new CPUInstruction(duration);
                }

                instructions.add(ins);
            }

        } catch (FileNotFoundException ex) {
            System.out.println("File Not Found");
        }
        return new Process(0, fileName, instructions);
    }

    @Override
    public int getPID() {
        return PID;
    }

    @Override
    public String getProgramName() {
        return ProgramName;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public int setPriority(int value) {
        priority = value;
        return value;
    }

    @Override
    public Instruction getInstruction() {
        return instructionList.get(programCounter);
    }

    @Override
    public boolean hasNextInstruction() {
        return (programCounter < instructionList.size() - 1);
    }

    @Override
    public void nextInstruction() {
        programCounter++;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "process(" + "pid=" + PID + ", state=" + state + ", name=\"" + ProgramName+"\")";

    }
}
