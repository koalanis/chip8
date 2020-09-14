package io.chip8;

import io.chip8.bitlogic.BinaryLogic;
import io.chip8.emu.CPU;
import io.chip8.emu.Emulator;
import io.chip8.emu.EmulatorInfoPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Created by kaleb on 12/31/2016.
 */
public class Main extends JFrame {


//      http://devernay.free.fr/hacks/chip8/C8TECH10.HTM#2.4

    public Main() throws InterruptedException, IOException {

        System.out.println("Hello World !!!!!");
        String workingdir = System.getProperty("user.dir");


        String dirName = "\\res\\c8games\\";
        String romName = "test_opcode.ch8";

        // This will reference one line at a time
        String line = null;

        Path path = Paths.get(workingdir+dirName+romName);
        byte[] bin = Files.readAllBytes(path);
        System.out.println(Arrays.toString(bin));
        System.out.println(bin.length);

        String program = Emulator.KALEID;
        byte[] bp = BinaryLogic.hexStringToByteArray(program);
        System.out.println(bp.length);
        System.out.println(program.length());

        if(program.length() != 0) {
            EmulatorInfoPanel infoPanel = new EmulatorInfoPanel( new CPU(bin));

            setTitle("Chip 8");
            this.setSize(infoPanel.getSize());
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            this.add(infoPanel);
            setLocationRelativeTo(null);
            setVisible(true);

            infoPanel.stepButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    emulationStep(infoPanel);
                }
            });



//            int target = 5000;
//            for(;;) {
//                emulationStep(infoPanel);
//            }
        }

    }


    public void emulationStep(EmulatorInfoPanel infoPanel) {
        short instr = infoPanel.cpu.emulateCycle();
        infoPanel.addInstruction(instr);
        infoPanel.updateRegisterInfo();
        if(infoPanel.cpu.drawFlag) {
            infoPanel.repaint();
        }
    }


    public static void main(String[] args) throws InterruptedException, IOException {
        new Main();
    }

}
