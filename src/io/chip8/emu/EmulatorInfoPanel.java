package io.chip8.emu;

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
    public JButton stepButton;

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

        JPanel inputContainer = new JPanel();
        inputContainer.setLayout(new GridLayout(3, 2, 2, 2));
        JButton button1 = new JButton();
        button1.setText("1");

        JButton button2 = new JButton();
        button2.setText("2");

        JButton button3 = new JButton();
        button3.setText("3");

        JButton button4 = new JButton();
        button4.setText("4");

        JButton button5 = new JButton();
        button5.setText("5");

        JButton button6 = new JButton();
        button6.setText("6");

        inputContainer.add(button1);
        inputContainer.add(button2);
        inputContainer.add(button3);
        inputContainer.add(button4);
        inputContainer.add(button5);
        inputContainer.add(button6);

        JPanel stepContainer = new JPanel();
        stepContainer.setLayout(new GridLayout(1, 1));
        this.stepButton = new JButton();
        stepButton.setText("next");

        stepContainer.add(stepButton);

        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new GridLayout(2, 1));

        buttonContainer.add(stepContainer);
        buttonContainer.add(inputContainer);


        this.canvasPanel.add("East", buttonContainer);



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
