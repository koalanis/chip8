package io.chip8.emu;


import io.chip8.emu.CPU;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kaleb on 1/7/2017.
 */
public class CPUCanvas extends JPanel {

    public CPU cpu;
    private int pixelSize;


    public CPUCanvas(CPU cpu) {
        this.cpu = cpu;
        pixelSize = 10;
        this.setSize(CPU.WIDTH*pixelSize, CPU.HEIGHT*pixelSize);
    }

    @Override
    public void paintComponent(Graphics g) {
        for (int i = 0; i < CPU.WIDTH; i++) {
            for (int j = 0; j < CPU.HEIGHT; j++) {
                if(cpu.getPixelAt(i,j) == 0) {
                    g.setColor(Color.BLACK);
                } else {
                    g.setColor(Color.WHITE);
                }

                g.fillRect(pixelSize*i, pixelSize*j, pixelSize, pixelSize);
            }
        }
    }
}
