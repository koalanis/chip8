package io.chip8;

import io.chip8.emu.CPU;

import javax.swing.*;
import java.awt.*;

public class EmulatorInfoPanel extends JTabbedPane {

    private JPanel canvasPanel;
    public CPUCanvas canvas;
    public CPU cpu;
    private int pixelSize;
    private  JTextArea textArea1;
    private  JTextArea textArea2;
    private StringBuilder instructionList;


    private JScrollPane instructions;
    private  JScrollPane registers;


    public EmulatorInfoPanel(CPU cpu) {
        this.cpu = cpu;
        this.cpu.initialize();
        this.pixelSize = 12;
        this.textArea1 = new JTextArea(5, 20);
        this.instructions = new JScrollPane(textArea1);
        this.instructionList = new StringBuilder();
        this.canvas = new CPUCanvas(cpu);

        this.canvasPanel = new JPanel();
        this.canvasPanel.setLayout(new BorderLayout());
        this.canvasPanel.add("Center", this.canvas);
        this.canvasPanel.setSize(CPU.WIDTH*pixelSize, CPU.HEIGHT*pixelSize);
        this.setSize(CPU.WIDTH*pixelSize, CPU.HEIGHT*pixelSize);
        textArea1.setEditable(false);
        textArea1.setText("Wassup Boi");

        this.textArea2 = new JTextArea(5, 20);
        this.registers = new JScrollPane(textArea2);
        textArea2.setEditable(false);
        textArea2.setText("Wassup Son");

        this.addTab("Tab 0", null, this.canvasPanel,
                "Canvas");

        this.addTab("Tab 1", null, instructions,
                "Instructions");

        this.addTab("Tab 2", null, registers,
                "Registers");


    }

    public void updateRegisterInfo() {
        this.textArea2.setText(this.cpu.toString());
    }

    public void addInstruction(short inst) {
        this.instructionList.append(Integer.toHexString(inst)).append("\n");
        this.textArea1.setText(this.instructionList.toString());
    }

}
